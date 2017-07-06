/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.elastic.report.engine.generate;

import java.util.Map;
import st.malike.elastic.report.engine.util.Enums;

/**
 *
 * @author malike_st
 */
public class GenerateData {

    private String query;
    private Enums.ReportFormat format;
    private String templateLocation;
    private String fileName;
    private String index;
    private Map mapData;

    public GenerateData() {
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Enums.ReportFormat getFormat() {
        return format;
    }

    public void setFormat(Enums.ReportFormat format) {
        this.format = format;
    }

    public String getTemplateLocation() {
        return templateLocation;
    }

    public void setTemplateLocation(String templateLocation) {
        this.templateLocation = templateLocation;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public Map getMapData() {
        return mapData;
    }

    public void setMapData(Map mapData) {
        this.mapData = mapData;
    }

}
