package com.zove.app.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.zove.app.dto.SocialDtos.UploadResponse;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
public class MediaStorageService {

    private static final Set<String> ALLOWED_CONTENT_PREFIXES = Set.of("image/", "video/");

    private final Path uploadRoot;

    public MediaStorageService(@Value("${zove.upload-dir:uploads}") String uploadDir) {
        this.uploadRoot = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    public UploadResponse store(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Upload file is empty");
        }

        var contentType = file.getContentType() == null ? "" : file.getContentType();
        var allowed = ALLOWED_CONTENT_PREFIXES.stream().anyMatch(contentType::startsWith);
        if (!allowed) {
            throw new ResponseStatusException(BAD_REQUEST, "Only image and video uploads are supported");
        }

        try {
            Files.createDirectories(uploadRoot);
            var originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "media" : file.getOriginalFilename());
            var extension = extensionOf(originalName);
            var fileName = UUID.randomUUID() + extension;
            var target = uploadRoot.resolve(fileName).normalize();
            file.transferTo(target);
            return new UploadResponse("/uploads/" + fileName, fileName, file.getSize());
        } catch (IOException exception) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Could not store upload");
        }
    }

    public Path uploadRoot() {
        return uploadRoot;
    }

    private String extensionOf(String fileName) {
        var index = fileName.lastIndexOf('.');
        if (index < 0 || index == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(index);
    }
}
