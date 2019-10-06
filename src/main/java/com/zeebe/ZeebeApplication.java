package com.zeebe;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({"com.zeebe.mapper"})
public class ZeebeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZeebeApplication.class, args);
    }

}
