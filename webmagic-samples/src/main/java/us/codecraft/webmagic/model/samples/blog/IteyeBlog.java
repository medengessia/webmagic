package us.codecraft.webmagic.model.samples.blog;

import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.manager.Site;
import us.codecraft.webmagic.model.annotation.TargetUrl;
import us.codecraft.webmagic.model.page.OOSpider;

/**
 * @author code4crafter@gmail.com <br>
 * Date: 13-8-2 <br>
 * Time: 上午7:52 <br>
 */
@TargetUrl("http://*.iteye.com/blog/*")
public class IteyeBlog implements Blog{

    @ExtractBy("//title")
    private String title;

    @ExtractBy(value = "div#blog_content",type = ExtractBy.Type.Css)
    private String content;

    @Override
    public String toString() {
        return "IteyeBlog{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    public static void main(String[] args) {
        OOSpider.create(Site.me(), IteyeBlog.class).addUrl("http://flashsword20.iteye.com/blog").run();
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
