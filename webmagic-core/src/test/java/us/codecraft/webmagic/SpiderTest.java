package us.codecraft.webmagic;

import org.junit.Ignore;
import org.junit.Test;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.processor.SimplePageProcessor;
import us.codecraft.webmagic.scheduler.Scheduler;
import us.codecraft.webmagic.thread.CountableThreadPool;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author code4crafter@gmail.com
 */
public class SpiderTest extends SuperSpiderTest {
	
	@Override
	protected SuperSpider createSpider() {
		return Spider.create(new SimplePageProcessor( "http://www.oschina.net/*"));
	}
	
	@Test
	public void testGetAll () {
		Spider spider = Spider.create(new SimplePageProcessor( "http://www.oschina.net/*"));
		List<Request> requests = new ArrayList<>();
		Request req = new Request("https://github.com/code4craft/webmagic");
		requests.add(req);
		spider.startRequests(requests);
		assertTrue(spider.getStartRequests() != null);
		assertTrue(spider.isSpawnUrl());
		assertTrue(spider.isDestroyWhenExit());
		List<String> urls = new ArrayList<>();
		urls.add("http://www.oschina.net/*");
		urls.add("https://github.com/code4craft/webmagic");
		List<ResultItems> got = spider.getAll(urls);
		assertTrue(spider.isSpawnUrl());
		assertTrue(spider.isDestroyWhenExit());
		assertTrue(spider.getStartRequests().isEmpty());
	}
	
	@Test
	public void testDestroyEach () {
		Spider spider = Spider.create(new SimplePageProcessor( "http://www.oschina.net/*"));
		Pipeline pip1 = new FilePipeline();
		Pipeline pip2 = new FilePipeline();
		spider.setThreadPool(new CountableThreadPool(1));
		spider.addPipeline(pip1);
		spider.addPipeline(pip2);
		spider.close();
		assertTrue(spider.getThreadPool().isShutdown());
	}
	
	@Test
	public void testComponentInitialisation () {
		Spider spider = Spider.create(new SimplePageProcessor( "http://www.oschina.net/*"));
		List<Request> requests = new ArrayList<>();
		Request req = new Request("https://github.com/code4craft/webmagic");
		requests.add(req);
		spider.startRequests(requests);
		assertNull(spider.getDownloader());
		assertTrue(spider.getPipelines().isEmpty());
		assertTrue(spider.getThreadPool() == null || spider.getThreadPool().isShutdown());
		assertTrue(spider.getStartRequests() != null);
		spider.initComponent();
		assertFalse(spider.getDownloader() == null);
		assertFalse(spider.getPipelines().isEmpty());
		assertFalse(spider.getThreadPool() == null || spider.getThreadPool().isShutdown());
		assertTrue(spider.getStartRequests().isEmpty());
	}
	
	@Test
	public void testAddRequests () {
		Spider spider = Spider.create(new SimplePageProcessor( "http://www.oschina.net/*"));
		List<Request> requests = new ArrayList<>();
		Request req = new Request("https://github.com/code4craft/webmagic");
		spider.startRequests(requests);
		assertTrue(spider.getStartRequests().isEmpty());
		spider.addRequest(req);
		assertTrue(spider.getStartRequests().isEmpty());
	}

    @Ignore("long time")
    @Test
    public void testStartAndStop() throws InterruptedException {
        Spider spider = (Spider) Spider.create(new SimplePageProcessor( "http://www.oschina.net/*")).addPipeline(new Pipeline() {
            @Override
            public void process(ResultItems resultItems, Task task) {
                System.out.println(1);
            }
        }).thread(1).addUrl("http://www.oschina.net/");
        spider.start();
        Thread.sleep(10000);
        spider.stop();
        Thread.sleep(10000);
        spider.start();
        Thread.sleep(10000);
    }

    @Ignore("long time")
    @Test
    public void testWaitAndNotify() throws InterruptedException {
        for (int i = 0; i < 10000; i++) {
            System.out.println("round " + i);
            testRound();
        }
    }

    private void testRound() {
        Spider spider = (Spider) Spider.create(new PageProcessor() {

            private AtomicInteger count = new AtomicInteger();

            @Override
            public void process(Page page) {
                page.setSkip(true);
            }

            @Override
            public Site getSite() {
                return Site.me().setSleepTime(0);
            }
        }).setDownloader(new Downloader() {
            @Override
            public Page download(Request request, Task task) {
                return new Page().setRawText("");
            }

            @Override
            public void setThread(int threadNum) {

            }
        }).setScheduler(new Scheduler() {

            private AtomicInteger count = new AtomicInteger();

            private Random random = new Random();

            @Override
            public void push(Request request, Task task) {

            }

            @Override
            public synchronized Request poll(Task task) {
                if (count.incrementAndGet() > 1000) {
                    return null;
                }
                if (random.nextInt(100)>90){
                    return null;
                }
                return new Request("test");
            }
        }).thread(10);
        spider.run();
    }

}
