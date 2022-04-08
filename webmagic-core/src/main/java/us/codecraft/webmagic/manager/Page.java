package us.codecraft.webmagic.manager;

import org.apache.commons.lang3.StringUtils;

import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.selector.extender.Html;
import us.codecraft.webmagic.selector.extender.Json;
import us.codecraft.webmagic.utils.HttpConstant;
import us.codecraft.webmagic.utils.UrlUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Object storing extracted result and urls to fetch.<br>
 * Not thread safe.<br>
 * Main method：                                               <br>
 * {@link #getUrl()} get url of current page                   <br>
 * {@link #getHtml()}  get content of current page                 <br>
 * {@link #putField(String, Object)}  save extracted result            <br>
 * {@link #getResultItems()} get extract results to be used in {@link us.codecraft.webmagic.pipeline.Pipeline}<br>
 * {@link #addTargetRequests(java.util.List)} {@link #addTargetRequest(String)} add urls to fetch                 <br>
 *
 * @author code4crafter@gmail.com <br>
 * @see us.codecraft.webmagic.downloader.Downloader
 * @see us.codecraft.webmagic.processor.PageProcessor
 * @since 0.1.0
 */
public class Page {

    private Request request;

    private ResultItems resultItems = new ResultItems();

    private Html html;

    private Json json;

    private String rawText;

    private Selectable url;

    private Map<String,List<String>> headers;

    private int statusCode = HttpConstant.StatusCode.CODE_200;

    private boolean downloadSuccess = true;

    private byte[] bytes;

    private List<Request> targetRequests = new ArrayList<Request>();

    private String charset;
    
    public Page() {
    }

    /**
     * Instantiates a page with download failure.
     * @return a page with download failure.
     */
    public static Page fail(){
        Page page = new Page();
        page.setDownloadSuccess(false);
        return page;
    }

    /**
     * Sets the skip at the resultItems attribute.
     * @param skip the boolean to set the skip with
     * @return the page with altered resultItems.
     */
    public Page setSkip(boolean skip) {
        resultItems.setSkip(skip);
        return this;

    }

    /**
     * store extract results
     *
     * @param key key
     * @param field field
     */
    public void putField(String key, Object field) {
        resultItems.put(key, field);
    }

    /**
     * get html content of page
     *
     * @return html
     */
    public Html getHtml() {
        if (html == null) {
            html = new Html(rawText, request.getUrl());
        }
        return html;
    }

    /**
     * get json content of page
     *
     * @return json
     * @since 0.5.0
     */
    public Json getJson() {
        if (json == null) {
            json = new Json(rawText);
        }
        return json;
    }

    /**
     * @param html html
     * @deprecated since 0.4.0
     * The html is parse just when first time of calling {@link #getHtml()}, so use {@link #setRawText(String)} instead.
     */
    public void setHtml(Html html) {
        this.html = html;
    }

    /**
     * Gets the target request.
     * @return the target request.
     */
    public List<Request> getTargetRequests() {
        return targetRequests;
    }

    /**
     * add urls to fetch
     *
     * @param requests requests
     */
    public void addTargetRequests(List<String> requests) {
        for (String s : requests) {
            if (StringUtils.isBlank(s) || s.equals("#") || s.startsWith("javascript:")) {
                continue;
            }
            s = UrlUtils.canonicalizeUrl(s, url.toString());
            targetRequests.add(new Request(s));
        }
    }

    /**
     * add urls to fetch
     *
     * @param requests requests
     * @param priority priority
     */
    public void addTargetRequests(List<String> requests, long priority) {
        for (String s : requests) {
            if (StringUtils.isBlank(s) || s.equals("#") || s.startsWith("javascript:")) {
                continue;
            }
            s = UrlUtils.canonicalizeUrl(s, url.toString());
            targetRequests.add(new Request(s).setPriority(priority));
        }
    }

    /**
     * add url to fetch
     *
     * @param requestString requestString
     */
    public void addTargetRequest(String requestString) {
        if (StringUtils.isBlank(requestString) || requestString.equals("#")) {
            return;
        }
        requestString = UrlUtils.canonicalizeUrl(requestString, url.toString());
        targetRequests.add(new Request(requestString));
    }

    /**
     * add requests to fetch
     *
     * @param request request
     */
    public void addTargetRequest(Request request) {
        targetRequests.add(request);
    }

    /**
     * get url of current page
     *
     * @return url of current page
     */
    public Selectable getUrl() {
        return url;
    }

    /**
     * Sets the url.
     * @param url the url to set the attribute with
     */
    public void setUrl(Selectable url) {
        this.url = url;
    }

    /**
     * get request of current page
     *
     * @return request
     */
    public Request getRequest() {
        return request;
    }

    /**
     * Sets the request in the page and the resultItems.
     * @param request the request to change the attribute by
     */
    public void setRequest(Request request) {
        this.request = request;
        this.resultItems.setRequest(request);
    }

    /**
     * Gets the result items.
     * @return the result items.
     */
    public ResultItems getResultItems() {
        return resultItems;
    }

    /**
     * Gets the status' code.
     * @return the status' code.
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the status' code.
     * @param statusCode the code to set as the status'.
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Gets the raw text.
     * @return the raw text.
     */
    public String getRawText() {
        return rawText;
    }

    /**
     * Sets the raw text.
     * @param rawText the raw text to replace the former one by
     * @return the modified page.
     */
    public Page setRawText(String rawText) {
        this.rawText = rawText;
        return this;
    }

    /**
     * Gets the headers.
     * @return the headers.
     */
    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    /**
     * Sets the headers.
     * @param headers the map containing the headers to set as an attribute.
     */
    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    /**
     * Tells whether the download is successful or not.
     * @return true if the download is successful, false otherwise.
     */
    public boolean isDownloadSuccess() {
        return downloadSuccess;
    }

    /**
     * Sets the download's status.
     * @param downloadSuccess the boolean value of the download's status
     */
    public void setDownloadSuccess(boolean downloadSuccess) {
        this.downloadSuccess = downloadSuccess;
    }

    /**
     * Gets the bytes.
     * @return the bytes.
     */
    public byte[] getBytes() {
        return bytes;
    }

    /**
     * Sets the bytes.
     * @param bytes the table of bytes to set the bytes with
     */
    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
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
     * @param charset the string to set the charset with
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    @Override
    public String toString() {
        return "Page{" +
                "request=" + request +
                ", resultItems=" + resultItems +
                ", html=" + html +
                ", json=" + json +
                ", rawText='" + rawText + '\'' +
                ", url=" + url +
                ", headers=" + headers +
                ", statusCode=" + statusCode +
                ", downloadSuccess=" + downloadSuccess +
                ", targetRequests=" + targetRequests +
                ", charset='" + charset + '\'' +
                ", bytes=" + Arrays.toString(bytes) +
                '}';
    }
}
