package com.example.complaints.store.web;

import com.fasterxml.jackson.databind.Module;
import javaslang.jackson.datatype.JavaslangModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration {

    @Bean
    public Module javaslangModule() {
        return new JavaslangModule();
    }

}
