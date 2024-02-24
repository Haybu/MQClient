#!/bin/bash

# Create a private key to use for your internal certificate authority
openssl genpkey -algorithm rsa -pkeyopt rsa_keygen_bits:4096 -out ca.key

# Issue a self-signed certificate for your internal certificate authority
openssl req -x509 -new -nodes -key ca.key -sha512 -days 30 -subj "/CN=example-selfsigned-ca" -out ca.crt

### QUEUE MANAGER
# Create a private key and certificate for a queue manager
openssl req -new -nodes -out example-qm.csr -newkey rsa:4096 -keyout example-qm.key -subj '/CN=example-qm'

# Sign the queue manager key with your internal certificate authority
openssl x509 -req -in example-qm.csr -CA ca.crt -CAkey ca.key -CAcreateserial -out example-qm.crt -days 7 -sha512

# Create a PKCS#12 key store with the server's key and certificate
openssl pkcs12 -export -in "example-qm.crt" -name "example-qm" -certfile "ca.crt" -inkey "example-qm.key" -out "example-qm.p12" -passout pass:passw0rd

# (Optional) Convert server key store to jks to use with java apps
keytool -importkeystore  -srckeystore example-qm.p12 -destkeystore example-qm.jks -srcstoretype PKCS12 -deststoretype jks -srcstorepass passw0rd -deststorepass passw0rd

### Application
# Create a private key and certificate signing request for an application
openssl req -new -nodes -out example-app1.csr -newkey rsa:4096 -keyout example-app1.key -subj '/CN=example-app1'

# Sign the application key with your internal certificate authority
openssl x509 -req -in example-app1.csr -CA ca.crt -CAkey ca.key -CAcreateserial -out example-app1.crt -days 7 -sha512

# Create a PKCS#12 key store with the application's key and certificate
openssl pkcs12 -export -in "example-app1.crt" -name "example-app1" -certfile "ca.crt" -inkey "example-app1.key" -out "example-app1.p12" -passout pass:passw0rd

# (Optioal) Convert client key store to jks to use with java apps
keytool -importkeystore  -srckeystore example-app1.p12 -destkeystore example-app1.jks -srcstoretype PKCS12 -deststoretype jks -srcstorepass passw0rd -deststorepass passw0rd

## note:
## you need to create the secret (create-secret.sh), configmap(configmap.yml), mq instance (qm.yml), and route (route.yml)
## in all-namespace mode, you can create a new namespace (namespace.yml) and change the K8s resources above to use this namespapce