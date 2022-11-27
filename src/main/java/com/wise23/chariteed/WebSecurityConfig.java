package com.wise23.chariteed;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

import static org.springframework.security.config.Customizer.withDefaults;
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
        @Bean
        EmbeddedDatabase datasource() {
                return new EmbeddedDatabaseBuilder()
                        .setType(EmbeddedDatabaseType.H2)
                        .setName("test")
                        .addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION)
                        .build();
        }

        @Bean
        JdbcUserDetailsManager users(DataSource dataSource, PasswordEncoder encoder) {
                UserDetails doctor = User.builder()
                        .username("meo")
                        .password(encoder.encode("password"))
                        .roles("DOCTOR")
                        .build();
                JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
                jdbcUserDetailsManager.createUser(doctor);
                return jdbcUserDetailsManager;
        }

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                return http
                        .csrf(csrf -> csrf.ignoringAntMatchers("/h2-console/**"))
                        .authorizeRequests( auth -> auth
                                .antMatchers("/h2-console/**").permitAll()
                                .antMatchers("/", "/home").permitAll()
                                .anyRequest().authenticated()
                        )
                        .headers(headers -> headers.frameOptions().sameOrigin())
                        .formLogin((form) -> form.loginPage("/login").defaultSuccessUrl("/hello", true).permitAll())
                        .logout((logout) -> logout.permitAll())
                        .build();
        }

        @Bean
        PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
