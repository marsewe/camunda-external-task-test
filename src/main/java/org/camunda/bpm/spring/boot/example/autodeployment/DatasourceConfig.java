package org.camunda.bpm.spring.boot.example.autodeployment;


import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DatasourceConfig {


    @Bean(name="camundaBpmDataSource")
    @ConfigurationProperties(prefix="spring.datasources.primary")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }


}
