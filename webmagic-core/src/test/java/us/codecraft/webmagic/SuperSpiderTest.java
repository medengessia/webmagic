package us.codecraft.webmagic;

import org.junit.Before;
import org.junit.Test;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.pipeline.Pipeline;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Matthieu Medeng
 */
public abstract class SuperSpiderTest {
	
	protected SuperSpider mySpider;
	
	protected abstract SuperSpider createSpider();
	
	@Before
	public void init() {
		this.mySpider = this.createSpider();
	}
	
	@Test
	public void testSpiderCreation () {
		assertSame(mySpider.getSite(), mySpider.getPageProcessor().getSite());
		mySpider.getPageProcessor().getSite().addCookie("A", "B", "C");
		assertTrue(mySpider.getPageProcessor().getSite().getAllCookies().containsKey("A"));
		assertEquals(mySpider.getPageProcessor().getSite().toString(), "Site{" +
                     "domain='" + mySpider.getPageProcessor().getSite().getDomain() + '\'' +
                     ", userAgent='" + mySpider.getPageProcessor().getSite().getUserAgent() + '\'' +
                     ", cookies=" + mySpider.getPageProcessor().getSite().getCookies() +
                     ", charset='" + mySpider.getPageProcessor().getSite().getCharset() + '\'' +
                     ", sleepTime=" + mySpider.getPageProcessor().getSite().getSleepTime() +
                     ", retryTimes=" + mySpider.getPageProcessor().getSite().getRetryTimes() +
                     ", cycleRetryTimes=" + mySpider.getPageProcessor().getSite().getCycleRetryTimes() +
                     ", timeOut=" + mySpider.getPageProcessor().getSite().getTimeOut() +
                     ", acceptStatCode=" + mySpider.getPageProcessor().getSite().getAcceptStatCode() +
                     ", headers=" + mySpider.getPageProcessor().getSite().getHeaders() +
                     '}');
	}
	
	@Test
	public void testPipelines () {
		Pipeline pip1 = new FilePipeline();
		List<Pipeline> pips = new ArrayList<>();
		pips.add(pip1);
		assertTrue(mySpider.getPipelines().isEmpty());
		mySpider.setPipelines(pips);
		assertFalse(mySpider.getPipelines().isEmpty());
		mySpider.clearPipeline();
		assertTrue(mySpider.getPipelines().isEmpty());
	}
	
	@Test
	public void testThreadNum () {
		assertEquals(1, mySpider.getThreadNum());
		mySpider.thread(5);
		assertEquals(5, mySpider.getThreadNum());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIllegalArgumentExceptionIsThrown () throws IllegalArgumentException {
		assertEquals(1, mySpider.getThreadNum());
		try {
			mySpider.thread(2);
		}
		catch (IllegalArgumentException e) {
			fail();
		}
		mySpider.thread(-1);
	}
	
	@Test(expected = IllegalStateException.class)
	public void testCheckIfRunning () throws IllegalStateException {
		try {
			mySpider.checkIfRunning();
		}
		catch (IllegalStateException e) {
			fail();
		}
		mySpider.setStat(1);
		mySpider.checkIfRunning();
	}

}
