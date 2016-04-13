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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String USER = "USER";
    private static final String ADMIN = "ADMIN";

    @Autowired
    UserService userService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
            .authorizeRequests()
                .antMatchers("/api/users/**").hasAuthority(ADMIN)
                .antMatchers("/api/user/**").hasAuthority(USER)
                .antMatchers("/api/files/**").hasAuthority(USER)
                .antMatchers("/storage/**").hasAuthority(USER)
                .antMatchers("/view/**").hasAuthority(USER)
                .antMatchers("/api/version").permitAll()
            .and().formLogin().loginPage("/login")
                .usernameParameter("login").passwordParameter("password").defaultSuccessUrl("/storage")
            .and().logout().logoutUrl("/logout").logoutSuccessUrl("/login")
                .invalidateHttpSession(true);
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
                    List<GrantedAuthority> roles;
                    if (user.getId() != 1) {
                        roles = AuthorityUtils.createAuthorityList(USER);
                    } else {
                        roles = AuthorityUtils.createAuthorityList(ADMIN, USER);
                    }
                    return new User(user.getLogin(), user.getPasswordHash(), roles);
                } else {
                    throw new UsernameNotFoundException("Could not find the user '" + username + "'");
                }
            }
        };
    }

}
