package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

@Configuration
public class ThymeleafConfig {

    @Bean
    public SpringResourceTemplateResolver templateResolver() {

        SpringResourceTemplateResolver resolver =
                new SpringResourceTemplateResolver();

        resolver.setPrefix("classpath:/my-templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false);

        return resolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine(
            SpringResourceTemplateResolver templateResolver) {

        SpringTemplateEngine engine = new SpringTemplateEngine();

        engine.setTemplateResolver(templateResolver);

        return engine;
    }

    @Bean
    public ThymeleafViewResolver viewResolver(
            SpringTemplateEngine templateEngine) {

        ThymeleafViewResolver viewResolver =
                new ThymeleafViewResolver();

        viewResolver.setTemplateEngine(templateEngine);
        viewResolver.setCharacterEncoding("UTF-8");
        viewResolver.setOrder(1);

        return viewResolver;
    }
}