package com.complexible.stardog.examples.handler;


import com.stardog.http.server.undertow.HttpServiceLoader;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtHttpHandler implements HttpHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExtHttpHandler.class);

	private final io.undertow.server.HttpHandler mNext;
	private final HttpServiceLoader.ServerContext mContext;


	public ExtHttpHandler(final HttpHandler theNext, final HttpServiceLoader.ServerContext theContext) {
		LOGGER.error("Loading ExtHttpHandler");
		mNext = theNext;
		mContext = theContext;
	}

	public void handleRequest(HttpServerExchange theExchange) throws Exception {
		LOGGER.error("ExtHttpHandler.handleRequest called");
		theExchange.getResponseHeaders().add(HttpString.tryFromString("TEST_HEADER"), "VALUE");
		mNext.handleRequest(theExchange);
	}
}
