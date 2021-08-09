package com.vlife.configurations;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource("classpath:application.properties")
public class BotConfiguration {

    @Value("${botUsername}")
    String botUsername;

    @Value("${botToken}")
    String token;
}
