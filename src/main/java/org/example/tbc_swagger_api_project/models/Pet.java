package org.example.tbc_swagger_api_project.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Pet {

    private Long id;
    private Category category;
    private String name;
    @Builder.Default
    private List<String> photoUrls = new ArrayList<>();
    @Builder.Default
    private List<Tag> tags = new ArrayList<>();
    private String status;
}
