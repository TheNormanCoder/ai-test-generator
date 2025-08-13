package com.thenormancoder.aitestgen.dto;

import java.util.List;

public record CodeAnalysisResult(
    String className,
    String packageName,
    List<MethodInfo> methods,
    List<FieldInfo> fields,
    List<String> imports,
    boolean hasConstructors,
    String classType
) {
    public record MethodInfo(
        String name,
        String returnType,
        List<ParameterInfo> parameters,
        boolean isPublic,
        boolean isStatic,
        String javadoc
    ) {}
    
    public record ParameterInfo(
        String name,
        String type
    ) {}
    
    public record FieldInfo(
        String name,
        String type,
        boolean isPrivate,
        boolean isStatic,
        boolean isFinal
    ) {}
}