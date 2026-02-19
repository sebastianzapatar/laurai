package com.nomelestar.lauracanceleatiempo.excepciones;

import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
public class ApiError {
    private Instant timestamp;
    private int status;
    private String error;
    private String path;
    private Map<String, String> fieldsErrors;
}
