package com.tutorbridge.config;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides an injectable application clock fixed to the configured time zone (Asia/Dhaka by default)
 * so rate limiters, demo schedules, and analytics charts can be tested deterministically.
 */
@Configuration
public class ClockConfig {

    @Bean
    public Clock clock(@Value("${app.zone:Asia/Dhaka}") String zoneId) {
        return Clock.system(ZoneId.of(zoneId));
    }
}
