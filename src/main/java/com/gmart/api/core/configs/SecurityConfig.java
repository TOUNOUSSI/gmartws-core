package com.gmart.api.core.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.gmart.api.core.security.JwtAuthenticationEntryPoint;
import com.gmart.api.core.security.JwtAuthenticationFilter;
import com.gmart.api.core.services.AccountService;


/**
 * Created by TOUNOUSSI Youssef on 02/05/18.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
	private AccountService accountService;

    @Value("${app.jwtSecret}")
    public String secret;

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
				.userDetailsService((UserDetailsService) accountService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
		http.headers().frameOptions().disable().and().cors().and().sessionManagement().maximumSessions(1).and()
				.sessionCreationPolicy(SessionCreationPolicy.NEVER).and().exceptionHandling()
				.authenticationEntryPoint(unauthorizedHandler).and().authorizeRequests()
				.and().authorizeRequests()
				.antMatchers("/", "/*.js", "/*.jsp", "/favicon.ico", "/**/*.png", "/**/*.gif", "/**/*.svg", "/**/*.jpg",
						"/**/*.html", "/**/*.css", "/**/*.js", "/account/login", "/**/login*", "/**/login**",
						"/invalidSession", "/resources/invalidSession")
				.permitAll()
				.antMatchers("/h2-console/**", "/h2-console", "/authentication/logout", "/authentication/login")
				.permitAll().antMatchers(org.springframework.http.HttpMethod.OPTIONS, "/service/**").permitAll()
				.antMatchers(HttpMethod.POST, "/authentication/signin", "/authentication/signup","/profile/update-profile-picture","/profile/update-profile-cover").permitAll()
				.antMatchers(HttpMethod.GET, "/friend/myfriends","/account/accounts").permitAll()
				.antMatchers(HttpMethod.GET, "/friend/find-friend/{criteria}","/friend/are-we-already-friends/{pseudoname}","/profile/find-my-profile","/profile/find-profile/{pseudoname}").permitAll()
				.antMatchers(HttpMethod.PUT, "/friend/add-new-friend/{pseudoname}").permitAll()
				.anyRequest().authenticated().and()
				.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).permitAll().and().csrf().disable(); // Disabling
																															// the
																															// CSRF

		http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
