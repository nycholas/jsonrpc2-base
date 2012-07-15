package com.thetransactioncompany.jsonrpc2.client;


/**
 * Interface allowing for inspection of the raw HTTP response to a JSON-RPC 2.0
 * request or notification. Can be used to retrieve the unparsed response 
 * content and headers.
 *
 * @since 1.6
 * @author Vladimir Dzhuvinov
 * @version 1.7.1 (2011-08-23)
 */
public interface RawResponseInspector {


	/**
	 * Allows for inspection of the specified raw HTTP response to a JSON-RPC
	 * 2.0 request or nofitication.
	 *
	 * @param rawResponse The raw HTTP response.
	 */
	public void inspect(final RawResponse rawResponse);

}
