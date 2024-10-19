package com.msx.springai.model;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record GetCapitalResponseWithInfo(@JsonPropertyDescription("This is the capital") String capital,
    @JsonPropertyDescription("This is the population") String population,
    @JsonPropertyDescription("This is the region") String region,
    @JsonPropertyDescription("This is the language") String language,
    @JsonPropertyDescription("This is the currency") String currency) {
}
