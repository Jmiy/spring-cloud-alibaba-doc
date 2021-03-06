/*
 * Copyright 2015-2021 the original author or authors.
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
package com.alibaba.cloud.examples.customer;

import com.alibaba.cloud.examples.DataInitializer;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Configuration for the {@link} slice of the system. A dedicated {@link},
 * {@link} and {@link}. Note that there could of course be some deduplication
 * with {@link }. I just decided to keep it to focus on the
 * sepeartion of the two. Also, some overlaps might not even occur in real world scenarios (whether to create DDl or the
 * like).
 *
 * @author Oliver Gierke
 */
@Configuration
@EnableJpaRepositories(entityManagerFactoryRef = "customerEntityManagerFactory",
		transactionManagerRef = "customerTransactionManager")
class CustomerConfig {

    @Autowired
	@Qualifier("dataSourceOne")
    private DataSource dataSourceOne;

	@Autowired
	private Environment environment;//??????????????????????????????environment.getProperty("server.port")

	@Bean
	PlatformTransactionManager customerTransactionManager() {
		return new JpaTransactionManager(customerEntityManagerFactory().getObject());
	}

	@Bean
	LocalContainerEntityManagerFactoryBean customerEntityManagerFactory() {

		HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
		jpaVendorAdapter.setGenerateDdl(true);//?????????????????? ????????????  true??????  false??????
		jpaVendorAdapter.setDatabasePlatform(environment.getProperty("spring.jpa.database-platform","org.hibernate.dialect.MySQL5InnoDBDialect"));//
		jpaVendorAdapter.setShowSql(environment.getProperty("spring.jpa.show-sql",Boolean.class,false));//????????????sql?????? true??????  false??????

		LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();

		//factoryBean.setDataSource(customerDataSource());

        factoryBean.setDataSource(dataSourceOne);
		factoryBean.setJpaVendorAdapter(jpaVendorAdapter);
		factoryBean.setPackagesToScan(CustomerConfig.class.getPackage().getName());
        factoryBean.setJpaProperties(hibernateProperties());

		return factoryBean;
	}

    Properties hibernateProperties() {

        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", environment.getProperty("spring.jpa.properties.hibernate.dialect"));

		/**
		 *https://spring.io/guides/gs/accessing-data-mysql/
		 * ???????????? model ???????????????????????????????????? ??????????????? jpaVendorAdapter.setGenerateDdl(true);//?????????????????? ????????????  true??????  false??????
		 * ?????????
		 * none???The default for MySQL. No change is made to the database structure.
		 * create???Creates the database every time but does not drop it on close.
		 * create-drop???Creates the database and drops it when SessionFactory closes
		 * ???????????????????????????none ????????????????????????create-drop
		 */
        //properties.setProperty("hibernate.hbm2ddl.auto", environment.getProperty("spring.jpa.properties.hibernate.hbm2ddl.auto","none"));

		/**
		 * ??????????????? hibernate.id true??????????????????????????????
		 * CREATE TABLE `hibernate_sequence` (
		 *   `next_val` bigint(20) DEFAULT NULL
		 * ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
		 * ?????????????????????????????????????????????id
		 */
		properties.setProperty("hibernate.id.new_generator_mappings", environment.getProperty("spring.jpa.properties.hibernate.id.new_generator_mappings","false"));

        return properties;
    }

//	@Bean
//	public DataSource dataSource() {
//
////		DriverManagerDataSource dataSource = new DriverManagerDataSource();
////		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
////
////		// dataSource.setUrl("jdbc:mysql://localhost:3306/wallet?createDatabaseIfNotExist=true");
////		dataSource.setUrl("jdbc:mysql://localhost:3306/wallet");
////		dataSource.setUsername("testuser");
////		dataSource.setPassword("testpassword");
//	}

//	@Bean
//	DataSource customerDataSource() {
//
////		try{
////			//?????????????????????url???dataSourceOne.getConnection().getMetaData().getURL()
////			System.out.println(dataSourceOne.getConnection().getMetaData().getURL());
////		}catch (SQLException e){
////
////		}
//
//
//		return dataSourceOne;
//
////		return new EmbeddedDatabaseBuilder().
////				setType(EmbeddedDatabaseType.HSQL).
////				setName("customers").
////				build();
//	}
}
