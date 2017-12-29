# DSA-engine

## Prerequisites
  - Install elasticsearch + kibana + x-pack for both (license needed after 30 days trial?)
  - From elasticsearch directory, run *bin\x-pack\setup-passwords interactive* to set passwords (use *auto* in place of *interactive* for random generated passwords)
  - In kibana directory, edit *config/kibana.yml* and set *elasticsearch.url, elasticsearch.username, elasticsearch.password*
  
## Apache Configuration

  - in httpd.conf, add
```

	    <VirtualHost *:80>
		    ProxyPreserveHost On
		    ProxyRequests On
		    ProxyPass / http://localhost:5601/
		    ProxyPassReverse / http://localhost:5601/
			RewriteEngine on
			RewriteCond %{HTTP_REFERER} !.*kibanalogin.*
			RewriteRule /app/kibana(.*)$ http://localhost:6030/dsa-engine/kibanalogin$1 [R=303]	
		</VirtualHost>
```
  
## Application Configuration
  - in *application.properties*, change *kibana.version*
  
## Web interface

  - */dsa-engine/consolelogin* for management console
  - */dsa-engine/kibanalogin* for kibana login
  
## REST interface

See API published on WSO2

*/management/{domain}/...*