/*
* (c) Copyright IBM Corporation 2018
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.ibm.mq.samples.jms;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;

import javax.jms.*;
import java.io.Console;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * A minimal and simple application for Point-to-point messaging.
 *
 * Application makes use of fixed literals, any customisations will require
 * re-compilation of this source file. Application assumes that the named queue
 * is empty prior to a run.
 *
 * Notes:
 *
 * API type: JMS API (v2.0, simplified domain)
 *
 * Messaging domain: Point-to-point
 *
 * Provider type: IBM MQ
 *
 * Connection mode: Client connection
 *
 * JNDI in use: No
 *
 */
public class JmsPutFile {

	// System exit status value (assume unset value to be 1)
	private static int status = 1;

	// Create variables for the connection to MQ
	private static final String HOST = "mtlsqmchl.chl.mq.apps.daffy-sy1zj7y5.cloud.techzone.ibm.com"; // Host name or IP address
	private static final int PORT = 443; // Listener port for your queue manager
	private static final String CHANNEL = "MTLSQMCHL"; // Channel name
	private static final String QMGR = "EXAMPLEQM"; // Queue manager name
	//private static final String APP_USER = "app1"; // User name that application uses to connect to MQ
	//private static final String APP_PASSWORD = "passw0rd"; // Password that the application uses to connect to MQ
	private static final String QUEUE_NAME = "EXAMPLE.QUEUE"; // Queue that the application uses to put and get messages to and from


	/**
	 * Main method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		// Sanity check main() arguments and warn user
		if (args.length > 0) {
                	System.out.println("\n!!!! WARNING: You have provided arguments to the Java main() function. JVM arguments (such as -Djavax.net.ssl.trustStore) must be passed before the main class or .jar you wish to run.\n\n");
                	Console c = System.console();
                	System.out.println("Press the Enter key to continue");
                	c.readLine();
                }

		// Variables
		JMSContext context = null;
		Destination destination = null;
		JMSProducer producer = null;
		JMSConsumer consumer = null;
		String filePath = "path/to/your/file.txt";

		try {
			// Create a connection factory
			JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
			JmsConnectionFactory cf = ff.createConnectionFactory();

			// Set the properties
			cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, HOST);
			cf.setIntProperty(WMQConstants.WMQ_PORT, PORT);
			cf.setStringProperty(WMQConstants.WMQ_CHANNEL, CHANNEL);
			cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
			cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, QMGR);
			cf.setStringProperty(WMQConstants.WMQ_APPLICATIONNAME, "JmsPutGet (JMS)");
			cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, true);
			//cf.setStringProperty(WMQConstants.USERID, APP_USER);
			//cf.setStringProperty(WMQConstants.PASSWORD, APP_PASSWORD);
			cf.setStringProperty(WMQConstants.WMQ_SSL_CIPHER_SUITE, "*TLS13ORHIGHER");

			// Create JMS objects
			context = cf.createContext();
			destination = context.createQueue("queue:///" + QUEUE_NAME);

			// Read the file content into a byte array
			File file = new File(filePath);
			byte[] fileContent = new byte[(int) file.length()];
			try (FileInputStream fis = new FileInputStream(file)) {
				fis.read(fileContent);
			}

			// Create a message
			BytesMessage message = context.createBytesMessage();
			message.writeBytes(fileContent);

			// publish
			producer = context.createProducer();
			producer.send(destination, message);
			System.out.println("File published to topic: " + QUEUE_NAME);

			producer.close();
            context.close();

			recordSuccess();
		} catch (JMSException | IOException e) {
			recordFailure(e);
		}

		System.exit(status);

	} // end main()

	/**
	 * Record this run as successful.
	 */
	private static void recordSuccess() {
		System.out.println("SUCCESS");
		status = 0;
	}

	/**
	 * Record this run as failure.
	 *
	 * @param ex
	 */
	private static void recordFailure(Exception ex) {
		if (ex != null) {
			if (ex instanceof JMSException) {
				processJMSException((JMSException) ex);
			} else {
				//System.out.println(ex);
				ex.printStackTrace();
			}
		}
		System.out.println("FAILURE");
		status = -1;
	}

	/**
	 * Process a JMSException and any associated inner exceptions.
	 *
	 * @param jmsex
	 */
	private static void processJMSException(JMSException jmsex) {
		System.out.println(jmsex);
		Throwable innerException = jmsex.getLinkedException();
		if (innerException != null) {
			System.out.println("Inner exception(s):");
		}
		while (innerException != null) {
			System.out.println(innerException);
			innerException = innerException.getCause();
		}
	}

}
