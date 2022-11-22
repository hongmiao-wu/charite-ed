package com.wise23.chariteed;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

import java.util.Scanner;
import java.io.*;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http.authorizeHttpRequests((requests) -> requests.antMatchers("/", "/home").permitAll().anyRequest()
                                .authenticated()).formLogin((form) -> form.loginPage("/login").permitAll())
                                .logout((logout) -> logout.permitAll());

                return http.build();
        }

        @Bean
        public UserDetailsService userDetailsService() {
                InMemoryUserDetailsManager userDetailsService = new InMemoryUserDetailsManager();

                // Read user.csv file
                String path = System.getProperty("user.dir") + "/users.csv";
                Scanner file_reader = null;

                try {
                        file_reader = new Scanner(new File(path));
                } catch (IOException e) {
                        e.printStackTrace();
                }

                // Add users
                file_reader.useDelimiter("\n");
                while (file_reader.hasNext()) {
                        String[] entry = file_reader.next().split(",");
                        UserDetails user = User.withUsername(entry[0]).password(getPassword(entry[1])).roles(entry[2])
                                        .build();
                        userDetailsService.createUser(user);
                }

                return userDetailsService;
        }

        private String getPassword(String password) {
                PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
                return encoder.encode(password);
        }
}
