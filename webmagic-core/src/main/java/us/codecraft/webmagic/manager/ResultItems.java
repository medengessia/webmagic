package us.codecraft.webmagic.manager;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Object contains extract results.<br>
 * It is contained in Page and will be processed in pipeline.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 * @see Page
 * @see us.codecraft.webmagic.pipeline.Pipeline
 */
public class ResultItems {

    private Map<String, Object> fields = new LinkedHashMap<String, Object>();

    private Request request;

    private boolean skip;

    /**
     * Gets the wanted key of fields attribute.
     * @param <T> a container of T-type objects.
     * @param key the string we are looking for in the map fields
     * @return the cast of type T of the key got in fields.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        Object o = fields.get(key);
        if (o == null) {
            return null;
        }
        return (T) fields.get(key);
    }

    /**
     * Gets the fields.
     * @return the fields.
     */
    public Map<String, Object> getAll() {
        return fields;
    }

    /**
     * Puts the key and the value set as parameters in fields.
     * @param <T> a container of T-type objects
     * @param key the string to put in the map fields
     * @param value the associated value
     * @return the modified result items
     */
    public <T> ResultItems put(String key, T value) {
        fields.put(key, value);
        return this;
    }

    /**
     * Gets the request.
     * @return the request.
     */
    public Request getRequest() {
        return request;
    }

    /**
     * Sets the request.
     * @param request the request to be set as an attribute
     * @return the modified result items.
     */
    public ResultItems setRequest(Request request) {
        this.request = request;
        return this;
    }

    /**
     * Whether to skip the result.<br>
     * Result which is skipped will not be processed by Pipeline.
     *
     * @return whether to skip the result
     */
    public boolean isSkip() {
        return skip;
    }


    /**
     * Set whether to skip the result.<br>
     * Result which is skipped will not be processed by Pipeline.
     *
     * @param skip whether to skip the result
     * @return this
     */
    public ResultItems setSkip(boolean skip) {
        this.skip = skip;
        return this;
    }

    @Override
    public String toString() {
        return "ResultItems{" +
                "fields=" + fields +
                ", request=" + request +
                ", skip=" + skip +
                '}';
    }
}
