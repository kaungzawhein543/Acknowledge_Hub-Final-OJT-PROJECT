package com.ace.service;

import com.ace.entity.Announcement;
//import net.sf.dynamicreports.report.builder.DynamicReports;
//import net.sf.dynamicreports.report.builder.ReportBuilder;
//import net.sf.dynamicreports.report.builder.component.TextFieldBuilder;
//import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.springframework.orm.hibernate5.SessionFactoryUtils.getDataSource;

@Service
public class ReportService {

    private final AnnouncementService announcementService;

    public ReportService(AnnouncementService announcementService){
        this.announcementService = announcementService;
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


}
