package us.codecraft.webmagic;

/**
 * An enum class formerly implemented within Spider class.
 * @author Medeng Matthieu
 *
 */
public enum Status {
    Init(0), Running(1), Stopped(2);

    private Status(int value) {
        this.value = value;
    }

    private int value;

    /**
     * Get the value of a status.
     * @return the value of a status. 
     */
    int getValue() {
        return value;
    }

    /**
     * Returns the corresponding status according to the entered parameter.
     * @param value the int value whose corresponding status is researched
     * @return the status whose value is set as parameter or the init status when there is no such status.
     */
    public static Status fromValue(int value) {
        for (Status status : Status.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        //default value
        return Init;
    }
}
