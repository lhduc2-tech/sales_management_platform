package com.vanesline.sales.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivationResponse {

    private boolean valid;

    private String email;

    @JsonProperty("full_name")
    private String fullName;

    private String message;
}
