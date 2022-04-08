package us.codecraft.webmagic.proxy;

import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.manager.Page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple ProxyProvider. Provide proxy as round-robin without heartbeat and error check. It can be used when all proxies are stable.
 * @author code4crafter@gmail.com
 *         Date: 17/4/16
 *         Time: 10:18
 * @since 0.7.0
 */
public class SimpleProxyProvider implements ProxyProvider {

    private final List<Proxy> proxies;

    private final AtomicInteger pointer;

    /**
     * Creates a simple proxy provider with a list of proxies.
     * @param proxies the list of proxies to set as an attribute
     */
    public SimpleProxyProvider(List<Proxy> proxies) {
        this(proxies, new AtomicInteger(-1));
    }

    /**
     * Creates a list from the proxies set as parameters and returns a new simple proxy provider with it. 
     * @param proxies the proxies to use to form a list
     * @return a new simple proxy provider.
     */
    public static SimpleProxyProvider from(Proxy... proxies) {
        List<Proxy> proxiesTemp = new ArrayList<Proxy>(proxies.length);
        for (Proxy proxy : proxies) {
            proxiesTemp.add(proxy);
        }
        return new SimpleProxyProvider(Collections.unmodifiableList(proxiesTemp));
    }

    @Override
    public void returnProxy(Proxy proxy, Page page, Task task) {
        //Donothing
    }

    @Override
    public Proxy getProxy(Task task) {
        return proxies.get(incrForLoop());
    }
    
    /**
     * Creates a simple proxy provider with a list of proxies and a pointer.
     * @param proxies the list of proxies to set as an attribute
     * @param pointer the atomic integer to set as a pointer attribute.
     */
    private SimpleProxyProvider(List<Proxy> proxies, AtomicInteger pointer) {
        this.proxies = proxies;
        this.pointer = pointer;
    }

    /**
     * Returns an incrementer for loop.
     * @return an incrementer.
     */
    private int incrForLoop() {
        int p = pointer.incrementAndGet();
        int size = proxies.size();
        if (p < size) {
            return p;
        }
        while (!pointer.compareAndSet(p, p % size)) {
            p = pointer.get();
        }
        return p % size;
    }

}
