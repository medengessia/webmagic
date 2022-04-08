package us.codecraft.webmagic.utils;

/**
 * @author yihua.huang@dianping.com
 */
public abstract class NumberUtils {

	/**
	 * Compares two long integers.
	 * @param o1 the first long integer
	 * @param o2 the second long integer
	 * @return 0 if both are equal, 1 if the first one is greater than the second one, -1 otherwise.
	 */
    public static int compareLong(long o1, long o2) {
        if (o1 < o2) {
            return -1;
        } else if (o1 == o2) {
            return 0;
        } else {
            return 1;
        }
    }
}
