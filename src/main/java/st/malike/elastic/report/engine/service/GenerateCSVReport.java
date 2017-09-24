/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.elastic.report.engine.service;

import com.github.wnameless.json.flattener.JsonFlattener;
import com.google.gson.Gson;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.lang.StringUtils;
import st.malike.elastic.report.engine.exception.JasperGenerationException;
import st.malike.elastic.report.engine.exception.ReportFormatUnkownException;
import st.malike.elastic.report.engine.exception.TemplateNotFoundException;

/**
 *
 * @author malike_st
 */
public class GenerateCSVReport implements GenerateReportService {

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
    @SuppressWarnings("unchecked")
    public File generateReport(Map params, List data, String templateFileLocation,
            String fileName, Generator.ReportFormat reportFormat) throws TemplateNotFoundException, JasperGenerationException, ReportFormatUnkownException {
        try {
            if (reportFormat == null) {
                throw new ReportFormatUnkownException("Report format unknown");
            }
            String filePath = System.getProperty("java.io.tmpdir") + File.separator + fileName + "." + reportFormat.toString().toLowerCase();
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
                Map<String, Object> flattenJson = JsonFlattener.flattenAsMap(new Gson().toJson(o));
                output = output + getData(headers, flattenJson) + "\n";
            }

            writeToFile(output, filePath);
            File csvFile = new File(filePath);
            return csvFile;
        } catch (ReportFormatUnkownException | FileNotFoundException ex) {
            throw new JasperGenerationException("Error generationg CSV");
        } catch (IOException ex) {
            throw new TemplateNotFoundException("Template not found");
        }
    }

    private void writeToFile(String output, String fileName) throws FileNotFoundException, IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(output);
        } catch (IOException e) {
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
            }
            os.add(val);
        }
        return StringUtils.join(os.toArray(), ",");
    }

}
