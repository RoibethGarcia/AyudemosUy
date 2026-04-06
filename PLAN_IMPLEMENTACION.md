# Plan de Implementación - Ayudemos.uy

## Objetivo

Desarrollar un sistema completo de gestión de donaciones usando Spring Boot en `backend/` y React en `web/`, manteniendo una arquitectura API REST + SPA.

---

## Estado verificado a la fecha

### Backend

- ✅ Monorepo consolidado
- ✅ Features cerradas para:
  - usuarios base
  - beneficiarios
  - donaciones
  - distribuciones
  - repartidores
  - reportes por zonas con mayor número de distribuciones
- ✅ Manejo global de errores
- ✅ Tests unitarios y web tests presentes en varias features
- ⚠️ Falta verificación ejecutable del wrapper y de la suite completa cuando se habilite correr comandos de validación

### Frontend

- ✅ `web/` inicializado con React 18 + TypeScript + Vite
- ✅ React Router configurado
- ✅ Cliente Axios base configurado
- ✅ Layout principal y páginas semilla por feature
- ✅ Casos conectados para:
  - usuarios
  - beneficiarios
  - repartidores
- ⚠️ Falta conectar donaciones, distribuciones y reportes

---

## Roadmap actualizado

### Fase 1 - Backend base

- ✅ Estructura Spring Boot
- ✅ Entidades del dominio
- ✅ Repositorios principales
- ✅ Servicios con lógica de negocio
- ✅ Endpoints REST mínimos

### Fase 2 - Backend avanzado

- ✅ Requerimientos especiales principales
- ✅ Reporte por zonas con mayor número de distribuciones
- ✅ Soporte explícito para repartidores
- ⚠️ Pendiente revisar si se requieren endpoints adicionales de distribución

### Fase 3 - Frontend React

#### Bloque 1: Fundación técnica
- ✅ Proyecto React con Vite
- ✅ React Router
- ✅ Axios para consumo de API
- ✅ Layout y navegación principal
- ✅ Páginas semilla por feature

#### Bloque 2: Casos de uso reales
- ✅ Alta y edición de usuarios
- ✅ Alta, edición y filtros de beneficiarios
- 🔲 Alta y edición de donaciones
- 🔲 Alta y consulta de distribuciones
- ✅ Alta y edición de repartidores
- 🔲 Visualización del reporte por zonas

#### Bloque 3: UX y robustez
- 🔲 Manejo de carga y errores
- 🔲 Validaciones de formularios
- 🔲 Filtros y búsquedas pendientes en módulos restantes
- 🔲 Estados vacíos y feedback visual homogéneo

### Fase 4 - Integración y cierre

- 🔲 Validación end-to-end de flujos principales
- 🔲 Ajustes finales de configuración
- 🔲 Documentación final y preparación para demo

---

## Prioridad técnica recomendada

1. Conectar ahora:
   - donaciones
   - distribuciones
   - reportes
2. Después homogeneizar UX y manejo de errores en todo `web/`
3. Por último refinar visualmente y cerrar demo

---

## Criterio de implementación

- Primero contratos claros
- Luego servicios HTTP tipados
- Después formularios y listados
- Por último refinamientos visuales

NO al revés. SI NO HAY FUNDACIÓN, LA UI TERMINA SIENDO UN PARCHE.
