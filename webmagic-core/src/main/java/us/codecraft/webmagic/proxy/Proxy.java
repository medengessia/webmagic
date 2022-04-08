package us.codecraft.webmagic.proxy;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;

public class Proxy {

    private String scheme;

    private String host;

    private int port;

    private String username;

    private String password;

    /**
     * Creates a new proxy.
     * @param uri a unique uri to define the proxy
     * @return the created proxy.
     */
    public static Proxy create(final URI uri) {
        Proxy proxy = new Proxy(uri.getHost(), uri.getPort(), uri.getScheme());
        String userInfo = uri.getUserInfo();
        if (userInfo != null) {
            String[] up = userInfo.split(":");
            if (up.length == 1) {
                proxy.username = up[0].isEmpty() ? null : up[0];
            } else {
                proxy.username = up[0].isEmpty() ? null : up[0];
                proxy.password = up[1].isEmpty() ? null : up[1];
            }
        }
        return proxy;
    }

    /**
     * Creates a proxy with an host and a port.
     * @param host the host for the proxy
     * @param port the port for the proxy
     */
    public Proxy(String host, int port) {
        this(host, port, null);
    }

    /**
     * Creates a proxy with an host, a port and a scheme.
     * @param host the host for the proxy
     * @param port the port for the proxy
     * @param scheme the scheme for the proxy
     */
    public Proxy(String host, int port, String scheme) {
        this.host = host;
        this.port = port;
        this.scheme = scheme;
    }

    /**
     * Creates a proxy with an host, a port, a username and a password.
     * @param host the host for the proxy
     * @param port the port for the proxy
     * @param username the username for the proxy
     * @param password the password for the proxy
     */
    public Proxy(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    /**
     * Gets the scheme.
     * @return the scheme.
     */
    public String getScheme() {
        return scheme;
    }

    /**
     * Sets the scheme.
     * @param scheme the string to set as an attribute for scheme
     */
    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    /**
     * Gets the host.
     * @return the host.
     */
	public String getHost() {
        return host;
    }

	/**
	 * Gets the port.
	 * @return the port.
	 */
    public int getPort() {
        return port;
    }

    /**
     * Gets the username.
     * @return the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the password. 
     * @return the password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Builds an uri.
     * @return the created uri.
     */
    public URI toURI() {
        final StringBuilder userInfoBuffer = new StringBuilder();
        if (username != null) {
            userInfoBuffer.append(urlencode(username));
        }
        if (password != null) {
            userInfoBuffer.append(":").append(urlencode(password));
        }
        final String userInfo = StringUtils.defaultIfEmpty(userInfoBuffer.toString(), null);
        URI uri;
        try {
            uri = new URI(scheme, userInfo, host, port, null, null, null);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        return uri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Proxy proxy = (Proxy) o;

        if (port != proxy.port) return false;
        if (host != null ? !host.equals(proxy.host) : proxy.host != null) return false;
        if (scheme != null ? !scheme.equals(proxy.scheme) : proxy.scheme != null) return false;
        if (username != null ? !username.equals(proxy.username) : proxy.username != null) return false;
        return password != null ? password.equals(proxy.password) : proxy.password == null;
    }

    @Override
    public int hashCode() {
        int result = host != null ? host.hashCode() : 0;
        result = 31 * result + port;
        result = 31 * result + (scheme != null ? scheme.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return this.toURI().toString();
    }
    
    /**
     * The encoded url.
     *  @param s the string to encode as an url
     *  @return the encoded url.
     *  @throws IllegalArgumentException
     */
    private String urlencode(String s) {
        String enc = StandardCharsets.UTF_8.name();
        try {
            return URLEncoder.encode(s, enc);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
