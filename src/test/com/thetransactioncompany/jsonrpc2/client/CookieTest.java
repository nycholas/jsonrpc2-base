package com.thetransactioncompany.jsonrpc2.client;


import java.io.*;
import java.net.*;
import java.util.*;

import com.thetransactioncompany.jsonrpc2.*;
import com.thetransactioncompany.jsonrpc2.util.*;

import junit.framework.*;


/**
 * Tests the cookie management.
 *
 * @author Vladimir Dzhuvinov
 * @version 1.7.1 (2011-08-23)
 */
public class CookieTest extends TestCase {
	
	
	public CookieTest(String name) {
	
			super(name);
	
	}
	
	
	public void testMultipleCookies() {
		
		CookieTestServer server = null;
		
		try {
			
			server = new CookieTestServer(18080);
			
			
		} catch (IOException e) {
			
			fail(e.getMessage());
		}
		
		URL url = null;
		
		try {
			url = new URL("http://localhost:18080/jsonrpc2/");
		
		} catch (MalformedURLException e) {
			
			fail(e.getMessage());
		}
		
		JSONRPC2Session client = new JSONRPC2Session(url);
		
		client.getOptions().acceptCookies(true);
		
		JSONRPC2Request req = new JSONRPC2Request("test.cookie", 0);
		
		JSONRPC2Response resp = null;
		
		try {
			resp = client.send(req);
		
		} catch (JSONRPC2SessionException e) {
			
			fail(e.getMessage());
		}
		
		System.out.println(resp);
		
		Set<HttpCookie> cookies = client.getCookies();
		
		System.out.println("Listing received cookies:");
		
		Iterator <HttpCookie> it = cookies.iterator();
		
		while(it.hasNext())
			System.out.println("\t" + it.next());
		
		// resend request
		try {
			resp = client.send(req);
		
		} catch (JSONRPC2SessionException e) {
			
			fail(e.getMessage());
		}
		
		server.stop();
		
	}
}