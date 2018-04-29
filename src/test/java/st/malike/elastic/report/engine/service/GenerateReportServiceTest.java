/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.elastic.report.engine.service;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import st.malike.elastic.report.engine.exception.ReportFormatUnknownException;
import st.malike.elastic.report.engine.exception.TemplateNotFoundException;

/**
 * @author malike_st
 */
@RunWith(MockitoJUnitRunner.class)
@Ignore
public class GenerateReportServiceTest {

  @InjectMocks
  GenerateHTMLReport generateHTMLReport;
  @InjectMocks
  GeneratePDFReport generatePDFReport;
  @InjectMocks
  GenerateCSVReport generateCSVReport;
  Map map;
  List list;
  String fileName;
  String templateFileName;
  Generator.ReportFormat reportFormat;

  @Before
  public void setUp() throws Exception {
    map = new HashMap<>();
    map.put("fullName", "Malike St");
    map.put("email", "st.malike@gmail.com");

    list = new LinkedList<>();
    // create documents
    for (int i = 1; i <= 10; i++) {
      Map dataMap = new HashMap();
      dataMap.put("id", i);
      dataMap.put("description", "Item Number " + i);
      dataMap.put("type", "CREDIT");
      list.add(dataMap);
    }

    fileName = "RANDOM_REPORT";
    templateFileName = "SampleTemplate.jrxml";
    reportFormat = Generator.ReportFormat.PDF;


  }

  @Test
  public void testGeneratePDFReport() throws Exception {
    generatePDFReport
        .generateReport(map, list, templateFileName, fileName, Generator.ReportFormat.PDF);
    File f = new File(System.getProperty("user.dir") +
        File.separator + "reports" +
        File.separator + fileName + "."
        + Generator.ReportFormat.PDF.toString().toLowerCase());
    Assert.assertTrue(f.exists() && !f.isDirectory());
  }

  @Test
  public void testGenerateHTMLReport() throws Exception {
    generateHTMLReport
        .generateReport(map, list, templateFileName, fileName, Generator.ReportFormat.HTML);
    File f = new File(System.getProperty("user.dir") +
        File.separator + "reports" +
        File.separator + fileName + "."
        + Generator.ReportFormat.HTML.toString().toLowerCase());
    Assert.assertTrue(f.exists() && !f.isDirectory());
  }

  @Test(expected = TemplateNotFoundException.class)
  public void testGeneratePDFReportThrowsTemplateNotFoundException() throws Exception {
    generatePDFReport.generateReport(map, list, null, fileName, reportFormat);
  }

  @Test(expected = ReportFormatUnknownException.class)
  public void testGenerateReportThrowsReportFormatUnknowException() throws Exception {
    generatePDFReport.generateReport(map, list, templateFileName, fileName, null);
  }

  @Test
  public void testGenerateCSVReport() throws Exception {
    generateCSVReport
        .generateReport(map, list, templateFileName, fileName, Generator.ReportFormat.CSV);
    File f = new File(System.getProperty("user.dir") +
        File.separator + "reports" +
        File.separator + fileName + "."
        + Generator.ReportFormat.CSV.toString().toLowerCase());
    Assert.assertTrue(f.exists() && !f.isDirectory());
  }

}
