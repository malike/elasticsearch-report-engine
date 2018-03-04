package st.malike.elastic.report.engine.service;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

/**
 * @autor malike_st
 */
public class FileServer implements HttpHandler {

    private final String TEMPLATE_PATH="template/";

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

    }
}
