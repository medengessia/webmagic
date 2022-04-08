package us.codecraft.webmagic.downloader;

import org.junit.Test;

import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.manager.Page;
import us.codecraft.webmagic.manager.Request;
import us.codecraft.webmagic.manager.Site;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author code4crafter@gmail.com
 *         Date: 2017/11/29
 *         Time: 下午1:32 (pm)
 */
public class SSLCompatibilityTest {

    @Test
    public void test_tls12() throws Exception {
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        Task task = Site.me().setCycleRetryTimes(5).toTask();
        Request request = new Request("https://juejin.im/");
        Page page = httpClientDownloader.download(request, task);
        assertThat(page.isDownloadSuccess()).isTrue();
    }
}
