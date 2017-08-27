/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.elastic.report.engine.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import st.malike.elastic.report.engine.exception.JasperGenerationException;
import st.malike.elastic.report.engine.exception.ReportFormatUnkownException;
import st.malike.elastic.report.engine.exception.TemplateNotFoundException;

/**
 *
 * @author malike_st
 */
public class GenerateHTMLReport implements GenerateReportService {

    /**
     *
     * @param params
     * @param data
     * @param templateFileLocation
     * @param fileName
     * @param reportFormat
     * @return
     * @throws TemplateNotFoundException
     * @throws JasperGenerationException
     * @throws ReportFormatUnkownException
     */
    @Override
    public File generateReport(Map params, List data, String templateFileLocation, String fileName,
            Generator.ReportFormat reportFormat) throws TemplateNotFoundException, JasperGenerationException, ReportFormatUnkownException {
        try {
            if (templateFileLocation == null || templateFileLocation.trim().isEmpty()) {
                throw new TemplateNotFoundException("Template file not found");
            }
            if (reportFormat == null) {
                throw new ReportFormatUnkownException("Report format unknown");
            }
            String htmlFilePath = System.getProperty("java.io.tmpdir") + File.separator + fileName + "." + reportFormat.toString().toLowerCase();
            InputStream reportStream = new FileInputStream(new File(templateFileLocation));
            JasperDesign jd = JRXmlLoader.load(reportStream);
            JasperReport jr = JasperCompileManager.compileReport(jd);
            JasperPrint jp = JasperFillManager.fillReport(jr, params, getDatasource(data));
            JasperExportManager.exportReportToHtmlFile(jp, htmlFilePath);
            File htmlFile = new File(htmlFilePath);
            return htmlFile;
        } catch (JRException e) {
            throw new JasperGenerationException("Error merging template and data :" + e.getMessage());
        } catch (IOException e) {
            throw new TemplateNotFoundException(templateFileLocation + " not found.");
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
