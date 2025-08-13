package com.thenormancoder.aitestgen.dto;

public record TestExportRequest(
    String sourceCode,
    String className,
    String packageName,
    TestGenerationRequest.TestType testType,
    TestGenerationRequest.CoverageLevel coverageLevel,
    String projectPath
) {}