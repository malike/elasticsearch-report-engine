/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.elastic.report.engine;

import com.google.gson.Gson;
import static com.jayway.restassured.RestAssured.given;
import java.io.File;
import org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import static org.junit.Assert.*;
import st.malike.elastic.report.engine.util.Enums;

@RunWith(BlockJUnit4ClassRunner.class)
public class ElasticReportPluginTest {

    private static Node node;
    private static ElasticsearchClusterRunner runner;
    private static final String CLUSTER_NAME = "REPORTDATA_CLUSTER";
    private static final String CLUSTER_HOST_ADDRESS = "localhost:9201-9210";
    private static final String INDEX = "reportindex";
    private static final int DOC_SIZE = 100;
    private static String TEMPLATE_NAME = "SampleTemplate.jrxml";
    private static String JASPER_TEMPLATE_FILE_LOCATION = "/";
    private Map param;

    @BeforeClass
    public static void setUp() throws IOException {

        runner = new ElasticsearchClusterRunner();

        runner.onBuild(new ElasticsearchClusterRunner.Builder() {
            @Override
            public void build(final int number, final Settings.Builder settingsBuilder) {
                settingsBuilder.put("http.cors.allow-origin", "*");
                settingsBuilder.put("http.cors.enabled", true);
                settingsBuilder.putArray("discovery.zen.ping.unicast.hosts", CLUSTER_HOST_ADDRESS);
            }
        }).build(ElasticsearchClusterRunner.newConfigs().clusterName(CLUSTER_NAME).numOfNode(1)
                .pluginTypes("st.malike.elastic.report.engine.ElasticReportPlugin"));

        runner.ensureYellow();

        //setupup dummy data        
        final String type = "reporttype";

        // create an index
        runner.createIndex(INDEX, (Settings) null);

        // create documents
        for (int i = 1; i <= DOC_SIZE; i++) {
            runner.insert(INDEX, type, String.valueOf(i),
                    "{"
                    + "\"description\":\"Transaction " + i + "\","
                    + "\"id\":" + i
                    + "}");
        }
        runner.refresh();

        SearchResponse searchResponse = runner.search(INDEX, type, null, null, 0, 10);
        assertEquals(DOC_SIZE, searchResponse.getHits().getTotalHits());

        node = runner.node();
    }

    @AfterClass
    public static void tearDown() throws IOException {
        runner.close();
        runner.clean();
    }

    @Before
    public void setUpTest() {

        Map dummy = new HashMap();
        dummy.put("fullName", "Malike St");
        dummy.put("email", "st.malike@gmail.com");
        dummy.put("age", "15");

        Map sort = new HashMap();
        sort.put("id", "DESC");

        String queryString= "{\"term\":{\"id\":20}}";

        
        ClassLoader classLoader = getClass().getClassLoader();
        File tempFile = new File(classLoader.getResource(TEMPLATE_NAME).getFile());
        JASPER_TEMPLATE_FILE_LOCATION = tempFile.getAbsolutePath();
         
        param = new HashMap();
        param.put("format", "PDF");
        param.put("fileName", "TEST_REPORT");
        param.put("index", INDEX);
        param.put("template", JASPER_TEMPLATE_FILE_LOCATION);
        param.put("mapData", dummy);
        param.put("from", 0);
        param.put("size", DOC_SIZE + DOC_SIZE);
        param.put("query", queryString);
        param.put("sort", sort);
    }

    @Test
    public void generateReport() {
        given()
                .log().all().contentType("application/json")
                .body(new Gson().toJson(param))
                .when()
                .post("http://localhost:9201/_generate")
                .then()
                .statusCode(200)
                .body("status", Matchers.is(true))
                .body("message", Matchers.is(Enums.JSONResponseMessage.SUCCESS.toString()));
    }

    @Test
    public void generateReportMissingParam() {
        param.remove("format");
        given()
                .log().all().contentType("application/json")
                .body(new Gson().toJson(param))
                .when()
                .post("http://localhost:9201/_generate")
                .then()
                .statusCode(500)
                .body("status", Matchers.is(false))
                .body("message", Matchers.is(Enums.JSONResponseMessage.MISSING_PARAM.toString()));
    }

    @Test
    public void generateReportTypeUnknown() {
        param.put("format", "UNKNOWN");
        given()
                .log().all().contentType("application/json")
                .body(new Gson().toJson(param))
                .when()
                .post("http://localhost:9201/_generate")
                .then()
                .statusCode(500)
                .body("status", Matchers.is(false))
                .body("message", Matchers.is(Enums.JSONResponseMessage.REPORT_FORMAT_UNKNOWN.toString()));
    }

}
