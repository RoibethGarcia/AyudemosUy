# Ayudemos.uy

Repositorio principal del sistema de gestión de donaciones de Ayudemos.uy.

## Estado actual

El repositorio ya está organizado como monorepo:

- `backend/` contiene el núcleo actual en Java 21 + Spring Boot
- `desktop/` permanece como placeholder para una futura app JavaFX
- `web/` ya tiene una base inicial en React 18 + TypeScript + Vite

## Estructura del repositorio

```text
AyudemosUy/
+-- backend/
+-- desktop/
+-- docs/
|   +-- arquitectura/
+-- web/
```

## Backend

El backend actual vive en `backend/` y hoy concentra la lógica de negocio expuesta por API REST.

### Stack verificado en código

- Java 21
- Spring Boot 3.2.9
- Spring Data JPA
- MySQL / H2
- MapStruct
- JUnit 5 + Mockito

### Estado funcional del backend

#### Features con API y lógica de negocio expuesta

- **Usuarios base**
  - `POST /usuarios`
  - `GET /usuarios`
  - `GET /usuarios/{id}`
  - `PUT /usuarios/{id}`
- **Beneficiarios**
  - `POST /beneficiarios`
  - `GET /beneficiarios`
  - `GET /beneficiarios/{id}`
  - `PUT /beneficiarios/{id}`
- **Donaciones**
  - `POST /donaciones`
  - `PUT /donaciones/{id}`
- **Distribuciones**
  - `POST /distribuciones`
  - `GET /distribuciones`
  - `GET /distribuciones/{id}`
  - `PUT /distribuciones/{id}`
- **Reportes**
  - `GET /reportes/zonas-mayor-distribuciones`
- **Repartidores**
  - `POST /repartidores`
  - `GET /repartidores`
  - `GET /repartidores/{id}`
  - `PUT /repartidores/{id}`

#### Features todavía parciales

- **Distribuciones**: todavía no expone búsquedas o reportes temporales más avanzados fuera del ranking por zona
- **Frontend**: donaciones, distribuciones y reportes siguen en modo semilla; usuarios, beneficiarios y repartidores ya están conectados

> La API `/usuarios` trabaja sobre usuarios base (`TYPE(u) = Usuario`) para no mezclar beneficiarios ni repartidores dentro de un endpoint genérico.
>
> La actualización de `/donaciones/{id}` preserva el subtipo existente (`ALIMENTO` o `ARTICULO`); no permite convertir una donación entre subtipos en el mismo endpoint.
>
> La API `/repartidores` modela al subtipo explícito y valida unicidad tanto de correo como de número de licencia.

Base URL prevista: `http://localhost:8080/api`

## Web

El módulo `web/` ya no es un placeholder vacío. Hoy incluye:

- React 18 + TypeScript + Vite
- React Router para navegación principal
- cliente Axios con `VITE_API_BASE_URL`
- layout principal y páginas semilla para las features del backend
- formularios y listados conectados para:
  - usuarios
  - beneficiarios
  - repartidores

### Próximo paso recomendado en frontend

- conectar donaciones
- conectar distribuciones
- conectar el reporte por zonas
- refinar estados de carga, vacíos y feedback visual en toda la UI

## Comandos del backend

Desde la raíz:

```powershell
.\mvnw.cmd -pl backend test
.\mvnw.cmd -pl backend spring-boot:run
```

O apuntando directo al módulo:

```powershell
.\mvnw.cmd -f backend\pom.xml test
.\mvnw.cmd -f backend\pom.xml spring-boot:run
```

## Configuración del backend

Variables soportadas:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

Los valores por defecto están definidos en `backend/src/main/resources/application.properties`.

## Prioridades recomendadas

### Backend

- verificar por ejecución el wrapper de Maven cuando se habilite correr comandos de verificación
- revisar si hacen falta endpoints adicionales de distribución
- estabilizar el contrato API antes de crecer formularios complejos en web

### Frontend

- conectar donaciones, distribuciones y reportes
- extraer contratos tipados por feature donde siga faltando
- endurecer manejo de errores y estados de carga
