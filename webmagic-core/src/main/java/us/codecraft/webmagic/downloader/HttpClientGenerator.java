package us.codecraft.webmagic.downloader;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.SystemUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.CookieStore;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.codecraft.webmagic.manager.Site;

/**
 * @author code4crafter@gmail.com <br>
 * @since 0.4.0
 */
public class HttpClientGenerator {

	private transient Logger logger = LoggerFactory.getLogger(getClass());

    private PoolingHttpClientConnectionManager connectionManager;

    /**
     * Generates a http client generator.
     */
    public HttpClientGenerator() {
        Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", buildSSLConnectionSocketFactory())
                .build();
        connectionManager = new PoolingHttpClientConnectionManager(reg);
        connectionManager.setDefaultMaxPerRoute(100);
    }

    /**
     * Sets the pool size.
     * @param poolSize the integer to set the pool size with
     * @return the http client generator.
     */
    public HttpClientGenerator setPoolSize(int poolSize) {
        connectionManager.setMaxTotal(poolSize);
        return this;
    }

    /**
     * Gets the client.
     * @param site the site whose client is generated
     * @return the generated client.
     */
    public CloseableHttpClient getClient(Site site) {
        return generateClient(site);
    }
    
    /**
     * Builds a SSL connection socket factory.
     * @return the SSL connection socket factory.
     */
    private SSLConnectionSocketFactory buildSSLConnectionSocketFactory() {
        try {
            SSLContext sslContext = createIgnoreVerifySSL();
            String[] supportedProtocols;
            if (SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_11)) {
                supportedProtocols = new String[] { "SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3" };
            } else {
                supportedProtocols = new String[] { "SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2" };
            }
            logger.debug("supportedProtocols: {}", String.join(", ", supportedProtocols));
            return new SSLConnectionSocketFactory(sslContext, supportedProtocols,
                    null,
                    new DefaultHostnameVerifier()); // 优先绕过安全证书 Bypass security certificates in preference
        } catch (KeyManagementException e) {
            logger.error("ssl connection fail", e);
        } catch (NoSuchAlgorithmException e) {
            logger.error("ssl connection fail", e);
        }
        return SSLConnectionSocketFactory.getSocketFactory();
	}

    /**
     * Creates a ignore case for verifying SSL.
     * @return the SSL context.
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    private SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        // 实现一个(implement one)X509TrustManager接口，用于绕过验证，不用修改里面的方法 (Interface, used to bypass authentication without modifying the methods inside)
        X509TrustManager trustManager = new X509TrustManager() {

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

        };

        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, new TrustManager[] { trustManager }, null);
        return sc;
	}

    /**
     * Generates a client for a site.
     * @param site the site whose client is generated
     * @return the http client to generate.
     */
    private CloseableHttpClient generateClient(Site site) {
        HttpClientBuilder httpClientBuilder = HttpClients.custom();

        httpClientBuilder.setConnectionManager(connectionManager);
        if (site.getUserAgent() != null) {
            httpClientBuilder.setUserAgent(site.getUserAgent());
        } else {
            httpClientBuilder.setUserAgent("");
        }
        if (site.isUseGzip()) {
            httpClientBuilder.addInterceptorFirst(new HttpRequestInterceptor() {

                public void process(
                        final HttpRequest request,
                        final HttpContext context) throws HttpException, IOException {
                    if (!request.containsHeader("Accept-Encoding")) {
                        request.addHeader("Accept-Encoding", "gzip");
                    }
                }
            });
        }
        //解决(settle)post/redirect/post 302跳转问题 (Jump question)
        httpClientBuilder.setRedirectStrategy(new CustomRedirectStrategy());

        SocketConfig.Builder socketConfigBuilder = SocketConfig.custom();
        socketConfigBuilder.setSoKeepAlive(true).setTcpNoDelay(true);
        socketConfigBuilder.setSoTimeout(site.getTimeOut());
        SocketConfig socketConfig = socketConfigBuilder.build();
        httpClientBuilder.setDefaultSocketConfig(socketConfig);
        connectionManager.setDefaultSocketConfig(socketConfig);
        httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(site.getRetryTimes(), true));
        generateCookie(httpClientBuilder, site);
        return httpClientBuilder.build();
    }

    /**
     * Generates a cookie.
     * @param httpClientBuilder the http client builder to disable in cookie management, then to reset
     * @param site the site whose cookies will be used
     */
    private void generateCookie(HttpClientBuilder httpClientBuilder, Site site) {
        if (site.isDisableCookieManagement()) {
            httpClientBuilder.disableCookieManagement();
            return;
        }
        CookieStore cookieStore = new BasicCookieStore();
        for (Map.Entry<String, String> cookieEntry : site.getCookies().entrySet()) {
            BasicClientCookie cookie = new BasicClientCookie(cookieEntry.getKey(), cookieEntry.getValue());
            cookie.setDomain(site.getDomain());
            cookieStore.addCookie(cookie);
        }
        for (Map.Entry<String, Map<String, String>> domainEntry : site.getAllCookies().entrySet()) {
            for (Map.Entry<String, String> cookieEntry : domainEntry.getValue().entrySet()) {
                BasicClientCookie cookie = new BasicClientCookie(cookieEntry.getKey(), cookieEntry.getValue());
                cookie.setDomain(domainEntry.getKey());
                cookieStore.addCookie(cookie);
            }
        }
        httpClientBuilder.setDefaultCookieStore(cookieStore);
    }

}
