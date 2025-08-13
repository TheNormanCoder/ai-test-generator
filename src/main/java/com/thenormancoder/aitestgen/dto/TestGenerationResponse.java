package com.thenormancoder.aitestgen.dto;

import java.time.LocalDateTime;
import java.util.List;

public record TestGenerationResponse(
    String generatedTestCode,
    List<String> testMethods,
    String testClassName,
    double estimatedCoverage,
    LocalDateTime generatedAt,
    List<String> suggestions
) {}