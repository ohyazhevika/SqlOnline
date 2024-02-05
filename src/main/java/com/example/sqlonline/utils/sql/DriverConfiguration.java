package com.example.sqlonline.utils.sql;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class DriverConfiguration {

    @Bean
    List<DriverJarInfo> configure() {
        List<DriverJarInfo> driverInfoList = new ArrayList<>();
        DriverJarInfo jar1 = new DriverJarInfo(
                "soqol1", "ru.relex.soqol.jdbc.Driver", "D:\\soqol\\bin\\soqol-jdbc-2.0.1.jar"
        );
        driverInfoList.add(jar1);
        return driverInfoList;
    }

}
