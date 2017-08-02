package st.malike.elastic.report.engine.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base64;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import st.malike.elastic.report.engine.exception.JasperGenerationException;
import st.malike.elastic.report.engine.exception.ReportFormatUnkownException;
import st.malike.elastic.report.engine.exception.TemplateNotFoundException;
import st.malike.elastic.report.engine.service.GenerateCSVReport;
import st.malike.elastic.report.engine.service.GenerateHTMLReport;
import st.malike.elastic.report.engine.service.GeneratePDFReport;

/**
 * @author malike_st
 */
public class Enums {

    public enum JSONResponseMessage {

        REPORT_FORMAT_UNKNOWN,
        ERROR_GENERATING_REPORT,
        SUCCESS,
        MISSING_PARAM,
        ERROR
    }

    public enum ReportFormat {

        /**
         *
         * PDF Report
         *
         */
        PDF {

                    @Override
                    public File generate(Map dataMap, List dataList, String templateFileLocation, String fileName) {
                        try {
                            return new GeneratePDFReport().generateReport(dataMap, dataList, templateFileLocation, fileName, PDF);
                        } catch (TemplateNotFoundException | JasperGenerationException | ReportFormatUnkownException ex) {
                            Logger.getLogger(Enums.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        return null;
                    }
                },
        /**
         *
         * CSV Report
         *
         */
        CSV {

                    @Override
                    public File generate(Map dataMap, List dataList, String templateFileLocation, String fileName) {
                        try {
                            return new GenerateCSVReport().generateReport(dataMap, dataList, templateFileLocation, fileName, CSV);
                        } catch (TemplateNotFoundException | JasperGenerationException | ReportFormatUnkownException ex) {
                            Logger.getLogger(Enums.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        return null;
                    }
                },
        /**
         *
         * HTML Report
         *
         */
        HTML {

                    @Override
                    public File generate(Map dataMap, List dataList, String templateFileLocation, String fileName) {
                        try {
                            return new GenerateHTMLReport().generateReport(dataMap, dataList, templateFileLocation, fileName, HTML);
                        } catch (TemplateNotFoundException | JasperGenerationException | ReportFormatUnkownException ex) {
                            Logger.getLogger(Enums.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        return null;
                    }
                };

        public abstract File generate(Map dataMap, List dataList, String templateFileLocation, String fileName);

        /**
         *
         * @param file
         * @return
         * @throws java.io.IOException
         */
        public String objectToBase64String(File file) throws IOException {
            if (file == null) {
                return null;
            }
            return Base64.encodeBase64URLSafeString(Files.readAllBytes(file.toPath()));
        }

        /**
         *
         * @param response
         * @return
         */
        public List<Map> extractData(SearchResponse response) {
            List<Map> data = new LinkedList<>();
            SearchHits hits = response.getHits();
            try {
                for (SearchHit hit : hits) {
                    Map<String, Object> sourceMap = hit.sourceAsMap();
                    data.add(sourceMap);
                }
            } catch (Exception e) {
            }
            return data;
        }
    }

}
