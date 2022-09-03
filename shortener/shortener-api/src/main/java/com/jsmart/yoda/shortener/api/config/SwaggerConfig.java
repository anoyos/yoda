package com.jsmart.yoda.shortener.api.config;

import java.time.LocalDate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author Nick Koretskyy
 *
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

	@Bean
	public Docket api() {// @formatter:off
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.jsmart.yoda.shortener.api"))
				.paths(PathSelectors.any())
				.build()
				.apiInfo(apiInfo())
				.pathMapping("/")
				.directModelSubstitute(LocalDate.class, String.class)
			    .genericModelSubstitutes(ResponseEntity.class)
			    ;
	}// @formatter:on

	private ApiInfo apiInfo() {// @formatter:off
		return new ApiInfoBuilder()
				.title("Shortener service REST API")
				.description("REST API for Url operations routine.")
				.termsOfServiceUrl("http://www-03.ibm.com/software/sla/sladb.nsf/sla/bm?Open")
				.contact(new Contact(
						"jSmart team",
						"http://yoda.jsmart.com/shortener/api/",
						"nik.koretskyy@gmail.com")
				)
				.license("Apache License Version 2.0")
				.licenseUrl("https://github.com/IBM-Bluemix/news-aggregator/blob/master/LICENSE")
				.version("1.0")
				.build();
	}// @formatter:on
}
