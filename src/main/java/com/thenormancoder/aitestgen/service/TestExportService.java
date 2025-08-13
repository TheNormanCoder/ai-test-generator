package com.thenormancoder.aitestgen.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Service
public class TestExportService {

    public void exportTestToFile(String testCode, String packageName, String className, String projectPath) throws IOException {
        Path testPath = buildTestFilePath(packageName, className, projectPath);
        
        // Crea le directory se non esistono
        Files.createDirectories(testPath.getParent());
        
        // Scrive il file di test
        Files.writeString(testPath, testCode, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public Path buildTestFilePath(String packageName, String className, String projectPath) {
        String testFileName = className + "Test.java";
        
        // Costruisce il path seguendo la struttura Maven standard
        Path basePath = Paths.get(projectPath, "src", "test", "java");
        
        if (packageName != null && !packageName.isEmpty()) {
            String[] packageParts = packageName.split("\\.");
            for (String part : packageParts) {
                basePath = basePath.resolve(part);
            }
        }
        
        return basePath.resolve(testFileName);
    }

    public boolean isValidProjectStructure(String projectPath) {
        Path pomPath = Paths.get(projectPath, "pom.xml");
        Path buildGradlePath = Paths.get(projectPath, "build.gradle");
        Path buildGradleKtsPath = Paths.get(projectPath, "build.gradle.kts");
        
        return Files.exists(pomPath) || Files.exists(buildGradlePath) || Files.exists(buildGradleKtsPath);
    }

    public ProjectType detectProjectType(String projectPath) {
        if (Files.exists(Paths.get(projectPath, "pom.xml"))) {
            return ProjectType.MAVEN;
        } else if (Files.exists(Paths.get(projectPath, "build.gradle")) || 
                   Files.exists(Paths.get(projectPath, "build.gradle.kts"))) {
            return ProjectType.GRADLE;
        }
        return ProjectType.UNKNOWN;
    }

    public enum ProjectType {
        MAVEN, GRADLE, UNKNOWN
    }
}