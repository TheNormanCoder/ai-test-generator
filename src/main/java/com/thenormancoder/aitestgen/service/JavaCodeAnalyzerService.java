package com.thenormancoder.aitestgen.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.thenormancoder.aitestgen.dto.CodeAnalysisResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JavaCodeAnalyzerService {

    public CodeAnalysisResult analyzeJavaCode(String sourceCode) {
        try {
            JavaParser parser = new JavaParser();
            CompilationUnit cu = parser.parse(sourceCode).getResult()
                .orElseThrow(() -> new IllegalArgumentException("Invalid Java code"));

            ClassOrInterfaceDeclaration clazz = cu.findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new IllegalArgumentException("No class found in source code"));

            String packageName = cu.getPackageDeclaration()
                .map(pd -> pd.getNameAsString())
                .orElse("");

            List<String> imports = cu.getImports().stream()
                .map(importDecl -> importDecl.getNameAsString())
                .collect(Collectors.toList());

            List<CodeAnalysisResult.MethodInfo> methods = analyzeMethods(clazz);
            List<CodeAnalysisResult.FieldInfo> fields = analyzeFields(clazz);
            
            boolean hasConstructors = clazz.getConstructors().size() > 0;
            String classType = clazz.isInterface() ? "interface" : "class";

            return new CodeAnalysisResult(
                clazz.getNameAsString(),
                packageName,
                methods,
                fields,
                imports,
                hasConstructors,
                classType
            );

        } catch (Exception e) {
            throw new RuntimeException("Error analyzing Java code: " + e.getMessage(), e);
        }
    }

    private List<CodeAnalysisResult.MethodInfo> analyzeMethods(ClassOrInterfaceDeclaration clazz) {
        List<CodeAnalysisResult.MethodInfo> methods = new ArrayList<>();
        
        for (MethodDeclaration method : clazz.getMethods()) {
            List<CodeAnalysisResult.ParameterInfo> parameters = method.getParameters().stream()
                .map(this::createParameterInfo)
                .collect(Collectors.toList());
            
            String javadoc = method.getJavadocComment()
                .map(jd -> jd.getContent())
                .orElse("");

            methods.add(new CodeAnalysisResult.MethodInfo(
                method.getNameAsString(),
                method.getTypeAsString(),
                parameters,
                method.isPublic(),
                method.isStatic(),
                javadoc
            ));
        }
        
        return methods;
    }

    private List<CodeAnalysisResult.FieldInfo> analyzeFields(ClassOrInterfaceDeclaration clazz) {
        List<CodeAnalysisResult.FieldInfo> fields = new ArrayList<>();
        
        for (FieldDeclaration field : clazz.getFields()) {
            field.getVariables().forEach(variable -> {
                fields.add(new CodeAnalysisResult.FieldInfo(
                    variable.getNameAsString(),
                    field.getElementType().asString(),
                    field.isPrivate(),
                    field.isStatic(),
                    field.isFinal()
                ));
            });
        }
        
        return fields;
    }

    private CodeAnalysisResult.ParameterInfo createParameterInfo(Parameter parameter) {
        return new CodeAnalysisResult.ParameterInfo(
            parameter.getNameAsString(),
            parameter.getTypeAsString()
        );
    }
}