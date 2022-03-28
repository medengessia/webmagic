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

    int getValue() {
        return value;
    }

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
