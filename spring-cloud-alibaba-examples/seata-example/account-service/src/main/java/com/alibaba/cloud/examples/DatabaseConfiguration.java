/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.cloud.examples;

import javax.annotation.Resource;
import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author xiaojing
 */
@Configuration
public class DatabaseConfiguration {

    @Autowired
    private Environment environment;//获取配置数据，例如：environment.getProperty("server.port")

//    @Autowired
////    @Qualifier("dataSourceOne")
//    private DataSource dataSourceOne;

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSource storageDataSource() {
        return new DruidDataSource();
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {

        System.out.println("====spring.jpa.database=====" + environment.getProperty("spring.jpa.database") + "============");
        System.out.println("====spring.datasource.url=====" + environment.getProperty("spring.datasource.url") + "============");

        //System.out.println(dataSource);
//        try {
//            //获取数据库连接url：dataSourceOne.getConnection().getMetaData().getURL()
//            System.out.println("===dataSource=====" + dataSource.getConnection().getMetaData().getURL());
//            System.out.println("===dataSourceOne=====" + dataSourceOne.getConnection().getMetaData().getURL());
////            System.out.println("====" + dataSourceOne.getUsername());
////            System.out.println("====" + dataSourceOne.getConnection().getMetaData().getPassword());
////            System.out.println("====" + dataSourceOne.getConnection().getMetaData().getDriverClassName());
////            System.out.println("====" + dataSourceOne.getConnection().getMetaData().getInitialSize());
////            System.out.println("====" + dataSourceOne.getConnection().getMetaData().getMaxActive());
//        } catch (SQLException e) {
//
//        }

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        jdbcTemplate.update("delete from account_tbl where user_id = 'U100001'");
        jdbcTemplate.update(
                "insert into account_tbl(user_id, money) values ('U100001', 10000)");

        return jdbcTemplate;
    }

}
