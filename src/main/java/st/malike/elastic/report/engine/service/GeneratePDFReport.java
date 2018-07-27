package st.malike.elastic.report.engine.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.apache.commons.io.IOUtils;
import st.malike.elastic.report.engine.exception.JasperGenerationException;
import st.malike.elastic.report.engine.exception.ReportFormatUnknownException;
import st.malike.elastic.report.engine.exception.TemplateNotFoundException;
import st.malike.elastic.report.engine.service.Generator.ReportFormat;
import st.malike.elastic.report.engine.util.ReportResponse;

/**
 * @author malike_st
 */
public class GeneratePDFReport implements GenerateReportService {

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
      ReportFormat reportFormat)
      throws TemplateNotFoundException, JasperGenerationException, ReportFormatUnknownException,
      PrivilegedActionException {
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
                byte[] dataInByte;
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                exportPdf(jp, byteArrayOutputStream, params);
                if (byteArrayOutputStream.size() == 0) {
                  return null;
                }
                byteArrayOutputStream.flush();
                dataInByte = byteArrayOutputStream.toByteArray();
                //write file to temp folder
                File reportFile = new File(reportFileLocation);
                FileOutputStream outputStream = new FileOutputStream(reportFile);
                IOUtils.write(dataInByte, outputStream);
                outputStream.flush();
                File htmlFile = new File(reportFileLocation);
                reportResponse.setReportFile(htmlFile);
                reportResponse.setSuccess(true);
                reportResponse.setResponse("Generated successfully");
                return reportResponse;
              } catch (RuntimeException e) {
                reportResponse.setReportFile(null);
                reportResponse.setSuccess(false);
                reportResponse.setResponse("Permission : " + e.getMessage());
              } catch (FileNotFoundException e) {
                reportResponse.setReportFile(null);
                reportResponse.setSuccess(false);
                reportResponse.setResponse(e.getMessage());
              } catch (JRException e) {
                reportResponse.setReportFile(null);
                reportResponse.setSuccess(false);
                reportResponse.setResponse(e.getMessage());
              } catch (IOException e) {
                reportResponse.setReportFile(null);
                reportResponse.setSuccess(false);
                reportResponse.setResponse(e.getMessage());
              }
              return reportResponse;
            }
          });
      if (reportFile.isSuccess()) {
        return reportFile.getReportFile();
      } else {
        throw new JasperGenerationException("Error generating PDF : "
            + reportFile.getResponse());
      }
    } catch (Exception e) {
      throw e;
    }
  }

  private JRDataSource getDatasource(List data) {
    if (data != null) {
      return new JRBeanCollectionDataSource(data);
    }
    return new JRBeanCollectionDataSource(new ArrayList());
  }

  private void exportPdf(JasperPrint jp, ByteArrayOutputStream baos, Map params) {
    // Create a JRPdfExporter instance
    JRPdfExporter exporter = new JRPdfExporter();
    exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
    exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
    try {
      exporter.exportReport();
    } catch (JRException e) {

    }
  }

}
