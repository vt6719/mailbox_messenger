package com.example.messenger.controller;

import com.example.messenger.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadMedia(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Файл пуст"));
        }

        String mediaUrl = fileStorageService.storeMedia(file);

        return ResponseEntity.ok(Map.of(
                "url", mediaUrl,
                "filename", file.getOriginalFilename(),
                "size", file.getSize(),
                "contentType", file.getContentType()
        ));
    }

    @PostMapping("/voice")
    public ResponseEntity<Map<String, Object>> uploadVoice(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Файл пуст"));
        }

        String mediaUrl = fileStorageService.storeVoice(file);

        return ResponseEntity.ok(Map.of(
                "url", mediaUrl,
                "filename", file.getOriginalFilename(),
                "size", file.getSize()
        ));
    }

    @GetMapping("/files/{folder}/{filename}")
    public ResponseEntity<Resource> getFile(
            @PathVariable String folder,
            @PathVariable String filename
    ) {
        Resource file = fileStorageService.loadFile(folder, filename);
        String contentType = fileStorageService.getContentType(filename);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(file);
    }
}
