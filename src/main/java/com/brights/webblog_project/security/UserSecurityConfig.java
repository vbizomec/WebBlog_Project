package com.brights.webblog_project.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class UserSecurityConfig {

//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.ignoring().antMatchers("/", "/register");
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests()
//                .antMatchers("/").permitAll()
//                .authorizeRequests()
                .antMatchers("/post/**").hasAnyRole("USER", "MODERATOR", "ADMIN")
                .antMatchers("/users/**").hasRole("ADMIN")
                .antMatchers("/postList").hasRole("ADMIN")
                .antMatchers("/updatePost/**").hasRole("ADMIN")
                .antMatchers("/showFormForPostDelete/**").hasRole("ADMIN")
                .antMatchers("/showCommentForUpdate/**").hasAnyRole("USER", "MODERATOR", "ADMIN")
                .antMatchers("/updatePost/**").hasAnyRole("USER", "MODERATOR", "ADMIN")
                .anyRequest().permitAll()
                .and()
                .formLogin()
                    .loginPage("/login")
                    .defaultSuccessUrl("/")
                    .failureUrl("/login?error")
//                    .passwordParameter("password")
//                    .usernameParameter("username")
                .and()
                .logout();
//                    .logoutSuccessUrl("/login?logout");

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
