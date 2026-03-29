package com.malgn.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.io.Resource;

@Getter
@AllArgsConstructor
public class FileDownloadDto {
    private Resource resource;
    private String contentDisposition;
}
