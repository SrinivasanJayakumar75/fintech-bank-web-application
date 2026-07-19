package com.fintech.fintech.application.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.fintech.fintech.application.account.dtos.AccountDtos;
import com.fintech.fintech.application.account.entities.Account;
import com.fintech.fintech.application.auth_users.dtos.UserDto;
import com.fintech.fintech.application.auth_users.entities.User;

@Configuration
public class AppConfig {

    @Bean
    public SpringTemplateEngine templateEngine(){
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCharacterEncoding("UTF-8");

        templateEngine.setTemplateResolver(templateResolver);

        return templateEngine;
    }

    @Bean
    public ModelMapper modelMapperConfig(){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
        .setFieldMatchingEnabled(true)
        .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
        .setMatchingStrategy(MatchingStrategies.STANDARD)
        .setCollectionsMergeEnabled(false)
        .setAmbiguityIgnored(true);

        // Hibernate wraps @OneToMany/@ManyToMany lists in PersistentBag; copy into a plain List first
        modelMapper.addConverter(new AbstractConverter<Collection<?>, List<?>>() {
            @Override
            protected List<?> convert(Collection<?> source) {
                return source == null ? null : new ArrayList<>(source);
            }
        });

        modelMapper.emptyTypeMap(Account.class, AccountDtos.class)
            .addMappings(mapper -> {
                mapper.skip(AccountDtos::setUser);
                mapper.skip(AccountDtos::setTransactions);
            })
            .implicitMappings();

        modelMapper.emptyTypeMap(User.class, UserDto.class)
            .addMappings(mapper -> mapper.skip(UserDto::setPassword))
            .implicitMappings();

        return modelMapper;
    }



    
}
