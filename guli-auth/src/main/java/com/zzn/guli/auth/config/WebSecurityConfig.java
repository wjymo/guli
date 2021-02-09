package com.zzn.guli.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(AuthenticationManagerBuilder builder) throws Exception{
//        builder.inMemoryAuthentication().withUser("admin").password("{noop}123").roles("ADMIN").and()
//                .withUser("root").password("{noop}123").roles("ROOT");
        builder.inMemoryAuthentication().withUser("admin").password(passwordEncoder.encode("111")).roles("ADMIN").and()
                .withUser("user").password(passwordEncoder.encode("111")).roles("USER");

    }
}
