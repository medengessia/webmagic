package us.codecraft.webmagic.manager;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author code4crafter@gmail.com
 *         Date: 17/4/8
 */
public class HttpRequestBody implements Serializable {

    private static final long serialVersionUID = 5659170945717023595L;

    public static abstract class ContentType {

        public static final String JSON = "application/json";

        public static final String XML = "text/xml";

        public static final String FORM = "application/x-www-form-urlencoded";

        public static final String MULTIPART = "multipart/form-data";
    }

    private byte[] body;

    private String contentType;

    private String encoding;

    public HttpRequestBody() {
    }

    /**
     * Creates a request body.
     * @param body the table of bytes
     * @param contentType the type of the content
     * @param encoding the encoding being used
     */
    public HttpRequestBody(byte[] body, String contentType, String encoding) {
        this.body = body;
        this.contentType = contentType;
        this.encoding = encoding;
    }

    /**
     * Gets the content's type.
     * @return the content's type.
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Gets the encoding.
     * @return the encoding.
     */
    public String getEncoding() {
        return encoding;
    }
    
    /**
     * Gets the body.
     * @return the body.
     */
    public byte[] getBody() {
        return body;
    }

    /**
     * Sets the table of bytes.
     * @param body a new table of bytes to set the body with
     */
    public void setBody(byte[] body) {
        this.body = body;
    }

    /**
     * Sets the content's type.
     * @param contentType the new type of the content.
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Sets the encoding.
     * @param encoding the new encoding to set the attribute with
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Returns a new http request body with json format.
     * @param json the json string to use in this operation
     * @param encoding the encoding to use
     * @return a new http request body with json format.
     */
    public static HttpRequestBody json(String json, String encoding) {
        try {
            return new HttpRequestBody(json.getBytes(encoding), ContentType.JSON, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("illegal encoding " + encoding, e);
        }
    }

    /**
     * Returns a new http request body with xml format.
     * @param xml the xml string to use in this operation
     * @param encoding the encoding to use
     * @return a new http request body with xml format.
     */
    public static HttpRequestBody xml(String xml, String encoding) {
        try {
            return new HttpRequestBody(xml.getBytes(encoding), ContentType.XML, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("illegal encoding " + encoding, e);
        }
    }

    /**
     * Creates a customised http request body.
     * @param body the table of bytes required
     * @param contentType the content's type
     * @param encoding the encoding being used
     * @return the new http request body.
     */
    public static HttpRequestBody custom(byte[] body, String contentType, String encoding) {
        return new HttpRequestBody(body, contentType, encoding);
    }

    /**
     * Creates a new http request body with a specific format.
     * @param params the map containing the parameters
     * @param encoding the encoding being used
     * @return a new http request body with a specific format.
     */
    public static HttpRequestBody form(Map<String,Object> params, String encoding){
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(params.size());
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            nameValuePairs.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
        }
        try {
            return new HttpRequestBody(URLEncodedUtils.format(nameValuePairs, encoding).getBytes(encoding), ContentType.FORM, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("illegal encoding " + encoding, e);
        }
    }

}
