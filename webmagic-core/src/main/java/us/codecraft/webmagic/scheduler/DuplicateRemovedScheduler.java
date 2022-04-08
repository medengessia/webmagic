package us.codecraft.webmagic.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.manager.Request;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;
import us.codecraft.webmagic.scheduler.component.HashSetDuplicateRemover;
import us.codecraft.webmagic.utils.HttpConstant;

/**
 * Remove duplicate urls and only push urls which are not duplicate.<br><br>
 *
 * @author code4crafer@gmail.com
 * @since 0.5.0
 */
public abstract class DuplicateRemovedScheduler implements Scheduler {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private DuplicateRemover duplicatedRemover = new HashSetDuplicateRemover();

    /**
     * Gets the duplicated remover.
     * @return the duplicated remover.
     */
    public DuplicateRemover getDuplicateRemover() {
        return duplicatedRemover;
    }

    /**
     * Sets the duplicated remover.
     * @param duplicatedRemover the remover to set as an attribute.
     * @return the modified duplicate removed scheduler.
     */
    public DuplicateRemovedScheduler setDuplicateRemover(DuplicateRemover duplicatedRemover) {
        this.duplicatedRemover = duplicatedRemover;
        return this;
    }

    @Override
    public void push(Request request, Task task) {
        logger.trace("get a candidate url {}", request.getUrl());
        if (shouldReserved(request) || noNeedToRemoveDuplicate(request) || !duplicatedRemover.isDuplicate(request, task)) {
            logger.debug("push to queue {}", request.getUrl());
            pushWhenNoDuplicate(request, task);
        }
    }

    /**
     * Tells whether the key Request.CYCLE_TRIED_TIMES in the map extras of a request is null or not.
     * @param request the request whose map will be seen as reserved or not
     * @return true if the key Request.CYCLE_TRIED_TIMES in the map extras of a request is not null, false otherwise.
     */
    protected boolean shouldReserved(Request request) {
        return request.getExtra(Request.CYCLE_TRIED_TIMES) != null;
    }

    /**
     * Tells whether the duplicated needs to be removed or not.
     * @param request the request whose method will get used
     * @return true if it is a need to remove the duplicated, false otherwise.
     */
    protected boolean noNeedToRemoveDuplicate(Request request) {
        return HttpConstant.Method.POST.equalsIgnoreCase(request.getMethod());
    }

    protected void pushWhenNoDuplicate(Request request, Task task) {

    }
    
}
