package us.codecraft.webmagic;

import us.codecraft.webmagic.manager.Request;

/**
 * Listener of Spider on page processing. Used for monitor and such on.
 *
 * @author code4crafer@gmail.com
 * @since 0.5.0
 */
public interface SpiderListener {

    public void onSuccess(Request request);

    /**
     * @deprecated Use {@link #onError(Request, Exception)} instead.
     */
    @Deprecated
    public void onError(Request request);

    default void onError(Request request, Exception e) {
        this.onError(request, e);
    }

}
