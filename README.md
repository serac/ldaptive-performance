CAS LDAP Performance Test Framework
===================================

This is a simple framework that compares the performance of existing CAS LDAP authentication components versus equivalent ones in https://github.com/serac/cas-server-integration-ldaptive.

Building
-------

    mvn clean package assembly:single

Executing
---------

You must configure an LDAP properties file for your local directory environment. Use one of the sample properties files as a template. Execute the test from the project directory using the helper script:

    bin/loadtest.sh 50000 200 path/to/users.csv /spring-ldap-context.xml
    bin/loadtest.sh 50000 200 path/to/users.csv /ldaptive-context.xml

