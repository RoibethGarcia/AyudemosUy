# Web

Frontend base inicializado con React 18 + TypeScript + Vite.

## Objetivo

- Reutilizar la API del mÃ³dulo `backend`
- Exponer la lÃ³gica de negocio en una SPA desacoplada del dominio y de JPA
- Preparar navegaciÃ³n, layout y cliente HTTP antes de conectar formularios y listados reales

## Estado actual

Este mÃ³dulo ya incluye:

- estructura React + TypeScript
- configuraciÃ³n Vite
- React Router
- cliente Axios
- layout principal
- pÃ¡ginas base para:
  - usuarios
  - beneficiarios
  - donaciones
  - distribuciones
  - repartidores
  - reportes
- integraciÃ³n funcional ya conectada para:
  - usuarios
  - beneficiarios
  - repartidores

## Scripts previstos

```bash
npm install
npm run dev
```

## Variables soportadas

- `VITE_API_BASE_URL` con valor por defecto `http://localhost:8080/api`

## Nota de integraciÃ³n

El backend ya permite CORS para `http://localhost:5173`, que es el puerto por defecto de Vite en esta base.
