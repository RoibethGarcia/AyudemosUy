# Ayudemos.uy - Sistema de Gestión de Donaciones

## 📋 Descripción
Sistema completo de gestión de donaciones desarrollado con **Spring Boot** (backend) y **React** (frontend).

Este proyecto es una adaptación moderna de la tarea académica original, migrando de Swing + Hibernate a una arquitectura REST API + SPA moderna.

## 🛠️ Stack Tecnológico

### Backend (en uso)
- **Java 17**
- **Spring Boot 3.2.x**
- **Spring Web** (REST API)
- **Spring Data JPA** (Hibernate)
- **MySQL** (base de datos principal)
- **H2** (base de datos en memoria para desarrollo/testing)
- **Spring Validation** (validaciones con anotaciones)
- **Lombok** (reduce boilerplate en entidades y servicios)
- **Spring DevTools** (hot reload en desarrollo)
- **Spring Boot Starter Test** (JUnit, pruebas)
- **Maven** (gestión de dependencias y build)

### Frontend (planificado)
- **React 18+**
- **TypeScript**
- **Axios** (consumo de APIs)
- **React Router** (navegación)
- **Tailwind CSS** o **Material UI** (UI moderna)

### Herramientas y entorno
- **Git & GitHub** (control de versiones y portfolio)
- **Postman / curl** (pruebas de la API)
- **MySQL Workbench** u otra herramienta SQL

## 📁 Estructura del Proyecto

```
ayudemos-uy-backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── edu/udelar/ayudemos/
│   │   │       ├── AyudemosApplication.java
│   │   │       ├── domain/          # Entidades JPA
│   │   │       ├── repository/      # Repositorios Spring Data
│   │   │       ├── service/         # Lógica de negocio
│   │   │       ├── controller/       # Controladores REST
│   │   │       └── dto/             # Data Transfer Objects
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-dev.properties
│   └── test/
└── pom.xml
```

## 📌 Estado Actual del Proyecto

### Backend
- ✅ **Estructura base Spring Boot** creada y compilando
- ✅ **Entidades del dominio** definidas (Usuario, Beneficiario, Repartidor, Donación, Distribución, etc.)
- ✅ **Repositorios JPA** creados para las entidades principales
- ✅ **Servicio y controlador de ejemplo** implementados para `Beneficiario`
- ✅ **Configuración de base de datos** y propiedades de Spring Boot
- ⏳ Servicios y controladores para **Usuario**, **Donación** y **Distribución**
- ⏳ Validaciones de negocio, manejo global de excepciones y capa de DTOs
- ⏳ Pruebas unitarias e integración
- ⏳ Documentación de API (Swagger/OpenAPI)

### Frontend
- ⏳ Proyecto React aún no creado (fase futura según `PLAN_IMPLEMENTACION.md`)

Para más detalle de roadmap y tareas pendientes, ver:
- `PLAN_IMPLEMENTACION.md`
- `PROXIMOS_PASOS.md`

## 🚀 Cómo Ejecutar

### Prerrequisitos
- Java 17 o superior
- Maven 3.8+
- MySQL 8.0+

### Configuración

1. **Clonar y entrar al proyecto:**
```bash
cd ayudemos-uy-backend
```

2. **Configurar base de datos:**
   - Crear base de datos MySQL: `CREATE DATABASE ayudemos_uy;`
   - Configurar credenciales en `src/main/resources/application.properties`

3. **Ejecutar la aplicación:**
```bash
mvn spring-boot:run
```

La API estará disponible en: `http://localhost:8080/api`

## 📚 Endpoints REST

### Implementados actualmente

#### Beneficiarios
- `POST /api/beneficiarios` - Crear beneficiario
- `GET /api/beneficiarios` - Listar beneficiarios

> Estos endpoints siguen el patrón mostrado en `PROXIMOS_PASOS.md` y sirven como base para el resto de recursos.

### Planificados (según enunciado y roadmap)

#### Usuarios
- `POST /api/usuarios` - Crear usuario (administrador/repartidor)
- `GET /api/usuarios` - Listar usuarios
- `GET /api/usuarios/{id}` - Obtener usuario por ID
- `PUT /api/usuarios/{id}` - Modificar usuario

#### Donaciones
- `POST /api/donaciones` - Crear donación (alimento/artículo)
- `GET /api/donaciones` - Listar donaciones
- `PUT /api/donaciones/{id}` - Modificar donación

#### Distribuciones
- `POST /api/distribuciones` - Crear distribución
- `GET /api/distribuciones` - Listar distribuciones
- `GET /api/distribuciones?estado={estado}` - Filtrar por estado
- `PUT /api/distribuciones/{id}` - Modificar distribución

## 🎓 Objetivos de Aprendizaje

- ✅ Arquitectura REST API con Spring Boot
- ✅ Spring Data JPA y Hibernate
- ✅ Diseño de APIs RESTful
- ✅ Manejo de excepciones y validaciones
- ✅ Pruebas unitarias e integración
- 🔄 Frontend moderno con React (próximamente)

## 📝 Notas

Este proyecto forma parte del plan de fortalecimiento del CV, combinando:
- Backend Java moderno (Spring Boot)
- Frontend moderno (React)
- Buenas prácticas de desarrollo
- Proyecto completo y presentable

## 👤 Autor
Roibeth Garcia

## 📄 Licencia
Proyecto académico / Portfolio personal