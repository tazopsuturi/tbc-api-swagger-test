package org.example.tbc_swagger_api_project.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/** Generic {@code {code, type, message}} envelope Petstore returns for deletes, form updates and errors. */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse {

    private Integer code;
    private String type;
    private String message;
}
