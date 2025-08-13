package com.thenormancoder.aitestgen.service;

import com.thenormancoder.aitestgen.dto.CodeAnalysisResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JavaCodeAnalyzerServiceStandaloneTest {

    private JavaCodeAnalyzerService analyzer;

    @BeforeEach
    void setUp() {
        analyzer = new JavaCodeAnalyzerService();
    }

    @Test
    void shouldAnalyzeSimpleClass() {
        String sourceCode = """
            package com.example;
            
            public class Calculator {
                private int value;
                
                public Calculator(int initialValue) {
                    this.value = initialValue;
                }
                
                public int add(int number) {
                    return value + number;
                }
                
                public static int multiply(int a, int b) {
                    return a * b;
                }
            }
            """;

        CodeAnalysisResult result = analyzer.analyzeJavaCode(sourceCode);

        assertThat(result.className()).isEqualTo("Calculator");
        assertThat(result.packageName()).isEqualTo("com.example");
        assertThat(result.hasConstructors()).isTrue();
        assertThat(result.methods()).hasSize(2);
        assertThat(result.fields()).hasSize(1);
        assertThat(result.classType()).isEqualTo("class");
    }

    @Test
    void shouldAnalyzeMethodDetails() {
        String sourceCode = """
            public class TestClass {
                public int add(int a, int b) {
                    return a + b;
                }
                
                private void privateMethod() {
                }
                
                public static String staticMethod(String input) {
                    return input.toUpperCase();
                }
            }
            """;

        CodeAnalysisResult result = analyzer.analyzeJavaCode(sourceCode);

        assertThat(result.methods()).hasSize(3);
        
        CodeAnalysisResult.MethodInfo addMethod = result.methods().stream()
            .filter(m -> m.name().equals("add"))
            .findFirst()
            .orElseThrow();
            
        assertThat(addMethod.isPublic()).isTrue();
        assertThat(addMethod.isStatic()).isFalse();
        assertThat(addMethod.returnType()).isEqualTo("int");
        assertThat(addMethod.parameters()).hasSize(2);
    }

    @Test
    void shouldThrowExceptionForInvalidCode() {
        String invalidCode = "invalid java code";
        
        assertThrows(RuntimeException.class, () -> {
            analyzer.analyzeJavaCode(invalidCode);
        });
    }

    @Test
    void shouldAnalyzeInterface() {
        String sourceCode = """
            public interface Calculator {
                int add(int a, int b);
                default int subtract(int a, int b) {
                    return a - b;
                }
            }
            """;

        CodeAnalysisResult result = analyzer.analyzeJavaCode(sourceCode);

        assertThat(result.classType()).isEqualTo("interface");
        assertThat(result.methods()).hasSize(2);
    }

    @Test
    void shouldAnalyzeFieldsCorrectly() {
        String sourceCode = """
            public class TestClass {
                private static final String CONSTANT = "test";
                private int privateField;
                public String publicField;
                protected boolean protectedField;
            }
            """;

        CodeAnalysisResult result = analyzer.analyzeJavaCode(sourceCode);

        assertThat(result.fields()).hasSize(4);
        
        CodeAnalysisResult.FieldInfo constantField = result.fields().stream()
            .filter(f -> f.name().equals("CONSTANT"))
            .findFirst()
            .orElseThrow();
            
        assertThat(constantField.isStatic()).isTrue();
        assertThat(constantField.isFinal()).isTrue();
        assertThat(constantField.type()).isEqualTo("String");
    }
}