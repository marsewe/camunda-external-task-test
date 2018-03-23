package org.camunda.bpm.spring.boot.example.autodeployment;

import org.camunda.bpm.engine.ExternalTaskService;
import org.camunda.bpm.engine.externaltask.LockedExternalTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class ExternalTaskPoller {

    private final Logger log = LoggerFactory.getLogger(ExternalTaskPoller.class);

    private final ThreadPoolExecutor noQueueExecutorService;
    private final String topicName = "say_hello";

    @Inject
    private ExternalTaskService externalTaskService;

    protected ExternalTaskPoller() {

        this.noQueueExecutorService = new ThreadPoolExecutor(15, 15, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(100));
    }

    @Scheduled(
            fixedRate = 100
    )
    public void poll() {
        List<LockedExternalTask> tasks = externalTaskService.fetchAndLock(10, UUID.randomUUID().toString())
                .topic(topicName, 600000)
                .execute();

        tasks.forEach(externalTask -> noQueueExecutorService.execute(() -> process(externalTask)));
    }


    public void executeTask(LockedExternalTask externalTask) throws Exception {
        log.info("Hello task {}", externalTask.getId());
        Thread.sleep(10); // do some work
    }



    private void process(LockedExternalTask task) {
        try {
            executeTask(task);
            externalTaskService.complete(task.getId(), task.getWorkerId());
            log.info("Completed external task: {}, topic: {}", task.getId(), topicName);

        } catch (Exception e) {
            externalTaskService.handleFailure(
                    task.getId(),
                    task.getWorkerId(),
                    e.getMessage(),
                    getRetryCount(task), 10000);
            log.error("Failed to process external task: {}, topic: {}", task.getId(), topicName, e);
        }
    }


    /**
     * decrement the number of retries (because at 0 we fail) or else use default
     */
    private int getRetryCount(LockedExternalTask externalTask) {
        return Optional.ofNullable(externalTask.getRetries())
                .map(existingRetries -> existingRetries - 1)
                .orElse(2);
    }




}
