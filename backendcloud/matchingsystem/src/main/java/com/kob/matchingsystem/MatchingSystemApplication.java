package com.kob.matchingsystem;

import com.kob.matchingsystem.service.MatchingService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.kob.matchingsystem.service.impl.MatchingServiceImpl;

@SpringBootApplication
public class MatchingSystemApplication {
    public static void main(String[] args) {
        MatchingServiceImpl.matchingPool.start(); // 启动匹配线程
        SpringApplication.run(MatchingSystemApplication.class, args);
    }
}