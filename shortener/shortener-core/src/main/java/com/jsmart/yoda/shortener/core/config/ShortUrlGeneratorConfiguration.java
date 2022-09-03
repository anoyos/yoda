package com.jsmart.yoda.shortener.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.jsmart.yoda.shortener.core.model.ShortUrlGeneratorSettings;

/**
 * @author Nick Koretskyy
 *
 */
@Configuration
@PropertySource("classpath:application.properties")
public class ShortUrlGeneratorConfiguration {

	@Value("${shorturl.lenght}")
	private int shorturlLenght;

	@Bean
	ShortUrlGeneratorSettings shortUrlGeneratorConfig() {
		ShortUrlGeneratorSettings shortUrlGeneratorSettings = new ShortUrlGeneratorSettings();
		shortUrlGeneratorSettings.setShortUrlLenght(shorturlLenght);
		return shortUrlGeneratorSettings;
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
	    return new PropertySourcesPlaceholderConfigurer();
	}

}
