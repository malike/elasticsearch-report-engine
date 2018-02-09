package st.malike.elastic.report.engine.exception;

/**
 * @autor malike_st
 */
public class IndexNotFoundException extends Exception {
    public IndexNotFoundException() {
    }

    public IndexNotFoundException(String message) {
        super(message);
    }

    public IndexNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public IndexNotFoundException(Throwable cause) {
        super(cause);
    }

    public IndexNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
