package st.malike.elastic.report.engine.util;

import java.io.File;

/**
 * @autor malike_st
 */
public class ReportResponse {

  private File reportFile;
  private String response;
  private boolean success;

  public File getReportFile() {
    return reportFile;
  }

  public void setReportFile(File reportFile) {
    this.reportFile = reportFile;
  }

  public String getResponse() {
    return response;
  }

  public void setResponse(String response) {
    this.response = response;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }
}
