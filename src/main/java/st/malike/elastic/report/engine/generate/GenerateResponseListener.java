/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.elastic.report.engine.generate;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestStatus;
import st.malike.elastic.report.engine.service.Generator;
import st.malike.elastic.report.engine.util.JSONResponse;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author malike_st
 */
public class GenerateResponseListener implements ActionListener<SearchResponse> {

    private final GenerateData generateData;
    private final RestChannel restChannel;
    private final RestRequest restRequest;

    public GenerateResponseListener(GenerateData generateData, RestChannel restChannel, RestRequest restRequest) {
        this.generateData = generateData;
        this.restChannel = restChannel;
        this.restRequest = restRequest;
    }

    @Override
    public void onResponse(SearchResponse searchResponse) {
        //get data
        JSONResponse message = new JSONResponse();
        message.setStatus(false);
        message.setCount(0L);
        message.setMessage(Generator.JSONResponseMessage.ERROR_GENERATING_REPORT.toString());
        try {

            List dataList = generateData.getFormat().extractData(searchResponse);
            final File reportFile = generateData.getFormat().generate(generateData.getMapData(), dataList, generateData.getTemplateLocation(), generateData.getFileName());
            if (reportFile == null) {
                XContentBuilder builder = restChannel.newBuilder();
                builder.startObject();
                message.toXContent(builder, restRequest);
                builder.endObject();
                restChannel.sendResponse(new BytesRestResponse(RestStatus.OK, builder));
            }
            message.setStatus(true);
            message.setCount(1L);
            if (generateData.getReturnAs().equals(Generator.ReturnAs.BASE64)) {
                message.setData(generateData.getFormat().objectToBase64String(reportFile, generateData.getFormat()));
            } else {
                message.setData(generateData.getFormat().getContents(reportFile, generateData.getFormat()));
            }
            message.setMessage(Generator.JSONResponseMessage.SUCCESS.toString());
            XContentBuilder builder = restChannel.newBuilder();
            builder.startObject();
            message.toXContent(builder, restRequest);
            builder.endObject();
            restChannel.sendResponse(new BytesRestResponse(RestStatus.OK, builder));
        } catch (Exception e) {
            try {
                onFailure(e);
                XContentBuilder builder = restChannel.newBuilder();
                builder.startObject();
                message.setData(e.getLocalizedMessage());
                message.toXContent(builder, restRequest);
                builder.endObject();
                restChannel.sendResponse(new BytesRestResponse(RestStatus.OK, builder));
            } catch (IOException ex) {
                onFailure(e);
            }
        }
    }

    @Override
    public void onFailure(Exception exception) {
        throw new ElasticsearchException("Failed to create a response.", exception.getLocalizedMessage());

    }

}
