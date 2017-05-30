package com.basaki.example.book.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * {@code BookApplication} represents the entry point for metric aspect spring
 * boot application example.
 * <p/>
 *
 * @author Indra Basak
 * @since 2/23/17
 */
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = {
        "com.basaki.example.book.config",
        "com.basaki.example.book.controller",
        "com.basaki.example.book.data.entity",
        "com.basaki.example.book.data.repository",
        "com.basaki.example.book.error",
        "com.basaki.example.book.model",
        "com.basaki.example.book.service",
        "com.basaki.example.book.swagger"})
public class BookApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookApplication.class, args);
    }
}
