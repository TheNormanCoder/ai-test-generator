package com.thenormancoder.aitestgen.dto;

public record TestGenerationRequest(
    String sourceCode,
    String className,
    String packageName,
    TestType testType,
    CoverageLevel coverageLevel
) {
    public enum TestType {
        UNIT_TEST,
        INTEGRATION_TEST,
        MOCK_TEST
    }
    
    public enum CoverageLevel {
        BASIC,
        COMPREHENSIVE,
        EDGE_CASES
    }
}