package us.codecraft.webmagic.downloader;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;

/**
 * @author code4crafter@gmail.com
 *         Date: 17/4/8
 *         Time: 19:43
 * @since 0.7.0
 */
public class HttpClientRequestContext {

    private HttpUriRequest httpUriRequest;

    private HttpClientContext httpClientContext;

    /**
     * Gets the http url request.
     * @return the http url request.
     */
    public HttpUriRequest getHttpUriRequest() {
        return httpUriRequest;
    }

    /**
     * Sets the http url request.
     * @param httpUriRequest the http url request to use in order to set it as the attribute
     */
    public void setHttpUriRequest(HttpUriRequest httpUriRequest) {
        this.httpUriRequest = httpUriRequest;
    }

    /**
     * Gets the http client context.
     * @return the http client context.
     */
    public HttpClientContext getHttpClientContext() {
        return httpClientContext;
    }

    /**
     * Sets the http client context.
     * @param httpClientContext the http client context to use in order to set it as the attribute
     */
    public void setHttpClientContext(HttpClientContext httpClientContext) {
        this.httpClientContext = httpClientContext;
    }

}
