# Ayudemos.uy

Repositorio principal del sistema de gestión de donaciones de Ayudemos.uy.

## Estado actual

El repositorio ya está organizado como monorepo:

- `backend/` contiene el núcleo actual en Java 21 + Spring Boot
- `desktop/` permanece como placeholder para una futura app JavaFX
- `web/` permanece como placeholder para una futura webapp React

## Estructura del repositorio

```text
AyudemosUy/
+-- backend/
+-- desktop/
+-- docs/
¦   +-- arquitectura/
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

- **Distribuciones**: todavía no expone búsqueda o reportes temporales más avanzados fuera del ranking por zona

> La API `/usuarios` trabaja sobre usuarios base (`TYPE(u) = Usuario`) para no mezclar beneficiarios ni repartidores dentro de un endpoint genérico.
>
> La actualización de `/donaciones/{id}` preserva el subtipo existente (`ALIMENTO` o `ARTICULO`); no permite convertir una donación entre subtipos en el mismo endpoint.
>
> La API `/repartidores` modela al subtipo explícito y valida unicidad tanto de correo como de número de licencia.

Base URL prevista: `http://localhost:8080/api`

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

### Saneamiento de base

- verificar por ejecución el wrapper de Maven cuando se habilite correr comandos de verificación
- revisar si conviene exponer filtros o reportes adicionales sobre distribuciones

### Luego

- estabilizar el contrato API antes de iniciar clientes
- recién después inicializar `desktop/` y `web/`
