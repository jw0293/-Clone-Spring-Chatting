package spring.shat.demo.config.websecurity;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .csrf().disable()
                .headers()
                    .frameOptions().sameOrigin()
                .and()
                    .formLogin()
                .and()
                    .authorizeRequests()
                        .antMatchers("/chat/**").hasRole("USER")
                        .anyRequest().permitAll();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                    .withUser("Kim")
                    .password("{noop}1234")
                    .roles("USER")
                .and()
                    .withUser("Park")
                    .password("{noop}1234")
                    .roles("USER")
                .and()
                    .withUser("Guest")
                    .password("{noop}1234")
                    .roles("GUEST");
    }

}
