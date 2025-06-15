package com.project_team09.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    @Autowired
    private ResourceLoader resourceLoader;

    /**
     * Endpoint to download template files
     * 
     * @param fileName The name of the file to download
     * @return ResponseEntity with the file as resource
     * @throws IOException If file cannot be read
     */
    @GetMapping("/{fileName:.+}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Resource> downloadTemplate(@PathVariable String fileName) throws IOException {
        // Load file as resource
        Resource resource = resourceLoader.getResource("classpath:templates/" + fileName);

        // Check if file exists
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        // Set content type
        String contentType = "application/octet-stream";
        if (fileName.endsWith(".xlsx")) {
            contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        } else if (fileName.endsWith(".xls")) {
            contentType = "application/vnd.ms-excel";
        }

        // Return the file with proper headers
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }
}
