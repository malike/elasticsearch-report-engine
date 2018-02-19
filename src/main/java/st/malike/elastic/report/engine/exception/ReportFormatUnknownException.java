/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.elastic.report.engine.exception;

/**
 * @author malike_st
 */
public class ReportFormatUnknownException extends Exception {

    public ReportFormatUnknownException() {
    }

    public ReportFormatUnknownException(String message) {
        super(message);
    }

    public ReportFormatUnknownException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReportFormatUnknownException(Throwable cause) {
        super(cause);
    }

    public ReportFormatUnknownException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
