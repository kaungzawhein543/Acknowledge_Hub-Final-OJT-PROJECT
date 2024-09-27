package com.ace.test;

import com.ace.entity.Announcement;
import com.ace.service.AnnouncementService;
import com.ace.service.AsyncCallback;
import com.ace.service.ReportService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

public class ReportServiceTest {
    @InjectMocks
    private ReportService reportService;

    @Mock
    private AnnouncementService announcementService;

    @Mock
    private AsyncCallback<byte[]> callback;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGenerateAnnouncementFile_Success() throws Exception {
        // Prepare mock announcement
        Integer announcementId = 1;
        String title = "Test Report Title";
        Announcement announcement = new Announcement();
        announcement.setId(announcementId);

        when(announcementService.getAnnouncementById(announcementId)).thenReturn(Optional.of(announcement));

        InputStream mockReportStream = getClass().getClassLoader().getResourceAsStream("reports/announcement.jrxml");
        assertNotNull("Report template not found: reports/announcement.jrxml", mockReportStream);

        JasperReport mockJasperReport = JasperCompileManager.compileReport(mockReportStream);
        JasperPrint mockJasperPrint = mock(JasperPrint.class);

        when(JasperCompileManager.compileReport(any(InputStream.class))).thenReturn(mockJasperReport);
        when(JasperFillManager.fillReport(any(JasperReport.class), any(Map.class), any(JRBeanCollectionDataSource.class)))
                .thenReturn(mockJasperPrint);
        when(JasperExportManager.exportReportToPdf(mockJasperPrint)).thenReturn(new byte[]{1, 2, 3});

        reportService.generateAnnouncementFile(announcementId, title, callback);

        verify(announcementService).getAnnouncementById(announcementId);
        verify(callback).onSuccess(any(byte[].class));
    }

    @Test
    public void testGenerateAnnouncementFile_AnnouncementNotFound() {
        Integer announcementId = 1;
        String title = "Test Report Title";

        // Mock the announcement service to return empty Optional
        when(announcementService.getAnnouncementById(announcementId)).thenReturn(Optional.empty());

        // Call the method under test
        reportService.generateAnnouncementFile(announcementId, title, callback);

        // Verify that the callback is called with failure
        verify(callback).onFailure(any(RuntimeException.class));
    }

}
