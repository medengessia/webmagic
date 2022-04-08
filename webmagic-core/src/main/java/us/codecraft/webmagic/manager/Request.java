package us.codecraft.webmagic.manager;

import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.utils.Experimental;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Object contains url to crawl.<br>
 * It contains some additional information.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
public class Request implements Serializable {

    private static final long serialVersionUID = 2062192774891352043L;

    public static final String CYCLE_TRIED_TIMES = "_cycle_tried_times";

    private String url;

    private String method;

    private HttpRequestBody requestBody;

    /**
     * this req use this downloader
     */
    private Downloader downloader;

    /**
     * Store additional information in extras.
     */
    private Map<String, Object> extras;

    /**
     * cookies for current url, if not set use Site's cookies
     */
    private Map<String, String> cookies = new HashMap<String, String>();

    private Map<String, String> headers = new HashMap<String, String>();

    /**
     * Priority of the request.<br>
     * The bigger will be processed earlier. <br>
     * @see us.codecraft.webmagic.scheduler.PriorityScheduler
     */
    private long priority;

    /**
     * When it is set to TRUE, the downloader will not try to parse response body to text.
     *
     */
    private boolean binaryContent = false;

    private String charset;

    public Request() {
    }

    /**
     * Creates a request with an url attribute.
     * @param url the url to set as an attribute
     */
    public Request(String url) {
        this.url = url;
    }

    /**
     * Gets the priority.
     * @return the priority.
     */
    public long getPriority() {
        return priority;
    }

    /**
     * Set the priority of request for sorting.<br>
     * Need a scheduler supporting priority.<br>
     * @see us.codecraft.webmagic.scheduler.PriorityScheduler
     *
     * @param priority priority
     * @return this
     */
    @Experimental
    public Request setPriority(long priority) {
        this.priority = priority;
        return this;
    }

    /**
     * Gets the extra.
     * @param <T> a container composed of type T objects
     * @param key the string to get in extras
     * @return the cast of type T of the key got in extras.
     */
    @SuppressWarnings("unchecked")
    public <T> T getExtra(String key) {
        if (extras == null) {
            return null;
        }
        return (T) extras.get(key);
    }

    /**
     * Puts the string key and the T-type value in extras.
     * @param <T> a container composed of type T objects
     * @param key the string to put in extras
     * @param value the T-type value to associate with key
     * @return the modified request.
     */
    public <T> Request putExtra(String key, T value) {
        if (extras == null) {
            extras = new HashMap<String, Object>();
        }
        extras.put(key, value);
        return this;
    }

    /**
     * Gets the url.
     * @return the url.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Gets the extras.
     * @return the extras.
     */
    public Map<String, Object> getExtras() {
        return extras;
    }

    /**
     * Sets the extras.
     * @param extras the map to replace the former one with
     * @return the modified request.
     */
    public Request setExtras(Map<String, Object> extras) {
        this.extras = extras;
        return this;
    }

    /**
     * Sets the url.
     * @param url the string to use to set the url
     * @return the modified request.
     */
    public Request setUrl(String url) {
        this.url = url;
        return this;
    }

    /**
     * The http method of the request. Get for default.
     * @return httpMethod
     * @see us.codecraft.webmagic.utils.HttpConstant.Method
     * @since 0.5.0
     */
    public String getMethod() {
        return method;
    }

    /**
     * Sets the method.
     * @param method the string to use to set the method
     * @return the modified request.
     */
    public Request setMethod(String method) {
        this.method = method;
        return this;
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (method != null ? method.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Request request = (Request) o;

        if (url != null ? !url.equals(request.url) : request.url != null) return false;
        return method != null ? method.equals(request.method) : request.method == null;
    }

    /**
     * Adds a cookie in the map.
     * @param name the name to use as key
     * @param value the associated value
     * @return the modified request.
     */
    public Request addCookie(String name, String value) {
        cookies.put(name, value);
        return this;
    }

    /**
     * Adds a header in the map.
     * @param name the name to use as key
     * @param value the associated value
     * @return the modified request.
     */
    public Request addHeader(String name, String value) {
        headers.put(name, value);
        return this;
    }

    /**
     * Gets the cookies.
     * @return the cookies.
     */
    public Map<String, String> getCookies() {
        return cookies;
    }

    /**
     * Gets the headers.
     * @return the headers.
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Gets the request body.
     * @return the request body.
     */
    public HttpRequestBody getRequestBody() {
        return requestBody;
    }

    /**
     * Sets the request body.
     * @param requestBody the request body to replace the former one by.
     */
    public void setRequestBody(HttpRequestBody requestBody) {
        this.requestBody = requestBody;
    }

    /**
     * Tells the status of binary content.
     * @return true if binary content, false otherwise.
     */
    public boolean isBinaryContent() {
        return binaryContent;
    }

    /**
     * Gets the downloader.
     * @return the downloader.
     */
    public Downloader getDownloader() {
        return downloader;
    }

    /**
     * Sets the downloader
     * @param downloader the downloader to be set as an attribute
     */
    public void setDownloader(Downloader downloader) {
        this.downloader = downloader;
    }

    /**
     * Sets the binary content.
     * @param binaryContent the boolean value of the binary content
     * @return the modified request
     */
    public Request setBinaryContent(boolean binaryContent) {
        this.binaryContent = binaryContent;
        return this;
    }

    /**
     * Gets the charset.
     * @return the charset.
     */
    public String getCharset() {
        return charset;
    }

    /**
     * Sets the charset.
     * @param charset the string to be set as a charset attribute
     * @return the modified request.
     */
    public Request setCharset(String charset) {
        this.charset = charset;
        return this;
    }

    @Override
    public String toString() {
        return "Request{" +
                "url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", extras=" + extras +
                ", priority=" + priority +
                ", headers=" + headers +
                ", cookies="+ cookies+
                '}';
    }

}
