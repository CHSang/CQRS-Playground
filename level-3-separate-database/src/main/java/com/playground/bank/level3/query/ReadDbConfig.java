package com.playground.bank.level3.query;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;

import javax.sql.DataSource;

/**
 * Connects the query side to the read database (settings in application.yml
 * under app.read-datasource; its tables come from the Liquibase changelog).
 *
 * Spring Boot only auto-configures one datasource (the write one), so the
 * read one is built here. It's kept out of the Spring context on purpose so
 * the write datasource auto-configuration still applies.
 */
@Configuration
public class ReadDbConfig {

    @Bean
    JdbcClient readDb(@Value("${app.read-datasource.url}") String url,
                      @Value("${app.read-datasource.username}") String username,
                      @Value("${app.read-datasource.password}") String password) {
        DataSource dataSource = DataSourceBuilder.create().url(url).username(username).password(password).build();
        return JdbcClient.create(dataSource);
    }
}
