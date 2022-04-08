package us.codecraft.webmagic.scheduler.component;

import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.manager.Request;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author code4crafer@gmail.com
 */
public class HashSetDuplicateRemover implements DuplicateRemover {

    private Set<String> urls = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    @Override
    public boolean isDuplicate(Request request, Task task) {
        return !urls.add(getUrl(request));
    }    

    @Override
    public void resetDuplicateCheck(Task task) {
        urls.clear();
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        return urls.size();
    }
    
    /**
     * Gets the url. 
     * @param request the request whose url will be taken.
     * @return the url.
     */
    protected String getUrl(Request request) {
        return request.getUrl();
    }
    
}
