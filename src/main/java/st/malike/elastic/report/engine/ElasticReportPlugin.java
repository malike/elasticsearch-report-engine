package st.malike.elastic.report.engine;

import org.elasticsearch.plugins.ActionPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.RestHandler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * malike_st.
 */
public class ElasticReportPlugin extends Plugin implements ActionPlugin {


    @Override
    public List<Class<? extends RestHandler>> getRestHandlers() {
        return Arrays.asList(ReportGenerateRestAction.class, ReportHistoryRestAction.class);
    }


}
