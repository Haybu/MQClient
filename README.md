# Configure IBM MQ with Mutual SSL and Test with a Sample Java App

1- Follow the steps in `create-certs.sh` file to create MQ server and client SSL keys and certificates.

2- login to Openshift cluster in your terminal.

3- create these Openshift resources. Note, these resources will be created in `cp4i` namespace in case of a single-namespace mode. For all_namespaces mode, you may need to created a new namespace and change the namespace references in these resource YAML files.

    a- create a secret using this command:

```sh
$ ./create-secret.sh
```

    
    b- create a configmap using this command:

```sh
$ oc create -f ./configmap.yml
```

    c- create a queue manager instance using this command. Note, you may need to change the licencse, etc.

```sh
$ oc create -f ./qm.yml    
```

    d- create Openshift route using this command:

```sh
$ oc create -f ./route.yml
```

4- compile the sample Java code:

```sh
$ java -cp ./com.ibm.mq.allclient-9.3.0.0.jar:./javax.jms-api-2.0.1.jar:./json-20220320.jar:. com.ibm.mq.samples.jms.JmsPutGet
```

5- run the sample Java code:

```sh
$ java -Djavax.net.debug=ssl -Djavax.net.ssl.trustStore=./example-qm.p12 -Djavax.net.ssl.trustStorePassword=passw0rd -Djavax.net.ssl.keyStore=./example-app1.p12 -Djavax.net.ssl.keyStorePassword=passw0rd  -cp ./com.ibm.mq.allclient-9.3.0.0.jar:./javax.jms-api-2.0.1.jar:./json-20220320.jar:. com.ibm.mq.samples.jms.JmsPutGet
```
