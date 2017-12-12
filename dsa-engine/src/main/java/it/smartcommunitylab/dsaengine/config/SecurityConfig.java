package it.smartcommunitylab.dsaengine.config;

import java.util.Map;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import it.smartcommunitylab.dsaengine.security.CustomAuthenticationProvider;
import it.smartcommunitylab.dsaengine.security.CustomResourceAuthenticationProvider;
import it.smartcommunitylab.dsaengine.security.CustomTokenExtractor;

@Configuration
@EnableWebSecurity
@EnableOAuth2Client
//@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig {

	@Autowired
	private CustomAuthenticationProvider customAuthenticationProvider;

	@Autowired
	private CustomResourceAuthenticationProvider customResourceAuthenticationProvider;	
	
	@Autowired
	OAuth2ClientContext oauth2ClientContext;	


	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth
		.authenticationProvider(customResourceAuthenticationProvider)
		.authenticationProvider(customAuthenticationProvider);
	}
	
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

	    @Bean(name="oauthAuthenticationEntryPoint")
	    public OAuth2AuthenticationEntryPoint getOAuth2AuthenticationEntryPoint() {
	    	return new OAuth2AuthenticationEntryPoint();
	    }
	    
	    @Bean(name="oAuth2AccessDeniedHandler")
	    public OAuth2AccessDeniedHandler getOAuth2AccessDeniedHandler() {
	    	return new OAuth2AccessDeniedHandler();
	    }  	  
	  
	
	@Configuration
	@Order(1)
	public static class OAuthSecurityConfig1 extends WebSecurityConfigurerAdapter {

		@Bean(name = "resourceFilter")
		public OAuth2AuthenticationProcessingFilter getResourceFilter() throws Exception {
			OAuth2AuthenticationProcessingFilter rf = new OAuth2AuthenticationProcessingFilter();
			rf.setAuthenticationManager(authenticationManager());
			rf.setTokenExtractor(new CustomTokenExtractor());
			rf.setStateless(false);
			return rf;
		}

		@Override
		public void configure(HttpSecurity http) throws Exception {
			http.csrf().disable();
			http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

			http.antMatcher("/management/**").authorizeRequests().antMatchers("/management/**").fullyAuthenticated().and().addFilterBefore(getResourceFilter(),
					RequestHeaderAuthenticationFilter.class);
		}
	} 
	  
	@Configuration
	@Order(2)
	public class WebSecurity extends WebSecurityConfigurerAdapter {
	  
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.headers().frameOptions().disable();
		http.csrf().disable();
		http.authorizeRequests().antMatchers("/console/**","/kibana/**").fullyAuthenticated().and().exceptionHandling().authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
		.accessDeniedPage("/accesserror").and().logout().logoutSuccessHandler(logoutSuccessHandler()).permitAll().and().csrf().disable()
		.addFilterBefore(aacFilter(), BasicAuthenticationFilter.class);
	}	  
	
	private Filter aacFilter() {
		OAuth2ClientAuthenticationProcessingFilter aacFilter = new OAuth2ClientAuthenticationProcessingFilter("/login");
		OAuth2RestTemplate aacTemplate = new OAuth2RestTemplate(aac(), oauth2ClientContext);
		aacFilter.setRestTemplate(aacTemplate);
		AacUserInfoTokenServices tokenServices = new AacUserInfoTokenServices(aacResource().getUserInfoUri(), aac().getClientId());
		
		tokenServices.setRestTemplate(aacTemplate);
		tokenServices.setPrincipalExtractor(new PrincipalExtractor() {

			@SuppressWarnings("unchecked")
			@Override
			public Object extractPrincipal(Map<String, Object> map) {
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
	

	
	
}
