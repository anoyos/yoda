package com.jsmart.yoda.shortener.api.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @author Nick Koretskyy
 *
 */
@Configuration
@ComponentScan("com.jsmart.yoda.shortener")
@EnableWebMvc
public class TestApiConfiguration {

}
