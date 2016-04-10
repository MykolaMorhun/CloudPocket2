package com.cloudpocket.config;

import com.cloudpocket.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserService userService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
            .authorizeRequests()
                .antMatchers("/api/**").fullyAuthenticated()
                /*.antMatchers("/api/**").hasRole("user")
                /*.antMatchers("/api/users/**").hasRole("admin")
                //.antMatchers("/api/user/**").access("hasRole('admin') and hasRole('user')")
                .antMatchers("/api/user/**").hasRole("user")
                .antMatchers("/api/files/**").hasRole("user")
                .antMatchers("/api/storage/**").hasRole("user")
                //.antMatchers("/storage**").fullyAuthenticated()*/
                //.antMatchers("/api/version").permitAll()
            .and().formLogin().loginPage("/login")
                .usernameParameter("login").passwordParameter("password").defaultSuccessUrl("/storage")
            .and().logout().logoutUrl("/logout").logoutSuccessUrl("/login")
                .invalidateHttpSession(true).addLogoutHandler(new CookieClearingLogoutHandler());
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(getPasswordEncoder());
    }

    @Bean
    public Md5PasswordEncoder getPasswordEncoder() {
        return new Md5PasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                com.cloudpocket.model.User user = userService.getUserByLogin(username);
                if (user != null) {
                    if (user.getId() != 1) {
                        return new User(user.getLogin(), user.getPasswordHash(),
                                AuthorityUtils.createAuthorityList("user"));
                    } else {
                        return new User(user.getLogin(), user.getPasswordHash(),
                                AuthorityUtils.createAuthorityList("admin"));
                    }
                } else {
                    throw new UsernameNotFoundException("Could not find the user '" + username + "'");
                }
            }
        };
    }

}
