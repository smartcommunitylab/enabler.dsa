# DSA-engine

## Prerequisites
  - Install elasticsearch + kibana + x-pack for both (license needed after 30 days trial?)
  - From elasticsearch directory, run *bin\x-pack\setup-passwords interactive* to set passwords (use *auto* in place of *interactive* for random generated passwords)
  - In kibana directory, edit *config/kibana.yml* and set *elasticsearch.url, elasticsearch.username, elasticsearch.password*
  
## Configuration
  - in *application.properties*, change *kibana.version*
  
## Web interface

  - */dsa-engine/consolelogin* for management console
  - */dsa-engine/kibanalogin* for kibana login
  
## REST interface

See API published on WSO2

*/management/{domain}/...*