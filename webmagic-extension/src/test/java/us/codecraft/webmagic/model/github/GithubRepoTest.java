package us.codecraft.webmagic.model.github;

import org.junit.Test;

import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.MockGithubDownloader;
import us.codecraft.webmagic.example.github.GithubRepo;
import us.codecraft.webmagic.manager.Site;
import us.codecraft.webmagic.manager.Spider;
import us.codecraft.webmagic.model.page.OOSpider;
import us.codecraft.webmagic.pipeline.PageModelPipeline;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author code4crafter@gmail.com <br>
 */
public class GithubRepoTest {

    @Test
    public void test() {
        ((Spider) OOSpider.create(Site.me().setSleepTime(0)
                , new PageModelPipeline<GithubRepo>() {
            @Override
            public void process(GithubRepo o, Task task) {
                assertThat(o.getStar()).isEqualTo(86);
                assertThat(o.getFork()).isEqualTo(70);
            }
        }, GithubRepo.class).addUrl("https://github.com/code4craft/webmagic").setDownloader(new MockGithubDownloader())).test("https://github.com/code4craft/webmagic");
    }

}
