/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.elastic.report.engine.generate;

import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.List;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestStatus;
import st.malike.elastic.report.engine.exception.NoDataFoundException;
import st.malike.elastic.report.engine.service.Generator;
import st.malike.elastic.report.engine.service.Generator.JSONResponseMessage;
import st.malike.elastic.report.engine.util.JSONResponse;
import st.malike.elastic.report.engine.util.ReportResponse;

/**
 * @author malike_st
 */
public class GenerateResponseListener implements ActionListener<SearchResponse> {

  private final GenerateData generateData;
  private final RestChannel restChannel;
  private final RestRequest restRequest;

  public GenerateResponseListener(GenerateData generateData, RestChannel restChannel,
      RestRequest restRequest) {
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

      ReportResponse reportFileResponse = AccessController.doPrivileged(
          new PrivilegedExceptionAction<ReportResponse>() {
            ReportResponse reportResponse = new ReportResponse();

            @Override
            public ReportResponse run() {
              try {

                File reportFile = generateData.getFormat()
                    .generate(generateData.getMapData(), dataList,
                        generateData.getTemplateLocation(),
                        generateData.getFileName());
                reportResponse.setReportFile(reportFile);
                reportResponse.setResponse("Generated successfully");
                reportResponse.setSuccess(true);
              } catch (Exception e) {
                reportResponse.setReportFile(null);
                reportResponse.setResponse(e.getLocalizedMessage());
                reportResponse.setSuccess(false);
              }
              return reportResponse;
            }
          });

      if (!reportFileResponse.isSuccess()) {
        message.setData("FAILED TO GENERATE : " + reportFileResponse.getResponse());
        message.setMessage(JSONResponseMessage.ERROR_GENERATING_REPORT.toString());
        message.setStatus(false);
        message.setCount(0L);
        restChannel.sendResponse(new BytesRestResponse(RestStatus.OK,
            "application/json", message.toString()));
        return;
      }
      message.setMessage(JSONResponseMessage.SUCCESS.toString());
      message.setStatus(true);
      message.setCount(Long.valueOf(dataList.size()));
      if (generateData.getReturnAs().equals(Generator.ReturnAs.BASE64)) {
        message.setData(
            generateData.getFormat()
                .objectToBase64String(reportFileResponse.getReportFile(), generateData
                    .getFormat()));
      } else {
        message.setData(generateData.getFormat()
            .getContents(reportFileResponse.getReportFile(), generateData.getFormat()));
      }
      message.setMessage(Generator.JSONResponseMessage.SUCCESS.toString());
      XContentBuilder builder = restChannel.newBuilder();
      builder.startObject();
      message.toXContent(builder, restRequest);
      builder.endObject();
      restChannel.sendResponse(new BytesRestResponse(RestStatus.OK, builder));
      return;
    } catch (NoDataFoundException nde) {
      message.setData(nde.getLocalizedMessage());
      message.setMessage(JSONResponseMessage.NO_DATA_EXCEPTION.toString());
      message.setStatus(false);
      message.setCount(0L);
      restChannel.sendResponse(new BytesRestResponse(RestStatus.OK,
          "application/json", message.toString()));
      return;
    } catch (Exception e) {
      message.setData(e.getMessage());
      message.setMessage(JSONResponseMessage.ERROR_GENERATING_REPORT.toString());
      message.setStatus(false);
      message.setCount(0L);
      restChannel.sendResponse(new BytesRestResponse(RestStatus.OK,
          "application/json", message.toString()));
      return;
    }
  }

  @Override
  public void onFailure(Exception exception) {
    JSONResponse message = new JSONResponse();
    message.setData(exception.getMessage());
    message.setMessage(JSONResponseMessage.ERROR_GENERATING_REPORT.toString());
    message.setStatus(false);
    message.setCount(0L);
    restChannel.sendResponse(new BytesRestResponse(RestStatus.OK,
        "application/json", message.toString()));
    throw new ElasticsearchException("FAILED : ",
        exception.getMessage());
  }

}
