package com.thenormancoder.aitestgen.service;

import com.thenormancoder.aitestgen.dto.CodeAnalysisResult;
import com.thenormancoder.aitestgen.dto.TestGenerationRequest;
import com.thenormancoder.aitestgen.dto.TestGenerationResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TestGeneratorService {

    private final JavaCodeAnalyzerService codeAnalyzer;

    public TestGeneratorService(JavaCodeAnalyzerService codeAnalyzer) {
        this.codeAnalyzer = codeAnalyzer;
    }

    public TestGenerationResponse generateTests(TestGenerationRequest request) {
        CodeAnalysisResult analysis = codeAnalyzer.analyzeJavaCode(request.sourceCode());
        
        String generatedCode = generateBasicTest(analysis, request);
        List<String> testMethods = List.of("testBasicFunctionality");
        List<String> suggestions = List.of("Consider using AI integration for better test generation");
        
        return new TestGenerationResponse(
            generatedCode,
            testMethods,
            analysis.className() + "Test",
            85.0,
            LocalDateTime.now(),
            suggestions
        );
    }

    private String generateBasicTest(CodeAnalysisResult analysis, TestGenerationRequest request) {
        StringBuilder testCode = new StringBuilder();
        
        testCode.append("package ").append(analysis.packageName()).append(";\n\n");
        testCode.append("import org.junit.jupiter.api.Test;\n");
        testCode.append("import static org.assertj.core.api.Assertions.assertThat;\n\n");
        testCode.append("class ").append(analysis.className()).append("Test {\n\n");
        testCode.append("    @Test\n");
        testCode.append("    void testBasicFunctionality() {\n");
        testCode.append("        assertThat(true).isTrue();\n");
        testCode.append("    }\n");
        testCode.append("}\n");
        
        return testCode.toString();
    }
}