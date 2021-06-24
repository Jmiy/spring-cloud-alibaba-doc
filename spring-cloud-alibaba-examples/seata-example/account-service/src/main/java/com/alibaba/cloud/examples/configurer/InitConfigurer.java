package com.alibaba.cloud.examples.configurer;

import com.alibaba.cloud.examples.customer.CustomerRepository;
import com.alibaba.cloud.examples.customer.Customer;
import com.alibaba.cloud.examples.customer.Customer.CustomerId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Configuration
//@Profile("default")
public class InitConfigurer{
//    @Autowired
//    private CustomerRepository userDao;
//
//    @PostConstruct
//    public  void  init(){
//        for (int i = 1; i <= 2; i++) {
//            Customer customer = userDao.save(new Customer("Dave"+i, "Matthews"+i));
//
//            System.out.println("====init====="+customer+"============");
//        }
//    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
