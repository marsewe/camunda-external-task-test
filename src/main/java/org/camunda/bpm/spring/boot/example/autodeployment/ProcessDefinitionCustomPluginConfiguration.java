package org.camunda.bpm.spring.boot.example.autodeployment;

import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.jobexecutor.JobExecutor;
import org.camunda.bpm.spring.boot.starter.configuration.impl.AbstractCamundaConfiguration;
import org.springframework.beans.factory.annotation.Autowired;

public class ProcessDefinitionCustomPluginConfiguration extends AbstractCamundaConfiguration {


    @Autowired
    private JobExecutor jobExecutor;


    @Override
    public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
        jobExecutor.setMaxJobsPerAcquisition(10);
        jobExecutor.setLockTimeInMillis(3000);
    }

}
