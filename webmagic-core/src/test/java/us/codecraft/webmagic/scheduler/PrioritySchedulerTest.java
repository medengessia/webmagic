package us.codecraft.webmagic.scheduler;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.manager.Request;
import us.codecraft.webmagic.manager.Site;

/**
 * @author code4crafter@gmail.com <br>
 */
public class PrioritySchedulerTest {

    private PriorityScheduler priorityScheduler = new PriorityScheduler();

    private Task task = new Task() {
        @Override
        public String getUUID() {
            return "1";
        }

        @Override
        public Site getSite() {
            return null;
        }
    };

    @Test
    public void testDifferentPriority() {
        Request request = new Request("a");
        request.setPriority(100);
        priorityScheduler.push(request,task);

        request = new Request("b");
        request.setPriority(900);
        priorityScheduler.push(request,task);

        request = new Request("c");
        priorityScheduler.push(request,task);

        request = new Request("d");
        request.setPriority(-900);
        priorityScheduler.push(request,task);

        Request poll = priorityScheduler.poll(task);
        assertEquals("b",poll.getUrl());
        poll = priorityScheduler.poll(task);
        assertEquals("a",poll.getUrl());
        poll = priorityScheduler.poll(task);
        assertEquals("c",poll.getUrl());
        poll = priorityScheduler.poll(task);
        assertEquals("d",poll.getUrl());
    }

    @Test
    public void testNoPriority() {
        Request request = new Request("a");
        priorityScheduler.push(request,task);

        request = new Request("b");
        priorityScheduler.push(request,task);

        request = new Request("c");
        priorityScheduler.push(request,task);

        Request poll = priorityScheduler.poll(task);
        assertEquals("a",poll.getUrl());

        poll = priorityScheduler.poll(task);
        assertEquals("b",poll.getUrl());

        poll = priorityScheduler.poll(task);
        assertEquals("c",poll.getUrl());
    }
}
