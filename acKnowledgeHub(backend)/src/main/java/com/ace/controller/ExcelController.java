package com.ace.controller;

import com.ace.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/excel")
public class ExcelController {

    private final ExcelService excelService;

    public ExcelController(ExcelService excelService) {
        this.excelService = excelService;
    }

    @PostMapping("/sys/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,@RequestParam("override") Integer override) throws IOException {
        if (file.isEmpty()) {
            return "Please select a file to upload.";
        }
        try{
            if(override == 1){
                excelService.processExcelFile(file,true);
            }else{
                excelService.processExcelFile(file,false);
            }
        }catch (Exception e){
            System.out.println(e.toString());
        }
        return "File uploaded successfully!";
    }
}
