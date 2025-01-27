package us.codecraft.webmagic.monitor;

import org.junit.Test;

import us.codecraft.webmagic.manager.Page;
import us.codecraft.webmagic.manager.Site;
import us.codecraft.webmagic.manager.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import javax.management.JMException;

/**
 * @author jerry_shenchao@163.com
 */
public class SeedUrlWithPortTest {

    @Test
    public void testSeedUrlWithPort() throws JMException {
        Spider spider = (Spider) Spider.create(new TempProcessor()).addUrl("http://www.hndpf.org:8889/");
        SpiderMonitor.instance().register(spider);
        spider.run();
    }
}

class TempProcessor implements PageProcessor {

    @Override
    public void process(Page page) {

    }

    @Override
    public Site getSite() {
        return Site.me();
    }
}
