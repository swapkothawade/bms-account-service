package com.mybank.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.hateoas.client.LinkDiscoverer;
import org.springframework.hateoas.mediatype.collectionjson.CollectionJsonLinkDiscoverer;
import org.springframework.plugin.core.SimplePluginRegistry;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import org.springframework.hateoas.client.LinkDiscoverers;
import springfox.documentation.service.Contact;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableSwagger2
public class BmsAccountServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BmsAccountServiceApplication.class, args);

	}

	@Bean
	public Docket userApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()                .apis(RequestHandlerSelectors.basePackage("com.mybank.api.controller"))
				.paths(PathSelectors.ant("/**/api/**"))
				.build().apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		return new ApiInfo("User Service",
				"User Management Service",
				"1.0.0",
				null,
				new Contact("Swapnil","http://mybank.com/contact","swapnil@mybank.com"),
				"author","",new ArrayList());
	}


	@Primary
	@Bean
	public LinkDiscoverers discoverers() {
		List<LinkDiscoverer> plugins = new ArrayList<>();
		plugins.add(new CollectionJsonLinkDiscoverer());
		return new LinkDiscoverers(SimplePluginRegistry.create(plugins));
	}

}
