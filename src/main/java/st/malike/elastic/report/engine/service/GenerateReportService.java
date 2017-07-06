package st.malike.elastic.report.engine.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
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
import net.sf.jasperreports.engine.export.JRXlsAbstractExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.apache.commons.io.IOUtils;
import st.malike.elastic.report.engine.util.Enums.ReportFormat;
import st.malike.elastic.report.engine.exception.JasperGenerationException;
import st.malike.elastic.report.engine.exception.TemplateNotFoundException;

/**
 * @author malike_st
 */
public class GenerateReportService {

    public File generateReport(Map params, List data, String templateFileLocation, String fileName,
            ReportFormat reportFormat) throws TemplateNotFoundException, JasperGenerationException {
        try {
            String generatedFileName = fileName + new Date().getTime() + "." + reportFormat.toString().toLowerCase();
            InputStream reportStream = new FileInputStream(new File(templateFileLocation));
            JasperDesign jd = JRXmlLoader.load(reportStream);
            JasperReport jr = JasperCompileManager.compileReport(jd);
            JasperPrint jp = JasperFillManager.fillReport(jr, params, getDatasource(data));
            byte[] dataInByte;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            switch (reportFormat) {
                case PDF:
                    exportPdf(jp, baos, params);
                    break;
                case XLS:
                    exportXls(jp, baos, params);
                    break;
                default:
            }
            if (baos.size() == 0) {
                return null;
            }
            baos.flush();
            dataInByte = baos.toByteArray();
            File reportFile = new File(System.getProperty("java.io.tmpdir") + File.separator + generatedFileName);
            FileOutputStream outputStream = new FileOutputStream(reportFile);
            IOUtils.write(dataInByte, outputStream);
            outputStream.flush();
            return reportFile;
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

    private void exportXls(JasperPrint jp, ByteArrayOutputStream baos, Map params) {
        // Create a JRXlsExporter instance
        JRXlsExporter exporter = new JRXlsExporter();
        // Here we assign the parameters jp and baos to the exporter
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);

        // Excel specific parameters
        exporter.setParameter(JRXlsAbstractExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
        exporter.setParameter(JRXlsAbstractExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
        exporter.setParameter(JRXlsAbstractExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
        try {
            exporter.exportReport();

        } catch (JRException e) {

        }
    }
}
