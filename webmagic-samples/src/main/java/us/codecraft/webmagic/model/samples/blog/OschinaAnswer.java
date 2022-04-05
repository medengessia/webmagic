package us.codecraft.webmagic.model.samples.blog;

import us.codecraft.webmagic.manager.Page;
import us.codecraft.webmagic.manager.Site;
import us.codecraft.webmagic.model.*;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.HelpUrl;
import us.codecraft.webmagic.model.annotation.TargetUrl;
import us.codecraft.webmagic.model.page.OOSpider;

/**
 * @author code4crafter@gmail.com <br>
 */
@TargetUrl("http://www.oschina.net/question/\\d+_\\d+*")
@HelpUrl("http://www.oschina.net/question/*")
@ExtractBy(value = "//ul[@class='list']/li[@class='Answer']", multi = true)
public class OschinaAnswer implements AfterExtractor{

    @ExtractBy("//img/@title")
    private String user;

    @ExtractBy("//div[@class='detail']")
    private String content;

    public static void main(String[] args) {
        OOSpider.create(Site.me(), OschinaAnswer.class).addUrl("http://www.oschina.net/question/567527_120597").run();
    }

    @Override
    public void afterProcess(Page page) {

    }
}
