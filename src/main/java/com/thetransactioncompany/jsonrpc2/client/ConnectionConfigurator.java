package com.thetransactioncompany.jsonrpc2.client;


import java.net.HttpURLConnection;


/**
 * Interface allowing for additional configuration of HTTP URL connections, such
 * as setting custom headers or timeouts.
 *
 * @since 1.5
 * @author Vladimir Dzhuvinov
 * @version 1.7.1 (2011-07-18)
 */
public interface ConnectionConfigurator {


	/**
	 * Allows for additional configuration of the specified HTTP URL 
	 * connection before it is established. This may include setting of
	 * custom HTTP headers, request and read timeouts, etc.
	 *
	 * @param connection The HTTP URL connection to configure.
	 */
	public void configure(final HttpURLConnection connection);

}
