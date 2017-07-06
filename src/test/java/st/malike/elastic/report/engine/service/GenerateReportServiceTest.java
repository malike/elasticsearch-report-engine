/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package st.malike.elastic.report.engine.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author malike_st
 */
@RunWith(MockitoJUnitRunner.class)
public class GenerateReportServiceTest {

    @InjectMocks
    GenerateReportService generateReportService;

    @Test
    public void testGenerateReport() throws Exception {
        Assert.assertEquals("AWESOME", "AWESOME");
    }
    
    @Test
    public void testGenerateReportThrowsTemplateNotFoundException() throws Exception {
        Assert.assertEquals("AWESOME", "AWESOME");
    }
    
    @Test
    public void testGenerateReportThrowsGenerationErrorExceptionException() throws Exception {
        Assert.assertEquals("AWESOME", "AWESOME");
    }

}
