# MCP Integration Guide

Il progetto AI Test Generator ora supporta **Model Context Protocol (MCP)** per l'integrazione avanzata con AI assistants.

## ⚠️ Stato Attuale

Il supporto Spring AI MCP è documentato ma non ancora disponibile nella versione M3. Per ora utilizziamo:
- **MCP Server standalone** (Node.js) - ✅ Funzionante
- **Spring Boot endpoints MCP** - 🔧 Mockup preparato per integrazione futura
- **Integrazione diretta AI assistants** - ✅ Pronto all'uso

## Architettura Hybrid

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  AI Assistant   │◄──►│  Spring Boot    │◄──►│   MCP Server    │
│                 │    │     App         │    │   (Node.js)     │
│  (MCP Client)   │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                              │
                              ▼
                       ┌─────────────────┐
                       │   Traditional   │
                       │   REST APIs     │
                       └─────────────────┘
```

## Setup Completo

### 1. Avvia il MCP Server
```bash
# Windows
start-mcp-server.bat

# Linux/Mac
cd mcp-server && npm install && npm start
```

### 2. Avvia Spring Boot App
```bash
mvn spring-boot:run
```

## Endpoint Disponibili

### API Tradizionali (Esistenti)
- `POST /api/v1/test-generator/generate`
- `POST /api/v1/test-generator/analyze`
- `POST /api/v1/test-generator/generate-and-export`
- `GET /api/v1/test-generator/health`

### API MCP (Nuove)
- `POST /api/v1/test-generator/mcp/generate`
- `POST /api/v1/test-generator/mcp/analyze`
- `POST /api/v1/test-generator/mcp/generate-and-export`
- `GET /api/v1/test-generator/mcp/health`
- `GET /api/v1/test-generator/mcp/tools`

## Tool MCP Disponibili

### 1. `analyze_java_code`
Analizza la struttura e complessità del codice Java.

### 2. `generate_junit_tests`
Genera test JUnit con AI tramite MCP.

### 3. `generate_and_export_tests`
Genera test e li esporta direttamente nella struttura del progetto.

### 4. `get_health_status`
Verifica lo stato del servizio.

## Uso con AI Assistants

### Configurazione MCP
```json
{
  "mcpServers": {
    "ai-test-generator": {
      "command": "node",
      "args": ["C:/path/to/ai-test-generator/mcp-server/server.js"]
    }
  }
}
```

### Esempi d'Uso
```
User: "Analizza questa classe Java e genera test completi"

AI Assistant: [utilizza automaticamente i tool MCP]
1. analyze_java_code
2. generate_and_export_tests
3. Risultato diretto nel progetto
```

## Vantaggi dell'Integrazione MCP

### 1. **Workflow Seamless**
- Zero context switching
- Generazione test inline durante sviluppo
- Export automatico nei path corretti

### 2. **Intelligenza Contestuale**
- AI assistant capisce il contesto completo del progetto
- Suggerimenti personalizzati
- Naming conventions automatiche

### 3. **Flessibilità**
- Usa API REST per integrazione con altri sistemi
- Usa MCP per workflow con AI assistants
- Stesso business logic, interfacce multiple

## Testing dell'Integrazione

### Test MCP Server
```bash
# Testa connessione MCP
curl -X GET http://localhost:8080/api/v1/test-generator/mcp/health

# Lista tool disponibili
curl -X GET http://localhost:8080/api/v1/test-generator/mcp/tools
```

### Test Generazione
```bash
curl -X POST http://localhost:8080/api/v1/test-generator/mcp/generate \
  -H "Content-Type: application/json" \
  -d '{
    "sourceCode": "public class Calculator { public int add(int a, int b) { return a + b; } }",
    "className": "Calculator",
    "packageName": "com.example"
  }'
```

## Troubleshooting

### Errore: "MCP server not available"
1. Verifica che il MCP server sia avviato: `start-mcp-server.bat`
2. Controlla che Node.js sia installato (v18+)
3. Verifica le dipendenze: `cd mcp-server && npm install`

### Errore: Spring AI MCP dependency
1. Verifica la versione Spring AI nel `pom.xml`
2. Controlla che la dipendenza MCP sia disponibile
3. Aggiorna Spring AI se necessario

## Prossimi Passi

1. **Enhanced AI Integration**: Integrazione con modelli AI più avanzati
2. **Real-time Test Updates**: Aggiornamento test in tempo reale
3. **Coverage Analysis**: Analisi coverage automatica via MCP
4. **IDE Plugins**: Plugin per IntelliJ/VSCode

---

**🎉 Il tuo AI Test Generator è ora pronto per l'integrazione avanzata con AI assistants!**