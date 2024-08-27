package com.ace.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
import java.util.Map;
import java.util.concurrent.CompletableFuture;


@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Autowired
    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    //Create Announcement
    @Async("taskExecutor")
    public CompletableFuture<Map<String, Object>> uploadFile(MultipartFile file, String name) throws IOException {
        Map uploadParams = ObjectUtils.asMap(
                "folder", "AcknowledgeHub",// Specify the folder here
                        "public_id",name
        );
        Map<String ,Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
        return CompletableFuture.completedFuture(uploadResult);
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
    public byte[] downloadPdf(String publicId) throws IOException, InterruptedException {
        // Generate the URL to download the PDF
        String url = cloudinary.url().generate( publicId + ".pdf");
        // Create an HTTP client and request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        // Send the request and get the response
        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

        // Check the response status code
        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new IOException("Failed to download PDF, status code: " + response.statusCode());
        }
    }


}
