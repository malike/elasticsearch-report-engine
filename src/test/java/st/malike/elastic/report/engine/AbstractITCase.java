package st.malike.elastic.report.engine;

import org.apache.http.HttpHost;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.test.ESIntegTestCase;
import org.elasticsearch.test.ESTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.IOException;
import java.util.Map;

import static org.elasticsearch.test.rest.ESRestTestCase.entityAsMap;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.not;
import static org.junit.Assume.assumeThat;

/**
 * malike_st.
 */
public abstract class AbstractITCase extends ESIntegTestCase {

    protected static final Logger staticLogger = ESLoggerFactory.getLogger("it");
    protected final static int HTTP_TEST_PORT = 9503;
    protected static RestClient client;

    @BeforeClass
    public static void startRestClient() {
        client = RestClient.builder(new HttpHost("localhost", HTTP_TEST_PORT)).build();
        try {
            Response response = client.performRequest("GET", "/");
            Map<String, Object> responseMap = entityAsMap(response);
            assertThat(responseMap, hasEntry("tagline", "You Know, for Search"));
            staticLogger.info("Integration tests ready to start... Cluster is running.");
        } catch (IOException e) {
            // If we have an exception here, let's ignore the test
            staticLogger.warn("Integration tests are skipped: [{}]", e.getMessage());
            assumeThat("Integration tests are skipped", e.getMessage(), not(containsString("Connection refused")));
            staticLogger.error("Full error is", e);
            fail("Something wrong is happening. REST Client seemed to raise an exception.");
        }
    }

    @AfterClass
    public static void stopRestClient() throws IOException {
        if (client != null) {
            client.close();
            client = null;
        }
        staticLogger.info("Stopping integration tests against an external cluster");
    }
}