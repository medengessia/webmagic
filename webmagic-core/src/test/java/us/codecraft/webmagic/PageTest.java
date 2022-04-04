package us.codecraft.webmagic;

import org.junit.Test;

import us.codecraft.webmagic.selector.extender.Html;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author Matthieu Medeng
 */
public class PageTest {
	
	@SuppressWarnings("deprecation")
	@Test
	public void testPageDisplayed () {
		Page myPage = new Page();
		Request req = new Request("https://github.com/code4craft/webmagic");
		List<String> reqs = new ArrayList<>();
		reqs.add("C");
		reqs.add("D");
		myPage.setRequest(req);
		Html ht = new Html("A", "B");
		myPage.setHtml(ht);
		myPage.setRawText(null);
		myPage.setUrl(ht);
		myPage.setHeaders(new HashMap<String, List<String>>());
		myPage.setStatusCode(2);
		myPage.addTargetRequest(req);
		myPage.addTargetRequest("new");
		myPage.addTargetRequests(reqs, 1);
		myPage.setCharset("E");
		myPage.setBytes(new byte[2]);
		assertEquals(myPage.toString(), "Page{" +
                                        "request=" + myPage.getRequest() +
                                        ", resultItems=" + myPage.getResultItems() +
                                        ", html=" + myPage.getHtml() +
                                        ", json=" + myPage.getJson() +
                                        ", rawText='" + myPage.getRawText() + '\'' +
                                        ", url=" + myPage.getUrl() +
                                        ", headers=" + myPage.getHeaders() +
                                        ", statusCode=" + myPage.getStatusCode() +
                                        ", downloadSuccess=" + myPage.isDownloadSuccess() +
                                        ", targetRequests=" + myPage.getTargetRequests() +
                                        ", charset='" + myPage.getCharset() + '\'' +
                                        ", bytes=" + Arrays.toString(myPage.getBytes()) +
                                        '}');
	}

}
