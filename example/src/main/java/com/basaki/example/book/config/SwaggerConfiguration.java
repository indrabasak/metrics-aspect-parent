package com.basaki.example.book.config;

import com.google.common.base.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * {@code SwaggerConfiguration} is the configuration for setting up swagger for
 * the author controller. The swagger documentation can be viewed at {@code
 * http://<host>:<port>/swagger-ui-html}
 * <p/>
 *
 * @author Indra Basak
 * @since 2/23/17
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    /**
     * Creates the Swagger Docket (configuration) bean.
     *
     * @return docket bean
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("book")
                .select()
                .apis(exactPackage("com.basaki.example.book.controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo("Book API", "API for Books"));
    }

    /**
     * Creates an object containing API information including author name,
     * email, version, license, etc.
     *
     * @param title       API title
     * @param description API description
     * @return API information
     */
    private ApiInfo apiInfo(String title, String description) {
        Contact contact = new Contact("Indra Basak", "",
                "developer@gmail.com");
        return new ApiInfo(title, description, "1.0", "terms of controller url",
                contact, "license", "license url");
    }

    private static Predicate<RequestHandler> exactPackage(final String pkg) {
        return input -> input.declaringClass().getPackage().getName().equals(
                pkg);
    }
}
