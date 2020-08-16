package com.mybank.api.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

public interface UploadService {
    @Async
    public abstract void uploadFile(final MultipartFile multipartFile) ;
}
