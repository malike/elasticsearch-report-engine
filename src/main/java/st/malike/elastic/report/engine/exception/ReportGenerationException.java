package st.malike.elastic.report.engine.exception;

/**
 * @autor malike_st
 */
public class ReportGenerationException extends Exception {

  public ReportGenerationException() {
  }

  public ReportGenerationException(String message) {
    super(message);
  }

  public ReportGenerationException(String message, Throwable cause) {
    super(message, cause);
  }

  public ReportGenerationException(Throwable cause) {
    super(cause);
  }

  public ReportGenerationException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
