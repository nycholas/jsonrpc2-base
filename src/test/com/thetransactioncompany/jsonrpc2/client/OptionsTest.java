package com.thetransactioncompany.jsonrpc2.client;


import java.net.*;

import com.thetransactioncompany.jsonrpc2.*;
import com.thetransactioncompany.jsonrpc2.util.*;

import junit.framework.*;


/**
 * Tests the JSONRPC2SessionOptions class.
 *
 * @author Vladimir Dzhuvinov
 * @version 1.7.1 (2011-07-13)
 */
public class OptionsTest extends TestCase {


        public OptionsTest(String name) {
                
                super(name);
        }


	public void testDefaultValues() {
	
		assertEquals("application/json", JSONRPC2SessionOptions.DEFAULT_CONTENT_TYPE);
		assertEquals("application/json", JSONRPC2SessionOptions.DEFAULT_ALLOWED_RESPONSE_CONTENT_TYPES[0]);
		assertEquals("text/plain", JSONRPC2SessionOptions.DEFAULT_ALLOWED_RESPONSE_CONTENT_TYPES[1]);
		assertEquals(false, JSONRPC2SessionOptions.DEFAULT_ACCEPT_COOKIES);
		assertEquals(null, JSONRPC2SessionOptions.DEFAULT_ORIGIN);
		assertEquals(false, JSONRPC2SessionOptions.DEFAULT_PRESERVE_OBJECT_MEMBER_ORDER);
		assertEquals(false, JSONRPC2SessionOptions.DEFAULT_IGNORE_VERSION);
		assertEquals(false, JSONRPC2SessionOptions.DEFAULT_PARSE_NON_STD_ATTRIBUTES);
		assertEquals(false, JSONRPC2SessionOptions.DEFAULT_TRUST_ALL);
	}
	
	
	public void testConstructor() {

		JSONRPC2SessionOptions opts = new JSONRPC2SessionOptions();
		
		assertEquals("application/json", opts.getRequestContentType());
		assertEquals("application/json", opts.getAllowedResponseContentTypes()[0]);
		assertEquals("text/plain", opts.getAllowedResponseContentTypes()[1]);
		assertEquals(false, opts.acceptsCookies());
		assertEquals(null, opts.getOrigin());
		assertEquals(false, opts.preservesParseOrder());
		assertEquals(false, opts.ignoresVersion());
		assertEquals(false, opts.parsesNonStdAttributes());
		assertEquals(false, opts.trustsAllCerts());
		
		assertTrue(opts.isAllowedResponseContentType("application/json"));
		assertTrue(opts.isAllowedResponseContentType("text/plain"));
	}
	
	
	public void testRequestContentType() {
		
		JSONRPC2SessionOptions opts = new JSONRPC2SessionOptions();
		
		opts.setRequestContentType("application/json+rpc");
		assertEquals("application/json+rpc", opts.getRequestContentType());
	}
	
	
	public void testAllowedRequestContentTypesPositive() {
	
		JSONRPC2SessionOptions opts = new JSONRPC2SessionOptions();
		
		opts.setAllowedResponseContentTypes(new String[]{"application/json+rpc"});
		
		assertTrue(opts.isAllowedResponseContentType("application/json+rpc"));
		assertTrue(opts.isAllowedResponseContentType("application/json+rpc;charset=utf8"));
	}
	
	
	public void testAllowedRequestContentTypesNegative() {
	
		JSONRPC2SessionOptions opts = new JSONRPC2SessionOptions();
		
		opts.setAllowedResponseContentTypes(new String[]{"applicaton/json+rpc"});
		
		assertFalse(opts.isAllowedResponseContentType("text/json"));
	}
	
	
	public void testAcceptCookies() {
	
		JSONRPC2SessionOptions opts = new JSONRPC2SessionOptions();
			
		opts.acceptCookies(true);
		assertTrue(opts.acceptsCookies());
	}
		
	
	public void testOrigin() {
	
		JSONRPC2SessionOptions opts = new JSONRPC2SessionOptions();
			
		opts.setOrigin("*");
		assertEquals("*", opts.getOrigin());
	}
	
	
	public void testParseOrder() {
		
		JSONRPC2SessionOptions opts = new JSONRPC2SessionOptions();
		
		opts.preserveParseOrder(true);
		assertTrue(opts.preservesParseOrder());
	}
	
	
	public void testVersion() {
	
		JSONRPC2SessionOptions opts = new JSONRPC2SessionOptions();
		
		opts.ignoreVersion(true);
		assertTrue(opts.ignoresVersion());
	}
	
	public void testNonStdAttributes() {
	
		JSONRPC2SessionOptions opts = new JSONRPC2SessionOptions();
		
		opts.parseNonStdAttributes(true);
		assertTrue(opts.parsesNonStdAttributes());
	}
	
	
	public void testTrustAllCerts() {
	
		JSONRPC2SessionOptions opts = new JSONRPC2SessionOptions();
		
		opts.trustAllCerts(true);
		assertTrue(opts.trustsAllCerts());
	}
}
