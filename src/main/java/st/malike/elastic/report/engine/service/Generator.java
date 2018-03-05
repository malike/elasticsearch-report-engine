package st.malike.elastic.report.engine.service;

import org.apache.commons.codec.binary.Base64;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import st.malike.elastic.report.engine.exception.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author malike_st
 */
public class Generator {

    public enum JSONResponseMessage {

        REPORT_FORMAT_UNKNOWN,
        ERROR_GENERATING_REPORT,
        SUCCESS,
        NO_DATA_EXCEPTION,
        MISSING_PARAM
    }

    public enum ReturnAs {

        FILE,
        BASE64,
        PLAIN
    }

    public enum ReportFormat {

        /**
         * PDF Report
         */
        PDF {
            @Override
            @SuppressWarnings("unchecked")
            public File generate(Map dataMap, List dataList, String templateFileLocation, String fileName) {
                try {
                    return new GeneratePDFReport().generateReport(dataMap, dataList, templateFileLocation, fileName, PDF);
                } catch (TemplateNotFoundException | JasperGenerationException | ReportFormatUnknownException ex) {
                    Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        },
        /**
         * CSV Report
         */
        CSV {
            @Override
            @SuppressWarnings("unchecked")
            public File generate(Map dataMap, List dataList, String templateFileLocation, String fileName) {
                if (dataList.isEmpty()) {
                    return null;
                }
                try {
                    return new GenerateCSVReport().generateReport(dataMap, dataList, templateFileLocation, fileName, CSV);
                } catch (TemplateNotFoundException | JasperGenerationException | ReportFormatUnknownException ex) {
                    Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        },
        /**
         * HTML Report
         */
        HTML {
            @Override
            @SuppressWarnings("unchecked")
            public File generate(Map dataMap, List dataList, String templateFileLocation, String fileName) {
                try {
                    return new GenerateHTMLReport().generateReport(dataMap, dataList, templateFileLocation, fileName, HTML);
                } catch (TemplateNotFoundException | JasperGenerationException | ReportFormatUnknownException ex) {
                    Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };

        @SuppressWarnings("unchecked")
        public abstract File generate(Map dataMap, List dataList, String templateFileLocation, String fileName);

        /**
         * @param file
         * @param reportFormat
         * @return
         * @throws java.io.IOException
         */
        @SuppressWarnings("unchecked")
        public Map objectToBase64String(File file, ReportFormat reportFormat) throws IOException {
            if (file == null) {
                return null;
            }
            Map data = new HashMap();
            data.put("reportFormat", reportFormat.toString());
            data.put("data", Base64.encodeBase64URLSafeString(Files.readAllBytes(file.toPath())));
            return data;
        }

        /**
         * @param response
         * @return
         */
        @SuppressWarnings("unchecked")
        public List<Map> extractData(SearchResponse response) throws NoDataFoundException, ReportGenerationException {
            List<Map> data = new LinkedList<>();
            SearchHits hits = response.getHits();
            if(response.isTimedOut() || response.isTerminatedEarly()){
                throw new ReportGenerationException("Report failed to get data. Kindly try again.");
            }
            if(hits.getTotalHits()==0) {
                throw new NoDataFoundException("No data found");
            }
                try {
                    for (SearchHit hit : hits) {
                        Map<String, Object> sourceMap = hit.getSourceAsMap();
                        data.add(sourceMap);
                    }
                } catch (Exception e) {
                }
            return data;
        }

        @SuppressWarnings("unchecked")
        public Map getContents(File reportFile, ReportFormat reportFormat) throws IOException {
            if (reportFile == null) {
                return null;
            }
            if (reportFormat.equals(ReportFormat.PDF)) {
                //default to base64 if report type is PDF
                return objectToBase64String(reportFile, reportFormat);
            }
            byte[] reportFileEncoded = Files.readAllBytes(Paths.get(reportFile.getAbsolutePath()));
            String report = new String(reportFileEncoded, StandardCharsets.UTF_8);
            Map data = new HashMap();
            data.put("reportFormat", reportFormat.toString());
            data.put("data", report);
            return data;
        }
    }

}
