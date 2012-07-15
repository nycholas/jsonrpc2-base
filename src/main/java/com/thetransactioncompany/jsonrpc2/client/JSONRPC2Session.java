package com.thetransactioncompany.jsonrpc2.client;


import java.io.*;
import java.net.*;
import java.security.*;
import java.security.cert.*;
import java.util.*;

import javax.net.ssl.*;

import com.thetransactioncompany.jsonrpc2.*;


/** 
 * Sends requests and / or notifications to a specified JSON-RPC 2.0 server URL.
 * The JSON-RPC 2.0 messages are dispatched by means of HTTP(S) POST.
 *
 * <p>The client-session class has a number of {@link JSONRPC2SessionOptions 
 * optional settings}. To change them pass a modified options instance to the
 * {@link #setOptions setOptions()} method.
 *
 * <p>Example JSON-RPC 2.0 client session:
 *
 * <pre>
 * // First, import the required packages:
 * 
 * // The Client sessions package
 * import com.thetransactioncompany.jsonrpc2.client.*;
 * 
 * // The Base package for representing JSON-RPC 2.0 messages
 * import com.thetransactioncompany.jsonrpc2.*;
 * 
 * // The JSON Smart package for JSON encoding/decoding (optional)
 * import net.minidev.json.*;
 * 
 * // For creating URLs
 * import java.net.*;
 * 
 * // ...
 * 
 * 
 * // Creating a new session to a JSON-RPC 2.0 web service at a specified URL
 * 
 * // The JSON-RPC 2.0 server URL
 * URL serverURL = null;
 * 
 * try {
 * 	serverURL = new URL("http://jsonrpc.example.com:8080");
 * 	
 * } catch (MalformedURLException e) {
 * 	// handle exception...
 * }
 * 
 * // Create new JSON-RPC 2.0 client session
 *  JSONRPC2Session mySession = new JSONRPC2Session(serverURL);
 * 
 * 
 * // Once the client session object is created, you can use to send a series
 * // of JSON-RPC 2.0 requests and notifications to it.
 * 
 * // Sending an example "getServerTime" request:
 * 
 *  // Construct new request
 *  String method = "getServerTime";
 *  int requestID = 0;
 *  JSONRPC2Request request = new JSONRPC2Request(method, requestID);
 * 
 *  // Send request
 *  JSONRPC2Response response = null;
 * 
 *  try {
 *          response = mySession.send(request);
 * 
 *  } catch (JSONRPC2SessionException e) {
 * 
 *          System.err.println(e.getMessage());
 *          // handle exception...
 *  }
 * 
 *  // Print response result / error
 *  if (response.indicatesSuccess())
 * 	System.out.println(response.getResult());
 *  else
 * 	System.out.println(response.getError().getMessage());
 * 
 * </pre>
 *
 * @author Vladimir Dzhuvinov
 * @version 1.7.1 (2011-08-23)
 */
public class JSONRPC2Session {


	/** 
	 * The server URL, which protocol must be HTTP or HTTPS. 
	 *
	 * <p>Example URL: "http://jsonrpc.example.com:8080"
	 */
	private URL url;


	/**
	 * The client-session options.
	 */
	private JSONRPC2SessionOptions options;


	/**
	 * Custom HTTP URL connection configurator.
	 */
	private ConnectionConfigurator connectionConfigurator;
	
	
	/**
	 * Optional HTTP raw response inspector.
	 */
	private RawResponseInspector responseInspector;
	
	
	/**
	 * Optional HTTP cookie store. 
	 */
	private Set<HttpCookie> cookies = new HashSet<HttpCookie>();


	/**
	 * Trust-all-certs (including self-signed) SSL socket factory.
	 */
	private SSLSocketFactory trustAllSocketFactory;


	/**
	 * Creates a new client session to a JSON-RPC 2.0 server at the
	 * specified URL. Uses a default {@link JSONRPC2SessionOptions} 
	 * instance.
	 *
	 * @param url The server URL, e.g. "http://jsonrpc.example.com:8080".
	 */
	public JSONRPC2Session (final URL url) {

		if (! url.getProtocol().equals("http") && ! url.getProtocol().equals("https"))	
			throw new IllegalArgumentException("The URL protocol must be HTTP or HTTPS");

		this.url = url;


		// Initialise the trust-all-certs SSL socket factory

		TrustManager[] trustAllCerts = new TrustManager[] {
				new X509TrustManager() {
					public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[]{}; }
					public void checkClientTrusted(X509Certificate[] certs, String authType) { }
					public void checkServerTrusted(X509Certificate[] certs, String authType) { }
				}
		};

		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new SecureRandom());
			trustAllSocketFactory = sc.getSocketFactory();

		} catch (Exception e) {
			// ignore
		}

		// Default session options
		options = new JSONRPC2SessionOptions();

		// Null connection configurator
		connectionConfigurator = null;
	}


	/**
	 * Gets the JSON-RPC 2.0 server URL.
	 *
	 * @return The server URL.
	 */
	public URL getURL() {

		return url;
	}


	/**
	 * Sets the JSON-RPC 2.0 server URL.
	 *
	 * @param url The server URL.
	 */
	public void setURL(final URL url) {

		this.url = url;
	}


	/**
	 * Gets the JSON-RPC 2.0 client session options.
	 *
	 * @return The client session options.
	 */
	public JSONRPC2SessionOptions getOptions() {

		return options;
	}


	/**
	 * Sets the JSON-RPC 2.0 client session options.
	 *
	 * @param options The client session options, must not be {@code null}.
	 */
	public void setOptions(final JSONRPC2SessionOptions options) {

		if (options == null)
			throw new NullPointerException("Client session options must not be null");

		this.options = options;
	}


	/**
	 * Gets the custom HTTP URL connection configurator.
	 *
	 * @since 1.5
	 *
	 * @return The connection configurator, {@code null} if none is set.
	 */
	public ConnectionConfigurator getConnectionConfigurator() {

		return connectionConfigurator;
	}


	/**
	 * Specifies a custom HTTP URL connection configurator. It will be
	 * {@link ConnectionConfigurator#configure applied} to each new HTTP
	 * connection after the {@link JSONRPC2SessionOptions session options}
	 * are applied and before the connection is established.
	 *
	 * <p>This method may be used to set custom HTTP request headers, 
	 * timeouts or other properties.
	 *
	 * @since 1.5
	 *
	 * @param connectionConfigurator A custom HTTP URL connection 
	 *                               configurator, {@code null} to remove
	 *                               a previously set one.
	 */
	public void setConnectionConfigurator(final ConnectionConfigurator connectionConfigurator) {

		this.connectionConfigurator = connectionConfigurator;
	}
	
	
	/**
	 * Gets the optional inspector for the raw HTTP responses.
	 * 
	 * @since 1.6
	 * 
	 * @return The optional inspector for the raw HTTP responses, {@code null} 
	 *         if none is set.
	 */
	public RawResponseInspector getRawResponseInspector() {
		
		return responseInspector;
	}
	
	
	/**
	 * Specifies an optional inspector for the raw HTTP responses to JSON-RPC
	 * 2.0 requests and notifications. Its {@link RawResponseInspector#inspect
	 * inspect} method will be called upon reception of a HTTP response.
	 * 
	 * <p>You can use the {@link RawResponseInspector} interface to retrieve
	 * the unparsed response content and headers.
	 * 
	 * @since 1.6
	 * 
	 * @param responseInspector An optional inspector for the raw HTTP 
	 *                          responses, {@code null} to remove a previously
	 *                          set one.
	 */
	public void setRawResponseInspector(final RawResponseInspector responseInspector) {
		
		this.responseInspector = responseInspector;
	}
	
	
	/**
	 * Gets all HTTP cookies currently stored in the client.
	 * 
	 * @return The HTTP cookies, or empty set if none were set by the server or
	 *         cookies are not {@link JSONRPC2SessionOptions#acceptsCookies
	 *         accepted}.
	 */
	public Set<HttpCookie> getCookies() {
		
		return cookies;
	}
	


	/**
	 * Applies the required headers to the specified URL connection.
	 *
	 * @param con The URL connection which must be open.
	 */
	private void applyHeaders(final URLConnection con) {

		// Add "Content-Type" header?
		if (options.getRequestContentType() != null)
			con.setRequestProperty("Content-Type", options.getRequestContentType());

		// Add "Origin" header?
		if (options.getOrigin() != null)
			con.setRequestProperty("Origin", options.getOrigin());
		
		// Add "Cookie" headers?
		if (options.acceptsCookies()) {
			
			Iterator <HttpCookie> it = cookies.iterator();
			
			StringBuilder buf = new StringBuilder();
			
			while (it.hasNext()) {
				
				HttpCookie c = it.next();
				
				buf.append(c.toString());
				
				// look ahead
				if (it.hasNext())
					buf.append("; ");
			}
			
			con.setRequestProperty("Cookie", buf.toString());
		}
	}
	
	
	/**
	 * Stores the cookies found the specified HTTP headers.
	 * 
	 * @param The HTTP headers to examine for "Set-Cookie" headers. Must not be
	 *        {@code null}.
	 */
	private void storeCookies(final Map <String,List<String>> headers) {
		
		if (headers == null)
			throw new NullPointerException("The HTTP headers must not be null");
		
		Iterator <Map.Entry<String,List<String>>> it = headers.entrySet().iterator();
		
		while (it.hasNext()) {
			
			Map.Entry <String,List<String>> h = it.next();
			
			// Careful: for some reason HttpURLConnection allows null header names!
			if (  h          == null                       ||
			      h.getKey() == null                       || 
			    ! h.getKey().equalsIgnoreCase("Set-Cookie")  )
				continue; // skip to next header
			
			Iterator <String> it2 = h.getValue().iterator();
			
			while (it2.hasNext()) {
				
				String cookieField = it2.next();
				
				if (cookieField == null)
					continue; // skip
				
				try {
					
					cookies.addAll(HttpCookie.parse(cookieField));
					
				} catch (IllegalArgumentException e) {
					// skip
					continue;
				}
			}
		}
	}


	/** 
	 * Sends a JSON-RPC 2.0 request using HTTP POST and returns the server
	 * response.
	 *
	 * @param request The JSON-RPC 2.0 request to send.
	 *
	 * @return The JSON-RPC 2.0 response returned by the server.
	 *
	 * @throws JSONRPC2SessionException On a network error, unexpected HTTP 
	 *                                  response content type or invalid 
	 *                                  JSON-RPC 2.0 response.
	 */
	public JSONRPC2Response send(final JSONRPC2Request request)
		throws JSONRPC2SessionException {

		// Open HTTP connection
		URLConnection con = null;

		try {

			con = url.openConnection();

		} catch (IOException e) {

			throw new JSONRPC2SessionException(
					"Network exception", 
					JSONRPC2SessionException.NETWORK_EXCEPTION,
					e);
		}

		applyHeaders(con);

		// Set POST mode
		con.setDoOutput(true);

		// Set trust all certs SSL factory?
		if (con instanceof HttpsURLConnection && options.trustsAllCerts()) 
			((HttpsURLConnection)con).setSSLSocketFactory(trustAllSocketFactory);

		// Apply connection configurator?
		if (connectionConfigurator != null)
			connectionConfigurator.configure((HttpURLConnection)con);

		// Send request encoded as JSON
		try {
			OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
			wr.write(request.toString());
			wr.flush();
			wr.close();

		} catch (IOException e) {

			throw new JSONRPC2SessionException(
					"Network exception",
					JSONRPC2SessionException.NETWORK_EXCEPTION,
					e);
		}

		// Get the response

		RawResponse rawResponse = null;

		try {

			rawResponse = RawResponse.parse((HttpURLConnection)con);

		} catch (IOException e) {

			throw new JSONRPC2SessionException(
					"Network exception",
					JSONRPC2SessionException.NETWORK_EXCEPTION,
					e);
		}
		
		if (responseInspector != null)
			responseInspector.inspect(rawResponse);
		
		if (options.acceptsCookies())
			storeCookies(rawResponse.getHeaderFields());

		// Check response content type?
		if (options.getAllowedResponseContentTypes() != null) {

			String mimeType = rawResponse.getContentType();

			if (! options.isAllowedResponseContentType(mimeType)) {

				throw new JSONRPC2SessionException(
						"The server returned an unexpected content type '" + mimeType + "' response", 
						JSONRPC2SessionException.UNEXPECTED_CONTENT_TYPE);
			}
		}

		// Parse and return the response
		JSONRPC2Response response = null;

		try {
			response = JSONRPC2Response.parse(rawResponse.getContent(), 
					options.preservesParseOrder(), 
					options.ignoresVersion(),
					options.parsesNonStdAttributes());

		} catch (JSONRPC2ParseException e) {

			throw new JSONRPC2SessionException(
					"Invalid JSON-RPC 2.0 response",
					JSONRPC2SessionException.BAD_RESPONSE,
					e);
		}

		// Response ID must match the request ID, except for
		// -32700 (parse error), -32600 (invalid request) and 
		// -32603 (internal error)

		Object reqID = request.getID();
		Object resID = response.getID();

		if (reqID != null && resID !=null && reqID.toString().equals(resID.toString()) ) {

			// ok
		}
		else if (reqID == null && resID == null) {

			// ok
		}
		else if (! response.indicatesSuccess() && ( response.getError().getCode() == -32700 ||
				response.getError().getCode() == -32600 ||
				response.getError().getCode() == -32603    )) {

			// ok
		}
		else {
			throw new JSONRPC2SessionException(
					"Invalid JSON-RPC 2.0 response: ID mismatch: Returned " + resID.toString() + ", expected " + reqID.toString(),
					JSONRPC2SessionException.BAD_RESPONSE);
		}


		return response;
	}


	/**
	 * Sends a JSON-RPC 2.0 notification using HTTP POST. Note that 
	 * contrary to requests, notifications produce no server response.
	 *
	 * @param notification The JSON-RPC 2.0 notification to send.
	 *
	 * @throws JSONRPC2SessionException On a network error.
	 */
	public void send(final JSONRPC2Notification notification)
		throws JSONRPC2SessionException {

		// Open HTTP connection
		URLConnection con = null;

		try {
			con = url.openConnection();

		} catch (IOException e) {

			throw new JSONRPC2SessionException(
					"Network exception", 
					JSONRPC2SessionException.NETWORK_EXCEPTION,
					e);
		}

		applyHeaders(con);

		// Set POST mode
		con.setDoOutput(true);

		// Set trust all certs SSL factory?
		if (con instanceof HttpsURLConnection && options.trustsAllCerts()) 
			((HttpsURLConnection)con).setSSLSocketFactory(trustAllSocketFactory);


		// Apply connection configurator?
		if (connectionConfigurator != null)
			connectionConfigurator.configure((HttpURLConnection)con);


		// Send notification encoded as JSON
		try {
			OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
			wr.write(notification.toString());
			wr.flush();
			wr.close();

		} catch (IOException e) {

			throw new JSONRPC2SessionException(
					"Network exception",
					JSONRPC2SessionException.NETWORK_EXCEPTION,
					e);
		}


		// Get the response /for the inspector only/

		RawResponse rawResponse = null;
		
		try {

			rawResponse = RawResponse.parse((HttpURLConnection)con);

		} catch (IOException e) {

			throw new JSONRPC2SessionException(
					"Network exception",
					JSONRPC2SessionException.NETWORK_EXCEPTION,
					e);
		}
		
		if (responseInspector != null)
			responseInspector.inspect(rawResponse);
		
		if (options.acceptsCookies())
			storeCookies(rawResponse.getHeaderFields());
	}
}

