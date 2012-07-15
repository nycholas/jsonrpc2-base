package com.thetransactioncompany.jsonrpc2.client;


import java.net.*;

import com.thetransactioncompany.jsonrpc2.*;
import com.thetransactioncompany.jsonrpc2.util.*;

import junit.framework.*;


/**
 * Tests the JSONRPC2Session class.
 *
 * @author Vladimir Dzhuvinov
 * @version 1.7.1 (2011-08-24)
 */
public class Test extends TestCase {

	public static final String URL_HTTP_GOOD = "http://localhost:8080/json2ldap/";
	public static final String URL_HTTP_BAD_HOST = "http://bad-host.com/ws/";
	public static final String URL_HTTP_BAD_PORT = "http://localhost:9999/";
	public static final String URL_HTTP_BAD_IP = "http://192.168.0.99";
	public static final String URL_HTTP_BAD_SERVICE = "http://google.com/";
	public static final String URL_HTTPS_SELF_SIGNED = "https://shakespeare.dzhuvinov.com:8443/json2ldap/";

	public static final String RPC_METHOD_GOOD = "ws.getTime";

	
	public Test(String name) {

		super(name);
	}


	public void testRequest() {

		// Assumes a Json2Ldap service

		JSONRPC2Request request = new JSONRPC2Request(RPC_METHOD_GOOD, 0);

		URL url = null;

		try {
			url = new URL(URL_HTTP_GOOD);

		} catch (MalformedURLException e) {
			fail(e.getMessage());
		}

		JSONRPC2Session session = new JSONRPC2Session(url);


		JSONRPC2Response response = null;

		try {
			response = session.send(request);

		} catch (JSONRPC2SessionException e) {
			fail(e.getMessage());
		}

		System.out.println("Response: " + response);
	}


	public void testNotification() {

		// Assumes a Json2Ldap service

		JSONRPC2Notification notification = new JSONRPC2Notification(RPC_METHOD_GOOD);

		URL url = null;

		try {
			url = new URL(URL_HTTP_GOOD);

		} catch (MalformedURLException e) {
			fail(e.getMessage());
		}

		JSONRPC2Session session = new JSONRPC2Session(url);


		try {
			session.send(notification);

		} catch (JSONRPC2SessionException e) {
			fail(e.getMessage());
		}
	}


	public void testNetworkException1() {

		// Assumes a Json2Ldap service

		JSONRPC2Request request = new JSONRPC2Request(RPC_METHOD_GOOD, 0);

		URL url = null;

		try {
			url = new URL(URL_HTTP_BAD_HOST);

		} catch (MalformedURLException e) {
			fail(e.getMessage());
		}

		JSONRPC2Session session = new JSONRPC2Session(url);


		JSONRPC2Response response = null;

		try {
			response = session.send(request);
			fail("Failed to raise network exception");

		} catch (JSONRPC2SessionException e) {
			// ok
			assertEquals(JSONRPC2SessionException.NETWORK_EXCEPTION, e.getCauseType());
			System.out.println(e.getMessage() + ": " + e.getCause().getMessage() + " [" + e.getCause().getClass().getName() + "]");
		}
	}


	public void testNetworkException2() {

		// Assumes a Json2Ldap service

		JSONRPC2Request request = new JSONRPC2Request(RPC_METHOD_GOOD, 0);

		URL url = null;

		try {
			url = new URL(URL_HTTP_BAD_PORT);

		} catch (MalformedURLException e) {
			fail(e.getMessage());
		}

		JSONRPC2Session session = new JSONRPC2Session(url);


		JSONRPC2Response response = null;

		try {
			response = session.send(request);
			fail("Failed to raise network exception");

		} catch (JSONRPC2SessionException e) {
			// ok
			assertEquals(JSONRPC2SessionException.NETWORK_EXCEPTION, e.getCauseType());
			System.out.println(e.getMessage() + ": " + e.getCause().getMessage() + " [" + e.getCause().getClass().getName() + "]");
		}
	}



	public void testNetworkException3() {

		// Assumes a Json2Ldap service

		JSONRPC2Request request = new JSONRPC2Request(RPC_METHOD_GOOD, 0);

		URL url = null;

		try {
			url = new URL(URL_HTTP_BAD_IP);

		} catch (MalformedURLException e) {
			fail(e.getMessage());
		}

		JSONRPC2Session session = new JSONRPC2Session(url);


		JSONRPC2Response response = null;

		try {
			response = session.send(request);
			fail("Failed to raise network exception");

		} catch (JSONRPC2SessionException e) {
			// ok
			assertEquals(JSONRPC2SessionException.NETWORK_EXCEPTION, e.getCauseType());
			System.out.println(e.getMessage() + ": " + e.getCause().getMessage() + " [" + e.getCause().getClass().getName() + "]");
		}
	}


	public void testNetworkException4() {

		// Assumes a Json2Ldap service

		JSONRPC2Request request = new JSONRPC2Request(RPC_METHOD_GOOD, 0);

		URL url = null;

		try {
			// NO POST (HTTP 405)
			url = new URL(URL_HTTP_BAD_SERVICE);

		} catch (MalformedURLException e) {
			fail(e.getMessage());
		}

		JSONRPC2Session session = new JSONRPC2Session(url);


		JSONRPC2Response response = null;

		try {
			response = session.send(request);
			fail("Failed to raise JSON-RPC 2.0 exception");

		} catch (JSONRPC2SessionException e) {
			// ok
			System.out.println(e.getMessage() + ": " + e.getCause().getMessage() + " [" + e.getCause().getClass().getName() + "]");

			assertEquals(JSONRPC2SessionException.NETWORK_EXCEPTION, e.getCauseType());
		}
	}


	public void testRequestTrustAllCerts() {

		// Assumes a Json2Ldap service

		JSONRPC2Request request = new JSONRPC2Request(RPC_METHOD_GOOD, 0);

		URL url = null;

		try {
			url = new URL(URL_HTTPS_SELF_SIGNED);

		} catch (MalformedURLException e) {
			fail(e.getMessage());
		}

		JSONRPC2Session session = new JSONRPC2Session(url);

		JSONRPC2SessionOptions opts = session.getOptions();
		opts.trustAllCerts(true);
		session.setOptions(opts);


		JSONRPC2Response response = null;

		try {
			response = session.send(request);

		} catch (JSONRPC2SessionException e) {

			String msg = e.getMessage();

			Throwable cause = e.getCause();
			if (cause != null)
				msg += ": " + cause.getMessage();

			fail(msg);
		}

		System.out.println("Response: " + response);
	}


	public void testRequestIDMatchingBoolean() {

		// Assumes a Json2Ldap service

		JSONRPC2Request request = new JSONRPC2Request(RPC_METHOD_GOOD, true);

		URL url = null;

		try {
			url = new URL(URL_HTTP_GOOD);

		} catch (MalformedURLException e) {
			fail(e.getMessage());
		}

		JSONRPC2Session session = new JSONRPC2Session(url);


		JSONRPC2Response response = null;

		try {
			response = session.send(request);

		} catch (JSONRPC2SessionException e) {

			fail(e.getMessage());
		}

		System.out.println("Response: " + response);
	}


	public void testRequestIDMatchingNumber() {

		// Assumes a Json2Ldap service

		JSONRPC2Request request = new JSONRPC2Request(RPC_METHOD_GOOD, 3.14);

		URL url = null;

		try {
			url = new URL(URL_HTTP_GOOD);

		} catch (MalformedURLException e) {
			fail(e.getMessage());
		}

		JSONRPC2Session session = new JSONRPC2Session(url);


		JSONRPC2Response response = null;

		try {
			response = session.send(request);

		} catch (JSONRPC2SessionException e) {

			fail(e.getMessage());
		}

		System.out.println("Response: " + response);
	}


	public void testRequestIDMatchingString() {

		// Assumes a Json2Ldap service

		JSONRPC2Request request = new JSONRPC2Request(RPC_METHOD_GOOD, "abc");

		URL url = null;

		try {
			url = new URL(URL_HTTP_GOOD);

		} catch (MalformedURLException e) {
			fail(e.getMessage());
		}

		JSONRPC2Session session = new JSONRPC2Session(url);


		JSONRPC2Response response = null;

		try {
			response = session.send(request);

		} catch (JSONRPC2SessionException e) {

			fail(e.getMessage());
		}

		System.out.println("Response: " + response);
	}


	public void testRequestIDMatchingNull() {

		// Assumes a Json2Ldap service

		JSONRPC2Request request = new JSONRPC2Request(RPC_METHOD_GOOD, null);

		URL url = null;

		try {
			url = new URL(URL_HTTP_GOOD);

		} catch (MalformedURLException e) {
			fail(e.getMessage());
		}

		JSONRPC2Session session = new JSONRPC2Session(url);


		JSONRPC2Response response = null;

		try {
			response = session.send(request);

		} catch (JSONRPC2SessionException e) {

			fail(e.getMessage());
		}

		System.out.println("Response: " + response);
	}

	public void testContentType() {

		URL url = null;

		try {
			url = new URL(URL_HTTP_GOOD);

		} catch (MalformedURLException e) {
			fail(e.getMessage());
		}

		JSONRPC2Session session = new JSONRPC2Session(url);

		session.getOptions().setRequestContentType("application/json+rpc");

		assertEquals("application/json+rpc", session.getOptions().getRequestContentType());
	}
}
