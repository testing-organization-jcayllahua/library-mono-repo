package com.test.libraryexample.config;

import com.test.libraryexample.MyService;
import com.test.libraryexample.StartupLogger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AutoConfig {
    @Bean
    public MyService myService(){
        return new MyService();
    }

    @Bean
    public StartupLogger startupLogger(){
        return new StartupLogger();
    }
}
