package us.codecraft.webmagic.manager;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.SuperSpider;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.pipeline.CollectorPipeline;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.pipeline.ResultItemsCollectorPipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.Scheduler;
import us.codecraft.webmagic.thread.CountableThreadPool;
import us.codecraft.webmagic.utils.UrlUtils;
import us.codecraft.webmagic.utils.WMCollections;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Entrance of a crawler.<br>
 * A spider contains four modules: Downloader, Scheduler, PageProcessor and
 * Pipeline.<br>
 * Every module is a field of Spider. <br>
 * The modules are defined in interface. <br>
 * You can customize a spider with various implementations of them. <br>
 * Examples: <br>
 * <br>
 * A simple crawler: <br>
 * Spider.create(new SimplePageProcessor("http://my.oschina.net/",
 * "http://my.oschina.net/*blog/*")).run();<br>
 * <br>
 * Store results to files by FilePipeline: <br>
 * Spider.create(new SimplePageProcessor("http://my.oschina.net/",
 * "http://my.oschina.net/*blog/*")) <br>
 * .pipeline(new FilePipeline("/data/temp/webmagic/")).run(); <br>
 * <br>
 * Use FileCacheQueueScheduler to store urls and cursor in files, so that a
 * Spider can resume the status when shutdown. <br>
 * Spider.create(new SimplePageProcessor("http://my.oschina.net/",
 * "http://my.oschina.net/*blog/*")) <br>
 * .scheduler(new FileCacheQueueScheduler("/data/temp/webmagic/cache/")).run(); <br>
 *
 * @author code4crafter@gmail.com <br>
 * @see Downloader
 * @see Scheduler
 * @see PageProcessor
 * @see Pipeline
 * @since 0.1.0
 */
public class Spider extends SuperSpider {
    
    protected Scheduler scheduler = new QueueScheduler();

    protected Logger logger = LoggerFactory.getLogger(getClass());    

    protected final static int STAT_STOPPED = 2;

    protected boolean destroyWhenExit = true;

    private ReentrantLock newUrlLock = new ReentrantLock();

    private Condition newUrlCondition = newUrlLock.newCondition();
    
    private int emptySleepTime = 30000;

    /**
     * create a spider with pageProcessor.
     *
     * @param pageProcessor pageProcessor
     * @return new spider
     * @see PageProcessor
     */
    public static Spider create(PageProcessor pageProcessor) {
        return new Spider(pageProcessor);
    }
    
    /**
     * create a spider with pageProcessor.
     *
     * @param spiderProcessor spiderProcessor
     * @see PageProcessor
     */
    public Spider (PageProcessor pageProcessor) {
        super(pageProcessor);
    }    
    
    /**
     * Get the scheduler.
     * @return the scheduler
     */
    public Scheduler getScheduler() {
        return scheduler;
    }
    
    /**
     * set scheduler for Spider
     *
     * @param scheduler scheduler
     * @return this
     * @see #setScheduler(us.codecraft.webmagic.scheduler.Scheduler)
     */
    @Deprecated
    public Spider scheduler(Scheduler scheduler) {
        return setScheduler(scheduler);
    }

    /**
     * set scheduler for Spider
     *
     * @param scheduler scheduler
     * @return this
     * @see Scheduler
     * @since 0.2.1
     */
    @Override
    public Spider setScheduler(Scheduler scheduler) {
        checkIfRunning();
        Scheduler oldScheduler = this.scheduler;
        this.scheduler = scheduler;
        if (oldScheduler != null) {
            Request request;
            while ((request = oldScheduler.poll(this)) != null) {
                this.scheduler.push(request, this);
            }
        }
        return this;
    }
    
    @Override
    public void run() {
        checkRunningStat();
        initComponent();
        logger.info("Spider {} started!",getUUID());
        while (!Thread.currentThread().isInterrupted() && stat.get() == STAT_RUNNING) {
            final Request request = scheduler.poll(this);
            if (request == null) {
                if (threadPool.getThreadAlive() == 0 && exitWhenComplete) {
                    break;
                }
                // wait until new url added
                waitNewUrl();
            } else {
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            processRequest(request);
                            onSuccess(request);
                        } catch (Exception e) {
                            onError(request, e);
                            logger.error("process request " + request + " error", e);
                        } finally {
                            pageCount.incrementAndGet();
                            signalNewUrl();
                        }
                    }
                });
            }
        }
        stat.set(STAT_STOPPED);
        // release some resources
        if (destroyWhenExit) {
            close();
        }
        logger.info("Spider {} closed! {} pages downloaded.", getUUID(), pageCount.get());
    }

    /**
     * close a spider by destroying all modules
     */
    public void close() {
        destroyEach(downloader);
        destroyEach(pageProcessor);
        destroyEach(scheduler);
        for (Pipeline pipeline : pipelines) {
            destroyEach(pipeline);
        }
        threadPool.shutdown();
    }

    /**
     * Process specific urls without url discovering.
     *
     * @param urls urls to process
     */
    public void test(String... urls) {
        initComponent();
        if (urls.length > 0) {
            for (String url : urls) {
                processRequest(new Request(url));
            }
        }
    }    

    /**
     * Add urls to crawl. <br>
     *
     * @param urls urls
     * @return this
     */
    @Override
	public SuperSpider addUrl(String... urls) {
		for (String url : urls) {
            addRequest(new Request(url));
        }
        signalNewUrl();
        return this;
	}

    /**
     * Download urls synchronizing.
     *
     * @param urls urls
     * @param <T> type of process result
     * @return list downloaded
     */
    public <T> List<T> getAll(Collection<String> urls) {
        destroyWhenExit = false;
        spawnUrl = false;
        if (startRequests!=null){
            startRequests.clear();
        }
        for (Request request : UrlUtils.convertToRequests(urls)) {
            addRequest(request);
        }
        CollectorPipeline collectorPipeline = getCollectorPipeline();
        pipelines.add(collectorPipeline);
        run();
        spawnUrl = true;
        destroyWhenExit = true;
        return collectorPipeline.getCollected();
    }  
    
    /**
     * Tells whether the urls are destroyed at the exit or not.
     * @return true if they have been destroyed, false otherwise
     */
    public boolean isDestroyWhenExit() {
		return destroyWhenExit;
	}

    /**
     * Get the first item of the urls. 
     * @param <T> a T-type object composite
     * @param url the url to process
     * @return the object T to get
     */
    public <T> T get(String url) {
        List<String> urls = WMCollections.newArrayList(url);
        List<T> resultItemses = getAll(urls);
        if (resultItemses != null && resultItemses.size() > 0) {
            return resultItemses.get(0);
        } else {
            return null;
        }
    }

    /**
     * Add urls with information to crawl.<br>
     *
     * @param requests requests
     * @return this
     */
    public Spider addRequest(Request... requests) {
        for (Request request : requests) {
            addRequest(request);
        }
        signalNewUrl();
        return this;
    }

    /**
     * starts the process
     */
    public void start() {
        runAsync();
    }

    /**
     * stop the process
     */
    public void stop() {
        if (stat.compareAndSet(STAT_RUNNING, STAT_STOPPED)) {
            logger.info("Spider " + getUUID() + " stop success!");
        } else {
            logger.info("Spider " + getUUID() + " stop fail!");
        }
    }

    /**
     * start with more than one threads
     *
     * @param executorService executorService to run the spider
     * @param threadNum threadNum
     * @return this
     */
    public Spider thread(ExecutorService executorService, int threadNum) {
        checkIfRunning();
        this.executorService = executorService;
        return (Spider) thread(threadNum);
    }
    
    /**
     * initialises a component
     */
    public void initComponent() {
        if (downloader == null) {
            this.downloader = new HttpClientDownloader();
        }
        if (pipelines.isEmpty()) {
            pipelines.add(new ConsolePipeline());
        }
        downloader.setThread(threadNum);
        if (threadPool == null || threadPool.isShutdown()) {
            if (executorService != null && !executorService.isShutdown()) {
                threadPool = new CountableThreadPool(threadNum, executorService);
            } else {
                threadPool = new CountableThreadPool(threadNum);
            }
        }
        if (startRequests != null) {
            for (Request request : startRequests) {
                addRequest(request);
            }
            startRequests.clear();
        }
        startTime = new Date();
    }
    
    /**
     * @deprecated Use {@link #onError(Request, Exception)} instead.
     */
    @Deprecated
    protected void onError(Request request) {
    }

    /**
     * assess the error of a request.
     * @param request the request to assess
     */
    protected void onError(Request request, Exception e) {
    	this.onError(request, e);
        if (CollectionUtils.isNotEmpty(spiderListeners)) {
            for (SpiderListener spiderListener : spiderListeners) {
                spiderListener.onError(request, e);
            }
        }
    }

    /**
     * assess the success of a request
     * @param request the request to assess
     */
    protected void onSuccess(Request request) {
        if (CollectionUtils.isNotEmpty(spiderListeners)) {
            for (SpiderListener spiderListener : spiderListeners) {
                spiderListener.onSuccess(request);
            }
        }
    }
    
    /**
     * make the thread sleep
     * @param time the duration of the sleep time
     */
    protected void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            logger.error("Thread interrupted when sleep",e);
        }
    }

    /**
     * extract and add requests
     * @param page the page to extract
     * @param spawnUrl true if the url is spawn, false otherwise
     */
    protected void extractAndAddRequests(Page page, boolean spawnUrl) {
        if (spawnUrl && CollectionUtils.isNotEmpty(page.getTargetRequests())) {
            for (Request request : page.getTargetRequests()) {
                addRequest(request);
            }
        }
    }
    
    /**
     * get the collector pipeline
     * @return the collector pipeline
     */
    protected CollectorPipeline getCollectorPipeline() {
        return new ResultItemsCollectorPipeline();
    }
    
    /**
     * destroy an object when it is closable
     * @param object the object to close
     */
    private void destroyEach(Object object) {
        if (object instanceof Closeable) {
            try {
                ((Closeable) object).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Process a request by downloading the associated page.
     * @param request the request to process
     */
    private void processRequest(Request request) {
        Page page;
        if (null != request.getDownloader()){
            page = request.getDownloader().download(request,this);
        }else {
            page = downloader.download(request, this);
        }
        if (page.isDownloadSuccess()){
            onDownloadSuccess(request, page);
        } else {
            onDownloaderFail(request);
        }
    }

    /**
     * handle the download when successful
     * @param request the request to process
     * @param page the page to process
     */
    private void onDownloadSuccess(Request request, Page page) {
        if (site.getAcceptStatCode().contains(page.getStatusCode())){
            pageProcessor.process(page);
            extractAndAddRequests(page, spawnUrl);
            if (!page.getResultItems().isSkip()) {
                for (Pipeline pipeline : pipelines) {
                    pipeline.process(page.getResultItems(), this);
                }
            }
        } else {
            logger.info("page status code error, page {} , code: {}", request.getUrl(), page.getStatusCode());
        }
        sleep(site.getSleepTime());
        return;
    }

    /**
     * handle a download when there is a fail and therefore retry
     * @param request the request to process
     */
    private void onDownloaderFail(Request request) {
        if (site.getCycleRetryTimes() == 0) {
            sleep(site.getSleepTime());
        } else {
            // for cycle retry
            doCycleRetry(request);
        }
    }

    /**
     * manage the retry cycles
     * @param request the request to process
     */
    private void doCycleRetry(Request request) {
        Object cycleTriedTimesObject = request.getExtra(Request.CYCLE_TRIED_TIMES);
        if (cycleTriedTimesObject == null) {
            addRequest(SerializationUtils.clone(request).setPriority(0).putExtra(Request.CYCLE_TRIED_TIMES, 1));
        } else {
            int cycleTriedTimes = (Integer) cycleTriedTimesObject;
            cycleTriedTimes++;
            if (cycleTriedTimes < site.getCycleRetryTimes()) {
                addRequest(SerializationUtils.clone(request).setPriority(0).putExtra(Request.CYCLE_TRIED_TIMES, cycleTriedTimes));
            }
        }
        sleep(site.getRetrySleepTime());
    }
    
    /**
     * check if spider is running
     */
    private void checkRunningStat() {
        while (true) {
            int statNow = stat.get();
            if (statNow == STAT_RUNNING) {
                throw new IllegalStateException("Spider is already running!");
            }
            if (stat.compareAndSet(statNow, STAT_RUNNING)) {
                break;
            }
        }
    }
    
    /**
     * add a request
     * @param request the request to add
     */
    private void addRequest(Request request) {
        if (site.getDomain() == null && request != null && request.getUrl() != null) {
            site.setDomain(UrlUtils.getDomain(request.getUrl()));
        }
        scheduler.push(request, this);
    }
    
    /**
     * implements the waiting of new urls
     */
    private void waitNewUrl() {
        newUrlLock.lock();
        try {
            //double check
            if (threadPool.getThreadAlive() == 0 && exitWhenComplete) {
                return;
            }
            newUrlCondition.await(emptySleepTime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.warn("waitNewUrl - interrupted, error {}", e);
        } finally {
            newUrlLock.unlock();
        }
    }
    
    /**
     * signal a new url
     */
    private void signalNewUrl() {
        try {
            newUrlLock.lock();
            newUrlCondition.signalAll();
        } finally {
            newUrlLock.unlock();
        }
    }
    
}
