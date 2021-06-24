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

import com.alibaba.nacos.api.config.listener.Listener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.Listener;

import javax.annotation.PostConstruct;
import com.alibaba.cloud.examples.customer.Customer.CustomerId;
/**
 * @author xiaojing
 */
//@SpringBootApplication(scanBasePackages = "com.alibaba.cloud.examples")
//@ComponentScan("com.alibaba.cloud.examples.*")
//@SpringBootApplication
@SpringBootApplication
public class AccountApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountApplication.class, args);
	}

	@Autowired
	DataInitializer initializer;

	@PostConstruct
	public void init() {

		CustomerId customerId = initializer.initializeCustomer();

		System.out.println("====init====="+customerId.getCustomerId()+"============");
		//initializer.initializeOrder(customerId);
	}

	@Bean
	public UserConfig userConfig() {
		return new UserConfig();
	}

}

//@Component
//class SampleRunner implements ApplicationRunner {
//
//	@Autowired
//	private NacosConfigManager nacosConfigManager;
//
//	@Override
//	public void run(ApplicationArguments args) throws Exception {
//		System.out.println("====SampleRunner=====");
//		nacosConfigManager.getConfigService().addListener(
//				"nacos-config-custom.properties", "DEFAULT_GROUP", new Listener() {
//
//					/**
//					 * Callback with latest config data.
//					 * @param configInfo latest config data for specific dataId in Nacos
//					 * server
//					 */
//					@Override
//					public void receiveConfigInfo(String configInfo) {
//						Properties properties = new Properties();
//						try {
//							properties.load(new StringReader(configInfo));
//						}
//						catch (IOException e) {
//							e.printStackTrace();
//						}
//						System.out.println("config changed: " + properties);
//					}
//
//					@Override
//					public Executor getExecutor() {
//						return null;
//					}
//				});
//	}
//
//}

@ConfigurationProperties(prefix = "user")
class UserConfig {

	private int age;

	private String name;

	private String hr;

	private Map<String, Object> map;

	private List<User> users;

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public String getHr() {
		return hr;
	}

	public void setHr(String hr) {
		this.hr = hr;
	}

	@Override
	public String toString() {
		System.out.println(map.keySet());
		System.out.println(map.values());
		System.out.println("====aa====="+map.get("aa")+"=====user.name==="+users.get(0).getName());
		return "UserConfig{" + "age=" + age + ", name='" + name + '\'' + ", map=" + map
				+ ", hr='" + hr + '\'' + ", users=" + users + '}';
	}

	public static class User {

		private String name;

		private String hr;

		private String avg;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getHr() {
			return hr;
		}

		public void setHr(String hr) {
			this.hr = hr;
		}

		public String getAvg() {
			return avg;
		}

		public void setAvg(String avg) {
			this.avg = avg;
		}

		@Override
		public String toString() {
			return "User{" + "name='" + name + '\'' + ", hr=" + hr + ", avg=" + avg + '}';
		}

	}

}
