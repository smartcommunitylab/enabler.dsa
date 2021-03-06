/**
 *    Copyright 2015 Fondazione Bruno Kessler - Trento RISE
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package it.smartcommunitylab.dsaengine.config;

import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.mongodb.MongoException;

import it.smartcommunitylab.dsaengine.elastic.ElasticManager;
import it.smartcommunitylab.dsaengine.storage.RepositoryManager;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableAsync
@EnableScheduling
@EnableSwagger2
public class AppConfig extends WebMvcConfigurerAdapter {

	@Autowired
	@Value("${swagger.title}")
	private String swaggerTitle;
	
	@Autowired
	@Value("${swagger.desc}")
	private String swaggerDesc;

	@Autowired
	@Value("${swagger.version}")
	private String swaggerVersion;
	
	@Autowired
	@Value("${swagger.tos.url}")
	private String swaggerTosUrl;
	
	@Autowired
	@Value("${swagger.contact}")
	private String swaggerContact;

	@Autowired
	@Value("${swagger.license}")
	private String swaggerLicense;

	@Autowired
	@Value("${swagger.license.url}")
	private String swaggerLicenseUrl;
	
	public AppConfig() {
	}

	@Bean
	RepositoryManager getRepositoryManager() throws UnknownHostException, MongoException {
		return new RepositoryManager();
	}
	
	@Bean
	ElasticManager getElasticManager() {
		return new ElasticManager();
	}
	
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
	    registry.addViewController("/").setViewName("index");
	}
	
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
		.allowedMethods("PUT", "DELETE", "GET", "POST", "PATCH");
	}
	
	@SuppressWarnings("deprecation")
	@Bean
  public Docket swaggerSpringMvcPluginAPI() {
		ApiInfo apiInfo = new ApiInfo(swaggerTitle, swaggerDesc, swaggerVersion, swaggerTosUrl, swaggerContact, 
				swaggerLicense, swaggerLicenseUrl);
     return new Docket(DocumentationType.SWAGGER_2)
     	.groupName("api")
     	.select()
     		.paths(PathSelectors.regex("/api/.*"))
     		.build()
        .apiInfo(apiInfo)
        .produces(getContentTypes());
  }
	
	@SuppressWarnings("deprecation")
	@Bean
  public Docket swaggerSpringMvcPluginManagement() {
		ApiInfo apiInfo = new ApiInfo(swaggerTitle, swaggerDesc, swaggerVersion, swaggerTosUrl, swaggerContact, 
				swaggerLicense, swaggerLicenseUrl);
     return new Docket(DocumentationType.SWAGGER_2)
     	.groupName("management")
     	.select()
     		.paths(PathSelectors.regex("/management/.*"))
     		.build()
        .apiInfo(apiInfo)
        .produces(getContentTypes());
  }	
	
	private Set<String> getContentTypes() {
		Set<String> result = new HashSet<String>();
		result.add("application/json");
    return result;
  }
	
}
