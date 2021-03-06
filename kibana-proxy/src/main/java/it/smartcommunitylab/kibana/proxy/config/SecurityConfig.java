package it.smartcommunitylab.kibana.proxy.config;

import java.util.Map;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@EnableWebSecurity
@EnableOAuth2Client
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	OAuth2ClientContext oauth2ClientContext;
	
	@Bean
	  @ConfigurationProperties("security.oauth2.client")
	  public AuthorizationCodeResourceDetails aac() {
	      return new AuthorizationCodeResourceDetails();
	  }

	  @Bean
	  @ConfigurationProperties("security.oauth2.resource")
	  public ResourceServerProperties aacResource() {
	      return new ResourceServerProperties();
	  }	

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/kibana/**","/console/**").authenticated().and().exceptionHandling()
		.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")).accessDeniedPage("/accesserror")
				.and().logout().logoutSuccessHandler(logoutSuccessHandler()).permitAll().and().csrf().disable().addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);		
	}

	private Filter ssoFilter() {
		OAuth2ClientAuthenticationProcessingFilter aacFilter = new OAuth2ClientAuthenticationProcessingFilter("/login");
		OAuth2RestTemplate aacTemplate = new OAuth2RestTemplate(aac(), oauth2ClientContext);
		aacFilter.setRestTemplate(aacTemplate);
		AacUserInfoTokenServices tokenServices = new AacUserInfoTokenServices(aacResource().getUserInfoUri(), aac().getClientId());
		
		tokenServices.setRestTemplate(aacTemplate);
		tokenServices.setPrincipalExtractor(new PrincipalExtractor() {

			@SuppressWarnings("unchecked")
			@Override
			public Object extractPrincipal(Map<String, Object> map) {
				System.err.println(map);
				return map.get("token");
			}
		});
		aacFilter.setTokenServices(tokenServices);

		return aacFilter;
	}
	
	private LogoutSuccessHandler logoutSuccessHandler() {
		SimpleUrlLogoutSuccessHandler handler = new SimpleUrlLogoutSuccessHandler();
		handler.setDefaultTargetUrl("/");
		handler.setTargetUrlParameter("target");
		return handler;
	}

}