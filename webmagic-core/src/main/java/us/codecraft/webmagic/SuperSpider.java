package us.codecraft.webmagic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.Scheduler;
import us.codecraft.webmagic.thread.CountableThreadPool;
import us.codecraft.webmagic.utils.UrlUtils;

/**
 * Here is an abstract class to lighten the Spider class and its four modules.
 * There are 26 method definitions and 4 abstract method declarations when Spider 
 * contains now 30 methods instead of 60. The enum Status has been separated as well.
 * Finally, most of the getters and setters are gathered in this super class.
 * 
 * @author Medeng Matthieu
 * @see Downloader
 * @see Scheduler
 * @see PageProcessor
 * @see Pipeline
 * @since 0.1.0
 *
 */
public abstract class SuperSpider implements Runnable, Task {

	protected Downloader downloader;

	protected ExecutorService executorService;

    protected PageProcessor pageProcessor;

	protected List<Pipeline> pipelines = new ArrayList<Pipeline>();

	protected Site site;

	protected List<SpiderListener> spiderListeners;

	protected List<Request> startRequests;

	protected boolean spawnUrl = true;

	protected CountableThreadPool threadPool;

	protected String uuid;

    protected boolean exitWhenComplete = true;

    protected int threadNum = 1;

	protected final static int STAT_INIT = 0;

    protected final static int STAT_RUNNING = 1;

	protected AtomicInteger stat = new AtomicInteger(STAT_INIT);

	protected Date startTime;

    protected final AtomicLong pageCount = new AtomicLong(0);

    /**
	 * create a spider with pageProcessor.
	 *
	 * @param pageProcessor pageProcessor
	 */
	public SuperSpider(PageProcessor pageProcessor) {
	    this.pageProcessor = pageProcessor;
	    this.site = pageProcessor.getSite();
	}

    /**
     * Set an uuid for spider.<br>
     * Default uuid is domain of site.<br>
     *
     * @param uuid uuid
     * @return this
     */
    public SuperSpider setUUID(String uuid) {
        this.uuid = uuid;
        return this;
    }

    /**
     * Get the list of spider listeners.
     * @return the list of spider listeners
     */
    public List<SpiderListener> getSpiderListeners() {
        return spiderListeners;
    }

    /**
     * Set a list of listeners for spider.
     * @param spiderListeners
     * @return this
     */
    public SuperSpider setSpiderListeners(List<SpiderListener> spiderListeners) {
        this.spiderListeners = spiderListeners;
        return this;
    }

    /**
     * Get the start time of the crawler.
     * @return the start time
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * Get running status by spider.
     *
     * @return running status
     * @see Status
     * @since 0.4.1
     */
    public Status getStatus() {
        return Status.fromValue(stat.get());
    }

    /**
     * Get thread count which is running
     *
     * @return thread count which is running
     * @since 0.4.1
     */
    public int getThreadAlive() {
        if (threadPool == null) {
            return 0;
        }
        return threadPool.getThreadAlive();
    }

    /**
     * Set the executor service
     * @param executorService
     * @return this
     */
    public SuperSpider setExecutorService(ExecutorService executorService) {
        checkIfRunning();
        this.executorService = executorService;
        return this;
    }

    /**
     * Whether add urls extracted to download.<br>
     * Add urls to download when it is true, and just download seed urls when it is false. <br>
     * DO NOT set it unless you know what it means!
     *
     * @param spawnUrl spawnUrl
     * @return this
     * @since 0.4.0
     */
    public SuperSpider setSpawnUrl(boolean spawnUrl) {
        this.spawnUrl = spawnUrl;
        return this;
    }

    /**
     * add a pipeline for Spider
     *
     * @param pipeline pipeline
     * @return this
     * @see #addPipeline(us.codecraft.webmagic.pipeline.Pipeline)
     * @deprecated
     */
    @Deprecated
	public SuperSpider pipeline(Pipeline pipeline) {
        return addPipeline(pipeline);
    }

    /**
     * add a pipeline for Spider
     *
     * @param pipeline pipeline
     * @return this
     * @see Pipeline
     * @since 0.2.1
     */
    public SuperSpider addPipeline(Pipeline pipeline) {
        checkIfRunning();
        this.pipelines.add(pipeline);
        return this;
    }

    /**
     * set pipelines for Spider
     *
     * @param pipelines pipelines
     * @return this
     * @see Pipeline
     * @since 0.4.1
     */
    public SuperSpider setPipelines(List<Pipeline> pipelines) {
        checkIfRunning();
        this.pipelines = pipelines;
        return this;
    }

    /**
     * clear the pipelines set
     *
     * @return this
     */
    public SuperSpider clearPipeline() {
        pipelines = new ArrayList<Pipeline>();
        return this;
    }

    /**
     * set the downloader of spider
     *
     * @param downloader downloader
     * @return this
     * @see #setDownloader(us.codecraft.webmagic.downloader.Downloader)
     * @deprecated
     */
    @Deprecated
	public SuperSpider downloader(Downloader downloader) {
        return setDownloader(downloader);
    }

    /**
     * set the downloader of spider
     *
     * @param downloader downloader
     * @return this
     * @see Downloader
     */
    public SuperSpider setDownloader(Downloader downloader) {
        checkIfRunning();
        this.downloader = downloader;
        return this;
    }

    /**
     * Indicates whether the exit is done when complete or not.
     * @return true if the exit is done when complete, false otherwise
     */
    public boolean isExitWhenComplete() {
        return exitWhenComplete;
    }

    /**
     * Exit when complete. <br>
     * True: exit when all url of the site is downloaded. <br>
     * False: not exit until call stop() manually.<br>
     *
     * @param exitWhenComplete exitWhenComplete
     * @return this
     */
    public SuperSpider setExitWhenComplete(boolean exitWhenComplete) {
        this.exitWhenComplete = exitWhenComplete;
        return this;
    }

    /**
     * Indicates whether the url is spawn or not.
     * @return true if the url is spawn, false otherwise 
     */
    public boolean isSpawnUrl() {
        return spawnUrl;
    }

	/**
     * Set startUrls of Spider.<br>
     * Prior to startUrls of Site.
     *
     * @param startUrls startUrls
     * @return this
     */
    public SuperSpider startUrls(List<String> startUrls) {
        checkIfRunning();
        this.startRequests = UrlUtils.convertToRequests(startUrls);
        return this;
    }

    /**
     * Set startUrls of Spider.<br>
     * Prior to startUrls of Site.
     *
     * @param startRequests startRequests
     * @return this
     */
    public SuperSpider startRequests(List<Request> startRequests) {
        checkIfRunning();
        this.startRequests = startRequests;
        return this;
    }

    /**
     * start a thread
     */
    public void runAsync() {
        Thread thread = new Thread(this);
        thread.setDaemon(false);
        thread.start();
    }

    /**
     * Get page count downloaded by spider.
     *
     * @return total downloaded page count
     * @since 0.4.1
     */
    public long getPageCount() {
        return pageCount.get();
    }   

    /**
     * start with more than one threads
     *
     * @param threadNum threadNum
     * @return this
     */
    public SuperSpider thread(int threadNum) {
        checkIfRunning();
        this.threadNum = threadNum;
        if (threadNum <= 0) {
            throw new IllegalArgumentException("threadNum should be more than one!");
        }
        return this;
    }
    
    @Override
    public String getUUID() {
        if (uuid != null) {
            return uuid;
        }
        if (site != null) {
            return site.getDomain();
        }
        uuid = UUID.randomUUID().toString();
        return uuid;
    }

    @Override
    public Site getSite() {
        return site;
    }
	
	/**
	 * Check if running
	 */
    protected void checkIfRunning() {
        if (stat.get() == STAT_RUNNING) {
            throw new IllegalStateException("Spider is already running!");
        }
    }
    
    public abstract void run();

	public abstract SuperSpider addUrl(String... urls);

	public abstract SuperSpider setScheduler(Scheduler scheduler);

	public abstract Spider scheduler(Scheduler Scheduler);

}
