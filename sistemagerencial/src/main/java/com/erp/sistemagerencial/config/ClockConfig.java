package com.erp.sistemagerencial.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ClockConfig {

    // Relógio do sistema disponível para injeção. Em testes, usamos Clock.fixed(...) no lugar.
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
