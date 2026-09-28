# Diagrama de infraestructura

```mermaid
flowchart LR
    Usuario(["Usuario"])

    subgraph PC[""]
        Programa["Programa Java"]
    end

    subgraph Railway["Railway (producción)"]
        Servidor[("Servidor con base de datos")]
    end

    subgraph Casero["Pruebas"]
        ServerCasero[("servercasero")]
    end

    Usuario --> Programa
    Programa -->|"guarda datos"| Servidor
    Servidor -->|"devuelve datos"| Programa
    Programa -.->|"guarda datos (pruebas)"| ServerCasero
    ServerCasero -.->|"devuelve datos (pruebas)"| Programa
```

# Modelo Relacional
