/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.elastic.report.engine.service;

import com.google.gson.Gson;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.lang.StringUtils;
import st.malike.elastic.report.engine.exception.JasperGenerationException;
import st.malike.elastic.report.engine.exception.ReportFormatUnknownException;
import st.malike.elastic.report.engine.exception.TemplateNotFoundException;
import st.malike.elastic.report.engine.util.ReportResponse;

/**
 * @author malike_st
 */
public class GenerateCSVReport implements GenerateReportService {

  /**
   * @param params
   * @param data
   * @param templateFile
   * @param fileName
   * @param reportFormat
   * @return
   * @throws TemplateNotFoundException
   * @throws JasperGenerationException
   * @throws ReportFormatUnknownException
   */
  @Override
  @SuppressWarnings("unchecked")
  public File generateReport(Map params, List data, String templateFile,
      String fileName, Generator.ReportFormat reportFormat)
      throws JasperGenerationException {
    ReportResponse reportFile = null;
    try {
      if (reportFormat == null) {
        throw new ReportFormatUnknownException("Report format unknown");
      }
      String filePath =
          System.getProperty("user.dir") +
              File.separator + "reports" +
              File.separator + fileName + "." + reportFormat
              .toString().toLowerCase();
      if (data.isEmpty()) {
        //throw exception of it not processed
      }

      Set<String> headers = new TreeSet<>();
      Map<String, Object> map = (Map<String, Object>) data.get(0);
      headers.addAll(map.keySet());

      if (!headers.isEmpty()) {
        //throw exception of it not processed
      }
      String output = StringUtils.join(headers.toArray(), ",") + "\n";
      for (Object o : data) {
        try {
          Map<String, Object> dt = (Map) o;
          output = output + getData(headers, dt) + "\n";
        } catch (Exception e) {
        }
      }

      final String csvOutput;
      if (output == null) {
        csvOutput = "NO Data";
      } else {
        csvOutput = output;
      }

      reportFile = AccessController.doPrivileged(
          new PrivilegedExceptionAction<ReportResponse>() {

            @Override
            public ReportResponse run() {
              ReportResponse reportResponse = new ReportResponse();
              try {

                writeToFile(csvOutput, filePath);
                File csvFile = new File(filePath);
                reportResponse.setReportFile(csvFile);
                reportResponse.setSuccess(true);
                reportResponse.setResponse("Generated successfully");

              } catch (Exception e) {
                reportResponse.setReportFile(null);
                reportResponse.setSuccess(false);
                reportResponse.setResponse("Error generating : " + e.getMessage());
              }
              return reportResponse;
            }
          });
      if (reportFile.isSuccess()) {
        return reportFile.getReportFile();
      } else {
        throw new JasperGenerationException("Error generating CSV : "
            + reportFile.getResponse());
      }
    } catch (Exception ex) {
      throw new JasperGenerationException("Error generating CSV : "
          + ((reportFile != null) ? reportFile.getResponse() : ex.getMessage()));
    }
  }

  private void writeToFile(String output, String fileName)
      throws Exception {
    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(new FileWriter(fileName));
      writer.write(output);
    } catch (Exception e) {
      throw e;
    } finally {
      close(writer);
    }
  }

  private void close(BufferedWriter writer) throws IOException {
    try {
      if (writer != null) {
        writer.close();
      }
    } catch (IOException e) {
      throw e;
    }
  }

  private Object getData(Set<String> headers, Map<String, Object> map) {
    List<Object> os = new ArrayList<>();
    for (String header : headers) {
      Object val = map.get(header) == null ? "" : map.get(header);
      if (val instanceof String) {
        val = ((String) val).replace(",", "");
      } else if (val instanceof List) {
        String listVal = new Gson().toJson(val).replace(",", ";");
        val = "\"" + listVal.replace("\"", "\'") + "\"";
      }
      os.add(val);
    }
    return StringUtils.join(os.toArray(), ",");
  }

}
