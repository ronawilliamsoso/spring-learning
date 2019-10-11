package com.wei.eu.pricing.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig
                extends WebSecurityConfigurerAdapter {

    @Value( "${/zte/global/spring-admin/username}" )
    private String inMemoryUser;

    @Value( "${/zte/global/spring-admin/password}" )
    private String inMemoryPassword;

    @Override
    protected void configure( final HttpSecurity http )
                    throws Exception {
        http.httpBasic()
                        .and().authorizeRequests().antMatchers( "/actuator/health" )
                        .access( "hasIpAddress( '127.0.0.1') or hasRole('SPRING_ADMIN')" )
                        .and().authorizeRequests().antMatchers( "/actuator/**" ).hasRole( "SPRING_ADMIN" )
                        .and().csrf().ignoringAntMatchers( "/actuator/**" );
    }

    @Autowired
    public void configureGlobal( final AuthenticationManagerBuilder auth )
                    throws Exception {
        auth.inMemoryAuthentication()
                        .withUser( inMemoryUser ).password( passwordEncoder().encode( inMemoryPassword ) )
                        .roles( "SPRING_ADMIN" );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder( 12 );
    }
}
