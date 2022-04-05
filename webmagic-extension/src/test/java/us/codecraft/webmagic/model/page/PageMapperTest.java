package us.codecraft.webmagic.model.page;

import org.junit.Test;

import us.codecraft.webmagic.model.github.GithubRepoApi;
import us.codecraft.webmagic.model.page.PageMapper;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author code4crafter@gmail.com
 *         Date: 2017/6/3
 *         Time: 下午3:23
 */
public class PageMapperTest {

    private PageMocker pageMocker = new PageMocker();

    @Test
    public void test_get() throws Exception {
        PageMapper<GithubRepoApi> pageMapper = new PageMapper<GithubRepoApi>(GithubRepoApi.class);
        GithubRepoApi githubRepo = pageMapper.get(pageMocker.getMockJsonPage());
        assertThat(githubRepo.getName()).isEqualTo("webmagic");
    }

}
