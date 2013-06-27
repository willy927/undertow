package io.undertow;

import io.undertow.predicate.Predicate;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.DateHandler;
import io.undertow.server.handlers.HttpContinueReadHandler;
import io.undertow.server.handlers.HttpTraceHandler;
import io.undertow.server.handlers.IPAddressAccessControlHandler;
import io.undertow.server.handlers.NameVirtualHostHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.PredicateHandler;
import io.undertow.server.handlers.RedirectHandler;
import io.undertow.server.handlers.SetHeaderHandler;
import io.undertow.server.handlers.URLDecodingHandler;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.websockets.api.WebSocketSessionHandler;
import io.undertow.websockets.core.handler.WebSocketProtocolHandshakeHandler;
import io.undertow.websockets.impl.WebSocketSessionConnectionCallback;

/**
 * Utility class with convenience methods for dealing with handlers
 *
 * @author Stuart Douglas
 */
public class Handlers {

    /**
     * Creates a new path handler, with the default handler specified
     *
     * @param defaultHandler The default handler
     * @return A new path handler
     */
    public static PathHandler path(final HttpHandler defaultHandler) {
        return new PathHandler(defaultHandler);
    }

    /**
     * Creates a new path handler
     *
     * @return A new path handler
     */
    public static PathHandler path() {
        return new PathHandler();
    }

    /**
     * Creates a new virtual host handler
     *
     * @return A new virtual host handler
     */
    public static NameVirtualHostHandler virtualHost() {
        return new NameVirtualHostHandler();
    }

    /**
     * Creates a new virtual host handler using the provided default handler
     *
     * @return A new virtual host handler
     */
    public static NameVirtualHostHandler virtualHost(final HttpHandler defaultHandler) {
        return new NameVirtualHostHandler().setDefaultHandler(defaultHandler);
    }

    /**
     * Creates a new virtual host handler that uses the provided handler as the root handler for the given hostnames.
     *
     * @param hostHandler The host handler
     * @param hostnames   The host names
     * @return A new virtual host handler
     */
    public static NameVirtualHostHandler virtualHost(final HttpHandler hostHandler, String... hostnames) {
        NameVirtualHostHandler handler = new NameVirtualHostHandler();
        for (String host : hostnames) {
            handler.addHost(host, hostHandler);
        }
        return handler;
    }

    /**
     * Creates a new virtual host handler that uses the provided handler as the root handler for the given hostnames.
     *
     * @param defaultHandler The default handler
     * @param hostHandler    The host handler
     * @param hostnames      The host names
     * @return A new virtual host handler
     */
    public static NameVirtualHostHandler virtualHost(final HttpHandler defaultHandler, final HttpHandler hostHandler, String... hostnames) {
        return virtualHost(hostHandler, hostnames).setDefaultHandler(defaultHandler);
    }

    /**
     * @param sessionHandler The web socket session handler
     * @return The web socket handler
     */
    public static WebSocketProtocolHandshakeHandler websocket(final WebSocketSessionHandler sessionHandler) {
        return new WebSocketProtocolHandshakeHandler(new WebSocketSessionConnectionCallback(sessionHandler));
    }

    /**
     * @param sessionHandler The web socket session handler
     * @param next           The handler to invoke if the web socket connection fails
     * @return The web socket handler
     */
    public static WebSocketProtocolHandshakeHandler websocket(final WebSocketSessionHandler sessionHandler, final HttpHandler next) {
        return new WebSocketProtocolHandshakeHandler(new WebSocketSessionConnectionCallback(sessionHandler), next);
    }

    /**
     * Return a new resource handler
     *
     * @param resourceManager The resource manager to use
     * @return A new resource handler
     */
    public static ResourceHandler resource(final ResourceManager resourceManager) {
        return new ResourceHandler().setResourceManager(resourceManager).setDirectoryListingEnabled(false);
    }

    /**
     * Returns a new redirect handler
     *
     * @param location The redirect location
     * @return A new redirect handler
     */
    public static RedirectHandler redirect(final String location) {
        return new RedirectHandler(location);
    }

    /**
     * Returns a new HTTP trace handler. This handler will handle HTTP TRACE
     * requests as per the RFC.
     * <p/>
     * WARNING: enabling trace requests may leak information, in general it is recomended that
     * these be disabled for security reasons.
     *
     * @param next The next handler in the chain
     * @return A HTTP trace handler
     */
    public static HttpTraceHandler trace(final HttpHandler next) {
        return new HttpTraceHandler(next);
    }

    /**
     * Returns a new HTTP handler that sets the Date: header.
     *
     * @param next The next handler in the chain
     * @return A new date handler
     */
    public static DateHandler date(final HttpHandler next) {
        return new DateHandler(next);
    }

    /**
     * Returns a new predicate handler, that will delegate to one of the two provided handlers based on the value of the
     * provided predicate.
     *
     * @param predicate    The predicate
     * @param trueHandler  The handler that will be executed if the predicate is true
     * @param falseHandler The handler that will be exected if the predicate is false
     * @return A new predicate handler
     * @see Predicate
     * @see io.undertow.predicate.Predicates
     */
    public static PredicateHandler predicate(final Predicate predicate, final HttpHandler trueHandler, final HttpHandler falseHandler) {
        return new PredicateHandler(predicate, trueHandler, falseHandler);
    }

    /**
     * Returns a handler that sets a response header
     *
     * @param next        The next handler in the chain
     * @param headerName  The name of the header
     * @param headerValue The header value
     * @return A new set header handler
     */
    public static SetHeaderHandler header(final HttpHandler next, final String headerName, final String headerValue) {
        return new SetHeaderHandler(next, headerName, headerValue);
    }

    /**
     * Returns a new handler that can allow or deny access to a resource based on IP address
     *
     * @param next         The next handler in the chain
     * @param defaultAllow Determine if a non-matching address will be allowed by default
     * @return A new IP access control handler
     */
    public static final IPAddressAccessControlHandler ipAccessControl(final HttpHandler next, boolean defaultAllow) {
        return new IPAddressAccessControlHandler(next).setDefaultAllow(defaultAllow);
    }

    /**
     * A handler that automatically handles HTTP 100-continue responses, by sending a continue
     * response when the first attempt is made to read from the request channel.
     *
     * @param next The next handler in the chain
     * @return A new continue handler
     */
    public static final HttpContinueReadHandler httpContinueRead(final HttpHandler next) {
        return new HttpContinueReadHandler(next);
    }

    /**
     * A handler that will decode the URL, query parameters and to the specified charset.
     * <p/>
     * If you are using this handler you must set the {@link io.undertow.UndertowOptions#DECODE_URL} parameter to false.
     * <p/>
     * This is not as efficient as using the parsers built in UTF-8 decoder. Unless you need to decode to something other
     * than UTF-8 you should rely on the parsers decoding instead.
     *
     * @param next    The next handler in the chain
     * @param charset The charset to decode to
     * @return a new url decoding handler
     */
    public static final URLDecodingHandler urlDecoding(final HttpHandler next, final String charset) {
        return new URLDecodingHandler(next, charset);
    }

    private Handlers() {

    }

}