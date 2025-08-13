package com.thenormancoder.aitestgen.controller;

import com.thenormancoder.aitestgen.dto.CodeAnalysisResult;
import com.thenormancoder.aitestgen.dto.TestExportRequest;
import com.thenormancoder.aitestgen.dto.TestGenerationRequest;
import com.thenormancoder.aitestgen.dto.TestGenerationResponse;
import com.thenormancoder.aitestgen.service.JavaCodeAnalyzerService;
import com.thenormancoder.aitestgen.service.TestGeneratorService;
import com.thenormancoder.aitestgen.service.TestExportService;
import com.thenormancoder.aitestgen.service.MCPTestGeneratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/test-generator")
@CrossOrigin(origins = "*")
public class TestGeneratorController {

    private final TestGeneratorService testGeneratorService;
    private final JavaCodeAnalyzerService codeAnalyzerService;
    private final TestExportService testExportService;
    private final MCPTestGeneratorService mcpTestGeneratorService;

    public TestGeneratorController(TestGeneratorService testGeneratorService, 
                                 JavaCodeAnalyzerService codeAnalyzerService,
                                 TestExportService testExportService,
                                 MCPTestGeneratorService mcpTestGeneratorService) {
        this.testGeneratorService = testGeneratorService;
        this.codeAnalyzerService = codeAnalyzerService;
        this.testExportService = testExportService;
        this.mcpTestGeneratorService = mcpTestGeneratorService;
    }

    @PostMapping("/generate")
    public ResponseEntity<TestGenerationResponse> generateTests(@RequestBody TestGenerationRequest request) {
        try {
            TestGenerationResponse response = testGeneratorService.generateTests(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/analyze")
    public ResponseEntity<CodeAnalysisResult> analyzeCode(@RequestBody Map<String, String> request) {
        try {
            String sourceCode = request.get("sourceCode");
            if (sourceCode == null || sourceCode.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            CodeAnalysisResult analysis = codeAnalyzerService.analyzeJavaCode(sourceCode);
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "AI Test Generator",
            "version", "1.0.0"
        ));
    }

    @PostMapping("/generate-and-export")
    public ResponseEntity<Map<String, Object>> generateAndExportTests(@RequestBody TestExportRequest request) {
        try {
            // Verifica se il progetto ha una struttura valida
            if (!testExportService.isValidProjectStructure(request.projectPath())) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid project structure. No pom.xml or build.gradle found."
                ));
            }
            
            // Genera i test
            TestGenerationRequest genRequest = new TestGenerationRequest(
                request.sourceCode(), request.className(), request.packageName(),
                request.testType(), request.coverageLevel()
            );
            
            TestGenerationResponse response = testGeneratorService.generateTests(genRequest);
            
            // Esporta i test nel file system
            testExportService.exportTestToFile(
                response.generatedTestCode(), 
                request.packageName(), 
                request.className(), 
                request.projectPath()
            );
            
            String filePath = testExportService.buildTestFilePath(
                request.packageName(), request.className(), request.projectPath()
            ).toString();
            
            TestExportService.ProjectType projectType = testExportService.detectProjectType(request.projectPath());
            
            return ResponseEntity.ok(Map.of(
                "message", "Test generated and exported successfully",
                "filePath", filePath,
                "projectType", projectType,
                "testClassName", response.testClassName(),
                "testMethods", response.testMethods(),
                "estimatedCoverage", response.estimatedCoverage(),
                "suggestions", response.suggestions()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to generate and export tests: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/templates")
    public ResponseEntity<Map<String, Object>> getTestTemplates() {
        return ResponseEntity.ok(Map.of(
            "testTypes", TestGenerationRequest.TestType.values(),
            "coverageLevels", TestGenerationRequest.CoverageLevel.values(),
            "sampleRequest", Map.of(
                "sourceCode", "public class Calculator {\n    public int add(int a, int b) {\n        return a + b;\n    }\n}",
                "className", "Calculator",
                "packageName", "com.example",
                "testType", "UNIT_TEST",
                "coverageLevel", "COMPREHENSIVE"
            ),
            "sampleExportRequest", Map.of(
                "sourceCode", "public class Calculator {\n    public int add(int a, int b) {\n        return a + b;\n    }\n}",
                "className", "Calculator",
                "packageName", "com.example",
                "testType", "UNIT_TEST",
                "coverageLevel", "COMPREHENSIVE",
                "projectPath", "/path/to/your/java/project"
            )
        ));
    }

    // ==================== MCP ENDPOINTS ====================

    @PostMapping("/mcp/generate")
    public ResponseEntity<TestGenerationResponse> generateTestsWithMCP(@RequestBody TestGenerationRequest request) {
        try {
            TestGenerationResponse response = mcpTestGeneratorService.generateTestsWithMCP(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/mcp/generate-and-export")
    public ResponseEntity<Map<String, Object>> generateAndExportWithMCP(@RequestBody TestExportRequest request) {
        try {
            Map<String, Object> result = mcpTestGeneratorService.generateAndExportWithMCP(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to generate and export tests via MCP: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/mcp/analyze")
    public ResponseEntity<Map<String, String>> analyzeCodeWithMCP(@RequestBody Map<String, String> request) {
        try {
            String sourceCode = request.get("sourceCode");
            if (sourceCode == null || sourceCode.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            String analysis = mcpTestGeneratorService.analyzeCodeWithMCP(sourceCode);
            return ResponseEntity.ok(Map.of("analysis", analysis));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to analyze code via MCP: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/mcp/health")
    public ResponseEntity<Map<String, String>> mcpHealth() {
        try {
            String status = mcpTestGeneratorService.getHealthStatus();
            return ResponseEntity.ok(Map.of("mcpStatus", status));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("mcpStatus", "Unavailable: " + e.getMessage()));
        }
    }

    @GetMapping("/mcp/tools")
    public ResponseEntity<Map<String, Object>> getAvailableMCPTools() {
        try {
            var tools = mcpTestGeneratorService.getAvailableTools();
            return ResponseEntity.ok(Map.of(
                "tools", tools,
                "count", tools.size(),
                "description", "Available MCP tools for AI Test Generator"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to get MCP tools: " + e.getMessage()
            ));
        }
    }
}