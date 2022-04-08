package us.codecraft.webmagic.utils;

import java.io.File;

/**
 * Base object of file persistence.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
public class FilePersistentBase {

    protected String path;
 
    public static String PATH_SEPERATOR = "/";

    static {
        String property = System.getProperties().getProperty("file.separator");
        if (property != null) {
            PATH_SEPERATOR = property;
        }
    }

    /**
     * Sets the path.
     * @param path the path
     */
    public void setPath(String path) {
        if (!path.endsWith(PATH_SEPERATOR)) {
            path += PATH_SEPERATOR;
        }
        this.path = path;
    }

    /**
     * Gets the file with name fullName.
     * @param fullName the name for the file to create
     * @return the file with name fullName.
     */
    public File getFile(String fullName) {
        checkAndMakeParentDirecotry(fullName);
        return new File(fullName);
    }

    /**
     * Checks the parent directory using the name.
     * @param fullName the name to use to create a file
     */
    public void checkAndMakeParentDirecotry(String fullName) {
        int index = fullName.lastIndexOf(PATH_SEPERATOR);
        if (index > 0) {
            String path = fullName.substring(0, index);
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }

    /**
     * Gets the path.
     * @return the path.
     */
    public String getPath() {
        return path;
    }
}
