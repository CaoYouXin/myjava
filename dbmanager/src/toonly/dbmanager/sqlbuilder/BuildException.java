package toonly.dbmanager.sqlbuilder;

/**
 * Created by cls on 15-3-23.
 */
public class BuildException extends RuntimeException {
    public BuildException() {
    }

    public BuildException(String message) {
        super(message);
    }

    public BuildException(String message, Throwable cause) {
        super(message, cause);
    }

    public BuildException(Throwable cause) {
        super(cause);
    }

    public BuildException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
