package it.smartcommunitylab.kibana.proxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
public class KibanaProxyApplication {

	public static void main(String[] args) {
		SpringApplication.run(KibanaProxyApplication.class, args);
	}
}
