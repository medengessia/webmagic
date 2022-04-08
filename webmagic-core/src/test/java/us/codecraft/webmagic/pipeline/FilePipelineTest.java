package us.codecraft.webmagic.pipeline;

import org.junit.BeforeClass;
import org.junit.Test;

import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.manager.Request;
import us.codecraft.webmagic.manager.ResultItems;
import us.codecraft.webmagic.manager.Site;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

/**
 * Created by ywooer on 2014/5/6 0006.
 */
public class FilePipelineTest {

    private static ResultItems resultItems;
    private static Task task;

    @BeforeClass
    public static void before() {
        resultItems = new ResultItems();
        resultItems.put("content", "webmagic 爬虫工具"); // crawler tool
        Request request = new Request("http://www.baidu.com");
        resultItems.setRequest(request);

        task = new Task() {
            @Override
            public String getUUID() {
                return UUID.randomUUID().toString();
            }

            @Override
            public Site getSite() {
                return null;
            }
        };
    }
    @Test
    public void testProcess() {
        FilePipeline filePipeline = new FilePipeline();
        filePipeline.process(resultItems, task);
    }
    
    @Test
    public void testFilePipelineCreation () {
    	FilePipeline filePipeline = new FilePipeline("path");
    	assertEquals("path\\", filePipeline.getPath());
    }
    
}
