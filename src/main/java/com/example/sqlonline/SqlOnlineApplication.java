package com.example.sqlonline;

import com.example.sqlonline.utils.sql.dbservice.DbManagementServiceConfigurations;
import com.example.sqlonline.utils.sql.dbservice.VersionRoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@SpringBootApplication
@EnableConfigurationProperties(DbManagementServiceConfigurations.class)
public class SqlOnlineApplication {
    @Autowired
    DbManagementServiceConfigurations databaseConfigurations;

    @Bean
    public DataSource dataSource() {
        VersionRoutingDataSource dataSource = new VersionRoutingDataSource();
        dataSource.setTargetDataSources(databaseConfigurations.createTargetDataSources());
        return dataSource;
    }

    public static void main(String[] args) {
        SpringApplication.run(SqlOnlineApplication.class, args);
    }
}
