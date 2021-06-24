package com.alibaba.cloud.examples.configurer;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class MultiDataSourceConfigurer {

    @Bean(name = "dataSourceOne")
    @Qualifier("dataSourceOne")
    @ConfigurationProperties("spring.datasource.druid.one")
    public DataSource dataSourceOne(){
        System.out.println("===dataSourceOne=");
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = "dataSourceTwo")
    @Qualifier("dataSourceTwo")
    @ConfigurationProperties("spring.datasource.druid.two")
    public DataSource dataSourceTwo(){
        System.out.println("===dataSourceTwo=");
        return DruidDataSourceBuilder.create().build();
    }

}
