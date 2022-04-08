package us.codecraft.webmagic.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author code4crafter@gmail.com
 *         Date: 16/12/18
 *         Time: 上午10:16
 */
public class WMCollections {

	/**
	 * Returns a set of T-type objects.
	 * @param <T> a container of T-type objects
	 * @param t a bunch of T-type objects
	 * @return the set of the T-type objects.
	 */
    public static <T> Set<T> newHashSet(T... t){
        Set<T> set = new HashSet<T>(t.length);
        for (T t1 : t) {
            set.add(t1);
        }
        return set;
    }

    /**
     * Returns a list of T-type objects.
     * @param <T> a container of T-type objects
     * @param t a bunch of T-type objects
     * @return the list of the T-type objects.
     */
    public static <T> List<T> newArrayList(T... t){
        List<T> set = new ArrayList<T>(t.length);
        for (T t1 : t) {
            set.add(t1);
        }
        return set;
    }
}
