package us.codecraft.webmagic.downloader;

import java.net.URI;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *支持(support)post 302跳转策略实现(jump policy implementation)类
 *HttpClient默认跳转(default jump)：httpClientBuilder.setRedirectStrategy(new LaxRedirectStrategy());
 *上述代码在(the above code is in)post/redirect/post这种情况下不会传递原有请求的数据信息。(In this case, the data information of the original request is not passed.) 
 *所以参考了下(So it refers to the next)SeimiCrawler这个项目的重定向策略。(The redirection policy for this project.)
 *原代码地址(original code address)：https://github.com/zhegexiaohuozi/SeimiCrawler/blob/master/project/src/main/java/cn/wanghaomiao/seimi/http/hc/SeimiRedirectStrategy.java
 */
public class CustomRedirectStrategy extends LaxRedirectStrategy {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
        URI uri = getLocationURI(request, response, context);
        String method = request.getRequestLine().getMethod();
        if ("post".equalsIgnoreCase(method)) {
            try {
                HttpRequestWrapper httpRequestWrapper = (HttpRequestWrapper) request;
                httpRequestWrapper.setURI(uri);
                httpRequestWrapper.removeHeaders("Content-Length");
                return httpRequestWrapper;
            } catch (Exception e) {
                logger.error("强转为(turn to)HttpRequestWrapper出错(make mistake)");
            }
            return new HttpPost(uri);
        } else {
            return new HttpGet(uri);
        }
    }
}
