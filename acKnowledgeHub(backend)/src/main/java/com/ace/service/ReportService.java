package com.ace.service;

import com.ace.dto.FeedbackListResponseDTO;
import com.ace.entity.Announcement.Announcement;
//import net.sf.dynamicreports.report.builder.DynamicReports;
//import net.sf.dynamicreports.report.builder.ReportBuilder;
//import net.sf.dynamicreports.report.builder.component.TextFieldBuilder;
//import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;

@Service
public class ReportService {

    private final AnnouncementService announcementService;
    private final FeedbackService feedbackService;

    public ReportService(AnnouncementService announcementService, FeedbackService feedbackService){
        this.announcementService = announcementService;
        this.feedbackService = feedbackService;
    }


    @Async("taskExecutor")
    public void generateAnnouncementFile(Integer id, String title, AsyncCallback<byte[]> callback) {
        try {
            // Load the JasperReport template from the resources
            InputStream reportStream = getClass().getResourceAsStream("/reports/announcement.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            // Fetch the announcement data
            Announcement announcement = announcementService.getAnnouncementById(id)
                    .orElseThrow(() -> new RuntimeException("Announcement not found"));

            // Prepare data source for the report
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(Collections.singletonList(announcement));

            // Set report parameters
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("REPORT_TITLE", title);

            // Fill the report with data
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            // Export the report to PDF
            byte[] pdfData = JasperExportManager.exportReportToPdf(jasperPrint);

            // Invoke the callback with the generated PDF data
            callback.onSuccess(pdfData);

        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(new RuntimeException("Failed to generate PDF", e));
        }
    }

    @Async("taskExecutor")
    public void generateFeedbackReport(Integer announcementId, String format, AsyncCallback<byte[]> callback) {
        try {
            // Load the JasperReport template for feedback
            InputStream reportStream = getClass().getResourceAsStream("/reports/feedbackReport.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            // Fetch feedback data for the given announcement ID
            List<FeedbackListResponseDTO> feedbackList = feedbackService.getFeedbackByAnnouncement(announcementId);

            // Prepare data source for the report
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(feedbackList);

            // Set report parameters
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("REPORT_TITLE", "Report for Announcement ID: " + announcementId);

            // Fill the report with data
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            byte[] outputData;
            if ("pdf".equalsIgnoreCase(format)) {
                // Export the report to PDF
                outputData = JasperExportManager.exportReportToPdf(jasperPrint);
            } else if ("excel".equalsIgnoreCase(format)) {
                // Export the report to Excel
                ByteArrayOutputStream xlsReportStream = new ByteArrayOutputStream();
                JRXlsxExporter exporter = new JRXlsxExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(xlsReportStream));

                SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
                configuration.setOnePagePerSheet(true);
                configuration.setDetectCellType(true);
                exporter.setConfiguration(configuration);

                exporter.exportReport();
                outputData = xlsReportStream.toByteArray();
            } else {
                throw new IllegalArgumentException("Unsupported report format: " + format);
            }

            // Invoke the callback with the generated data
            callback.onSuccess(outputData);

        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(new RuntimeException("Failed to generate feedback report", e));
        }
    }


}
