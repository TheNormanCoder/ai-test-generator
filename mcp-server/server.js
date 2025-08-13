#!/usr/bin/env node

import { Server } from "@modelcontextprotocol/sdk/server/index.js";
import { StdioServerTransport } from "@modelcontextprotocol/sdk/server/stdio.js";
import {
  CallToolRequestSchema,
  ListToolsRequestSchema,
} from "@modelcontextprotocol/sdk/types.js";
import axios from "axios";

const API_BASE_URL = "http://localhost:8080/api/v1/test-generator";

class TestGeneratorMCPServer {
  constructor() {
    this.server = new Server(
      {
        name: "ai-test-generator-mcp",
        version: "1.0.0",
      },
      {
        capabilities: {
          tools: {},
        },
      }
    );

    this.setupToolHandlers();
  }

  setupToolHandlers() {
    // List available tools
    this.server.setRequestHandler(ListToolsRequestSchema, async () => ({
      tools: [
        {
          name: "analyze_java_code",
          description: "Analyze Java source code structure and complexity",
          inputSchema: {
            type: "object",
            properties: {
              sourceCode: {
                type: "string",
                description: "Java source code to analyze"
              }
            },
            required: ["sourceCode"]
          }
        },
        {
          name: "generate_junit_tests",
          description: "Generate JUnit tests for Java code with AI",
          inputSchema: {
            type: "object",
            properties: {
              sourceCode: {
                type: "string",
                description: "Java source code to generate tests for"
              },
              className: {
                type: "string",
                description: "Name of the class"
              },
              packageName: {
                type: "string",
                description: "Package name for the class"
              },
              testType: {
                type: "string",
                enum: ["UNIT_TEST", "INTEGRATION_TEST", "MOCK_TEST"],
                description: "Type of tests to generate"
              },
              coverageLevel: {
                type: "string",
                enum: ["BASIC", "COMPREHENSIVE", "EDGE_CASES"],
                description: "Level of test coverage"
              }
            },
            required: ["sourceCode", "className", "packageName"]
          }
        },
        {
          name: "generate_and_export_tests",
          description: "Generate JUnit tests and export them directly to project structure",
          inputSchema: {
            type: "object",
            properties: {
              sourceCode: {
                type: "string",
                description: "Java source code to generate tests for"
              },
              className: {
                type: "string",
                description: "Name of the class"
              },
              packageName: {
                type: "string",
                description: "Package name for the class"
              },
              testType: {
                type: "string",
                enum: ["UNIT_TEST", "INTEGRATION_TEST", "MOCK_TEST"],
                description: "Type of tests to generate"
              },
              coverageLevel: {
                type: "string",
                enum: ["BASIC", "COMPREHENSIVE", "EDGE_CASES"],
                description: "Level of test coverage"
              },
              projectPath: {
                type: "string",
                description: "Absolute path to the Java project root"
              }
            },
            required: ["sourceCode", "className", "packageName", "projectPath"]
          }
        },
        {
          name: "get_health_status",
          description: "Check the health status of the AI Test Generator service",
          inputSchema: {
            type: "object",
            properties: {}
          }
        }
      ]
    }));

    // Handle tool calls
    this.server.setRequestHandler(CallToolRequestSchema, async (request) => {
      const { name, arguments: args } = request.params;

      try {
        switch (name) {
          case "analyze_java_code":
            return await this.analyzeJavaCode(args.sourceCode);
            
          case "generate_junit_tests":
            return await this.generateJUnitTests(args);
            
          case "generate_and_export_tests":
            return await this.generateAndExportTests(args);
            
          case "get_health_status":
            return await this.getHealthStatus();
            
          default:
            throw new Error(`Unknown tool: ${name}`);
        }
      } catch (error) {
        return {
          content: [
            {
              type: "text",
              text: `Error: ${error.message}`
            }
          ]
        };
      }
    });
  }

  async analyzeJavaCode(sourceCode) {
    const response = await axios.post(`${API_BASE_URL}/analyze`, {
      sourceCode
    });
    
    const analysis = response.data;
    return {
      content: [
        {
          type: "text",
          text: `## Code Analysis Results

**Class:** ${analysis.className}
**Package:** ${analysis.packageName}
**Methods:** ${analysis.methods.length}
**Fields:** ${analysis.fields.length}
**Complexity:** ${analysis.complexityScore}

### Methods:
${analysis.methods.map(m => `- ${m.name}(${m.parameters.join(', ')}) -> ${m.returnType}`).join('\n')}

### Fields:
${analysis.fields.map(f => `- ${f.type} ${f.name}`).join('\n')}

### Suggestions:
${analysis.suggestions.map(s => `- ${s}`).join('\n')}`
        }
      ]
    };
  }

  async generateJUnitTests(args) {
    const {
      sourceCode,
      className,
      packageName,
      testType = "UNIT_TEST",
      coverageLevel = "COMPREHENSIVE"
    } = args;

    const response = await axios.post(`${API_BASE_URL}/generate`, {
      sourceCode,
      className,
      packageName,
      testType,
      coverageLevel
    });
    
    const result = response.data;
    return {
      content: [
        {
          type: "text",
          text: `## Generated JUnit Tests

**Test Class:** ${result.testClassName}
**Estimated Coverage:** ${result.estimatedCoverage}%
**Test Methods:** ${result.testMethods.join(', ')}

### Generated Test Code:
\`\`\`java
${result.generatedTestCode}
\`\`\`

### Suggestions:
${result.suggestions.map(s => `- ${s}`).join('\n')}`
        }
      ]
    };
  }

  async generateAndExportTests(args) {
    const {
      sourceCode,
      className,
      packageName,
      testType = "UNIT_TEST",
      coverageLevel = "COMPREHENSIVE",
      projectPath
    } = args;

    const response = await axios.post(`${API_BASE_URL}/generate-and-export`, {
      sourceCode,
      className,
      packageName,
      testType,
      coverageLevel,
      projectPath
    });
    
    const result = response.data;
    return {
      content: [
        {
          type: "text",
          text: `## Tests Generated and Exported Successfully! ✅

**File Created:** \`${result.filePath}\`
**Project Type:** ${result.projectType}
**Test Class:** ${result.testClassName}
**Test Methods:** ${result.testMethods.join(', ')}
**Estimated Coverage:** ${result.estimatedCoverage}%

The test file has been automatically saved to your project's test directory.

### Next Steps:
- Run your tests: \`mvn test\` or \`gradle test\`
- Review and customize the generated tests
- Add additional edge cases if needed

### Suggestions:
${result.suggestions.map(s => `- ${s}`).join('\n')}`
        }
      ]
    };
  }

  async getHealthStatus() {
    const response = await axios.get(`${API_BASE_URL}/health`);
    const health = response.data;
    
    return {
      content: [
        {
          type: "text",
          text: `## Service Health Status

**Status:** ${health.status}
**Service:** ${health.service}
**Version:** ${health.version}

The AI Test Generator service is running and ready to generate tests!`
        }
      ]
    };
  }

  async run() {
    const transport = new StdioServerTransport();
    await this.server.connect(transport);
    console.error("AI Test Generator MCP Server running on stdio");
  }
}

const server = new TestGeneratorMCPServer();
server.run().catch(console.error);