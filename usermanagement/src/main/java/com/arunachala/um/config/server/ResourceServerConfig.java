package com.arunachala.um.config.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.arunachala.alplcommon.utils.AppSecurityConstants;
import com.arunachala.um.config.CustomLogoutSuccessHandler;



@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	private static final String RESOURCE_ID = "resource-server-rest-api";
	private static final String SECURED_READ_SCOPE = "#oauth2.hasScope('read')";
	private static final String SECURED_WRITE_SCOPE = "#oauth2.hasScope('write')";
	private static final String SECURED_PATTERN = "/api/**";
	
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) {
		resources.resourceId(RESOURCE_ID);
	}
	
	@Autowired
    private CustomLogoutSuccessHandler customLogoutSuccessHandler;
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http
				.csrf().disable()
				.authorizeRequests()
					.antMatchers("/master/**","/api/master/users/**","/employee/attendence/**", "/fuel/**", 
							"/licencetype/**", "/drivers/**","/orders/**","/renewal/payments/**" ,"/mobile/vigilance/**",
							"/vigilance/**","/maintenance/**","/operations/**","/jobcard/**","/asset/*","/api/master/users/*",
							"/mechanic/**","/rmc/mobile/orders/*","/rmc/mobile/trip/*").hasAnyAuthority(AppSecurityConstants.ROLE_MANAGER,AppSecurityConstants.ROLE_VIGILANCE_OFFICER)
					.antMatchers("/api/admin/**").hasAuthority(AppSecurityConstants.ROLE_ADMIN)
					.antMatchers("/renewal-scheduler/*","/asset-scheduler/*","/driver-scheduler/*","/maintenance-scheduler/*",
							"/v2/api-docs", "/configuration/ui", "/swagger-resources/**", "/configuration/**","/swagger-ui.html", "/webjars/**",
							"/api/user/**","/user/forgotPassword", "/notification/**","/mobile/alcohol-check/*","/driver/violation/*","/vendor/*",
							"/driver/fr/**","/driver/paysheet/**","/rmc/mobile/request-otp","/rmc/mobile/validate-and-login","/violation/**").permitAll()
					//.anyRequest().authenticated()
				.and()
					.formLogin()
						.loginPage("/login")
						.defaultSuccessUrl("/homepage.html")
						.failureUrl("/login?error=true")
						.permitAll()
				/*.and()
					.antMatcher("/user/forgotPassword")*/
				.and()
					.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
					//.invalidSessionUrl("/invalidSession.html")
					.maximumSessions(100)
					//.maximumSessions(5).maxSessionsPreventsLogin(true)
					.sessionRegistry(sessionRegistry())
				.and()
					.sessionFixation().none()
				.and()
					.logout()
						.logoutUrl("/logout")
						.logoutSuccessHandler(customLogoutSuccessHandler)
						.logoutSuccessUrl("/logout?status=success")
						.permitAll()
				.and()
					.httpBasic();
	}

	@Bean
	public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}

	@Bean
	public FilterRegistrationBean<CorsFilter> corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		CorsConfiguration configAutentication = new CorsConfiguration();
		configAutentication.setAllowCredentials(true);
		configAutentication.addAllowedOrigin("*");
		configAutentication.addAllowedHeader("Authorization");
		configAutentication.addAllowedHeader("Content-Type");
		configAutentication.addAllowedHeader("Access-Control-Allow-Origin");
		configAutentication.addAllowedHeader("Access-Control-Allow-Headers");
		configAutentication.addAllowedHeader("Access-Control-Allow-Methods");
		configAutentication.addAllowedHeader("Accept");
		configAutentication.addAllowedMethod("POST");
		configAutentication.addAllowedMethod("GET");
		configAutentication.addAllowedMethod("DELETE");
		configAutentication.addAllowedMethod("PUT");
		configAutentication.addAllowedMethod("OPTIONS");
		configAutentication.setMaxAge(3600L);
		source.registerCorsConfiguration("/**", configAutentication);

		FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<CorsFilter>(new CorsFilter(source));
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return bean;
	}

}
