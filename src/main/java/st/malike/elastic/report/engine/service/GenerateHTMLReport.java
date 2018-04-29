/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.elastic.report.engine.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import st.malike.elastic.report.engine.exception.JasperGenerationException;
import st.malike.elastic.report.engine.exception.ReportFormatUnknownException;
import st.malike.elastic.report.engine.exception.TemplateNotFoundException;
import st.malike.elastic.report.engine.util.ReportResponse;

/**
 * @author malike_st
 */
public class GenerateHTMLReport implements GenerateReportService {

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
  public File generateReport(Map params, List data, String templateFile, String fileName,
      Generator.ReportFormat reportFormat)
      throws TemplateNotFoundException, JasperGenerationException,
      ReportFormatUnknownException, PrivilegedActionException {
    try {
      if (templateFile == null || templateFile.trim().isEmpty()) {
        throw new TemplateNotFoundException("Template file not found");
      }
      if (reportFormat == null) {
        throw new ReportFormatUnknownException("Report format unknown");
      }
      ReportResponse reportFile = null;
      reportFile = AccessController.doPrivileged(
          new PrivilegedExceptionAction<ReportResponse>() {

            @Override
            public ReportResponse run() {
              ReportResponse reportResponse = new ReportResponse();
              String reportFileLocation =
                  System.getProperty("user.dir") +
                      File.separator + "reports" +
                      File.separator + fileName + "." + reportFormat.toString().toLowerCase();

              String templateFileLocation = System.getProperty("user.dir") +
                  File.separator + "templates" +
                  File.separator + templateFile;
              try {

                InputStream reportStream = new FileInputStream(new File(templateFileLocation));
                JasperReport jr;
                if (templateFile.endsWith("jasper")) {
                  jr = (JasperReport) JRLoader.loadObject(
                      new File(templateFileLocation));
                } else if (templateFile.endsWith("jrxml")) {
                  JasperDesign jd = JRXmlLoader.load(reportStream);
                  jr = JasperCompileManager.compileReport(jd);
                } else {
                  reportResponse.setReportFile(null);
                  reportResponse.setSuccess(false);
                  reportResponse.setResponse("Jasper Report Template file not supported");
                  return reportResponse;
                }
                JasperPrint jp = JasperFillManager.fillReport(jr, params, getDatasource(data));
                JasperExportManager.exportReportToHtmlFile(jp, reportFileLocation);
                File htmlFile = new File(reportFileLocation);
                reportResponse.setReportFile(htmlFile);
                reportResponse.setSuccess(true);
                reportResponse.setResponse("Generated successfully");
                return reportResponse;
              } catch (RuntimeException e) {
                reportResponse.setReportFile(null);
                reportResponse.setSuccess(false);
                reportResponse.setResponse("Permission : "+e.getMessage());
              } catch (FileNotFoundException e) {
                reportResponse.setReportFile(null);
                reportResponse.setSuccess(false);
                reportResponse.setResponse("File Not Found : "+e.getMessage()
                +" [\""+reportFileLocation+"\"]");
              } catch (JRException e) {
                reportResponse.setReportFile(null);
                reportResponse.setSuccess(false);
                reportResponse.setResponse("Jasper Report Exception : "+e.getMessage());
              }
              return reportResponse;
            }
          });
      if (reportFile.isSuccess()) {
        return reportFile.getReportFile();
      } else {
        throw new JasperGenerationException("Error generating HTML : "
            + reportFile.getResponse());
      }
    } catch (Exception e) {
      throw e;
    }
  }

  private JRDataSource getDatasource(List data) {
    if (null != data) {
      return new JRBeanCollectionDataSource(data);
    }
    return new JRBeanCollectionDataSource(new ArrayList());
  }

}
