package com.ace.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.exceptions.NotFound;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;


import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
public class CloudinaryService {

    private static final String[] PREDEFINED_FOLDERS = {"images", "documents", "archives", "spreadsheets"};
    private final Cloudinary cloudinary;

    @Autowired
    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    // Create Announcement
    @Async("taskExecutor")
    public CompletableFuture<Map<String, Object>> uploadFile(MultipartFile file, String name) throws IOException {
        String baseFolder = "AcknowledgeHub";
        String contentType = file.getContentType();
        String folder;
        String newFileName;

        String latestFilePath = findLatestFileByBaseName(name);
        int versionNumber = 1;
        if (latestFilePath != null) {
            // Extract the latest version number and increment it
            versionNumber = extractVersionNumber(latestFilePath) + 1;
        }

        // Generate the new file name with the incremented version
        newFileName = name + "_V" + versionNumber;


        // Determine the appropriate folder based on the content type
        if (contentType != null && contentType.startsWith("image/")) {
            folder = baseFolder + "/images/" + name;
        } else if (contentType != null && contentType.equals("application/pdf")) {
            folder = baseFolder + "/documents/" + name;
        } else if (contentType != null && contentType.equals("application/x-zip-compressed")) {
            folder = baseFolder + "/archives/" + name;
            newFileName += ".zip";  // Ensure the name ends with .zip
        } else if (contentType != null && contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            folder = baseFolder + "/spreadsheets/" + name;
            newFileName += ".xlsx";  // Ensure the name ends with .xlsx
        } else {
            folder = baseFolder + "/others/" + name;  // Default folder for unknown types
        }

        // Find the latest version of the file across all folders


        // Set the resource type for ZIP files
        String resourceType = "auto";  // Default to auto-detect
        if (contentType != null && contentType.equals("application/x-zip-compressed")) {
            resourceType = "raw";  // Explicitly set resource type for ZIP files
        }

        // Upload the file to Cloudinary
        Map<String, Object> uploadParams = ObjectUtils.asMap(
                "folder", folder,
                "public_id", newFileName,
                "resource_type", resourceType
        );
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
        System.out.println(uploadResult);
        return CompletableFuture.completedFuture(uploadResult);
    }


    public String findLatestFileByBaseName(String baseName) {
        AtomicReference<String> latestVersionedFileRef = new AtomicReference<>(null);

        for (String predefinedFolder : PREDEFINED_FOLDERS) {
            String prefix = "AcknowledgeHub/" + predefinedFolder + "/" + baseName;
            String[] resourceTypes = {"image", "raw", "auto"};
            String fileExtensionPattern;

            // Special handling for archives and spreadsheets
            if (predefinedFolder.equals("archives")) {
                fileExtensionPattern = ".*\\.zip$";
            } else if (predefinedFolder.equals("spreadsheets")) {
                fileExtensionPattern = ".*\\.xlsx$";
            } else {
                fileExtensionPattern = ".*";
            }

            for (String resourceType : resourceTypes) {
                try {
                    Map<String, Object> result = cloudinary.api().resources(ObjectUtils.asMap(
                            "type", "upload",
                            "prefix", prefix,
                            "resource_type", resourceType,
                            "max_results", 500
                    ));

                    if (result != null && result.containsKey("resources")) {
                        List<Map<String, Object>> resources = (List<Map<String, Object>>) result.get("resources");

                        if (!resources.isEmpty()) {
                            List<String> versionedFiles = resources.stream()
                                    .map(resource -> (String) resource.get("public_id"))
                                    .filter(file -> file.matches(".*_V\\d+.*" + fileExtensionPattern))
                                    .collect(Collectors.toList());

                            for (String file : versionedFiles) {
                                String currentLatestFile = latestVersionedFileRef.get();
                                if (currentLatestFile == null || extractVersionNumber(file) > extractVersionNumber(currentLatestFile)) {
                                    latestVersionedFileRef.set(file);
                                }
                            }
                        }
                    }
                } catch (NotFound e) {
                    System.out.println("Path not found: " + prefix);
                } catch (HttpStatusCodeException e) {
                    System.err.println("HTTP Status Code Error: " + e.getStatusCode());
                    System.err.println("Response Body: " + e.getResponseBodyAsString());
                } catch (Exception e) {

                }
            }
        }
        return latestVersionedFileRef.get();
    }

    // Helper method to extract version number from file name
    private int extractVersionNumber(String fileName) {
        Pattern pattern = Pattern.compile("_V(\\d+)");  // Match version number
        Matcher matcher = pattern.matcher(fileName);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                System.err.println("Error parsing version number in file name: " + fileName);
            }
        }
        return 0;  // Default to 0 if no version number is found
    }






    //Delete Announcement
    public void deleteFile(String publicId) {
        try {
            String fullPublicId = "AcknowledgeHub/" + publicId; // Ensure the folder name is included
            cloudinary.uploader().destroy(fullPublicId, ObjectUtils.emptyMap());
            System.out.println("Deleted file with publicId: " + fullPublicId);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error deleting file: " + e.getMessage());
        }
    }


    public Map getFile(String publicId) {
        try {
            publicId = ("AcknowledgeHub/"+publicId);
            Map result = cloudinary.api().resource(publicId, ObjectUtils.emptyMap());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public String getUrlsOfAnnouncements(String publicId){
        try{
            // Step 1: Request the resource with the `attachment` flag to force a download
            Map result = cloudinary.api().resource(publicId, ObjectUtils.asMap(
                    "flags", "attachment"  // This ensures the URL is for downloading
            ));

            // Step 2: Return the secure URL that triggers the download
            return result.get("secure_url").toString();
        } catch (Exception e){
            System.out.println(e.getMessage());
            return "";
        }
    }


    public Map<String, Object> downloadFile(String publicId) throws IOException, InterruptedException {
        // Generate the URL to download the file
        String url = cloudinary.url().generate(publicId);

        // Create an HTTP client and request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        // Send the request and get the response
        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

        // Check the response status code
        if (response.statusCode() == 200) {
            // Extract content type from response headers if available
            String contentType = response.headers().firstValue("Content-Type").orElse("application/octet-stream");

            // Determine file extension based on content type
            String fileExtension = determineFileExtension(contentType);

            Map<String, Object> fileData = new HashMap<>();
            fileData.put("fileBytes", response.body());
            fileData.put("contentType", contentType);
            fileData.put("fileName", publicId + fileExtension);

            return fileData;
        } else {
            throw new IOException("Failed to download file, status code: " + response.statusCode());
        }
    }

    private String determineFileExtension(String contentType) {
        switch (contentType) {
            case "application/pdf":
                return ".pdf";
            case "application/vnd.ms-excel":
                return ".xls";
            case "application/zip":
                return ".zip";
            // Add more cases as needed
            default:
                return ".bin"; // Default extension for unknown types
        }
    }





}
