# Arquitectura

## Decisión vigente

El repositorio evoluciona como monorepo con:

- `backend/` como núcleo del sistema
- `desktop/` como cliente JavaFX futuro
- `web/` como cliente React inicializado

## Principios

- La lógica de negocio vive en el backend
- Los clientes consumen la API, no los repositorios ni JPA
- Desktop y web deben poder evolucionar sin duplicar reglas del negocio
- La web debe crecer por feature, no como una bolsa de pantallas aisladas

## Estructura interna actual del backend

Las features expuestas siguen esta convención:

```text
feature/
+-- api/
|   +-- dto/
|   +-- mapper/
+-- application/
|   +-- command/
|   +-- exception/
+-- domain/
+-- infrastructure/
```

## Estado real por feature backend

### `usuario`

- Ya quedó cerrada como feature backend para usuarios base
- Expone alta, consulta, listado y modificación
- Usa consultas exactas sobre `TYPE(u) = Usuario` para no mezclar beneficiarios ni repartidores en `/usuarios`
- Mantiene unicidad de correo contra toda la jerarquía de usuarios

### `beneficiario`

- Feature completa a nivel backend
- Expone alta, consulta, modificación y listado por zona/estado
- Reutiliza `Usuario` como base del modelo

### `donacion`

- Tiene `api/`, `application/`, `domain/` e `infrastructure/`
- Expone alta y modificación de donaciones
- La modificación preserva el subtipo existente y valida el payload según ese subtipo
- Maneja subtipos `Alimento` y `Articulo` con validaciones de negocio

### `distribucion`

- Feature completa a nivel backend para los casos actuales
- Expone alta, consulta, modificación y listado por estado/zona
- Usa `@EntityGraph` para evitar problemas de carga diferida al mapear relaciones hacia la API
- Depende de `beneficiario`, `donacion` y `repartidor`

### `reportes`

- Ya existe como feature API/application
- Expone el ranking `GET /reportes/zonas-mayor-distribuciones`
- Se apoya en una consulta agregada sobre `DistribucionRepository`

### `repartidor`

- Ya quedó cerrada como feature backend
- Expone alta, consulta, listado y modificación
- Mantiene unicidad global de correo contra `Usuario` y unicidad propia de `numeroLicencia`
- Sigue siendo relación de `distribucion`, pero ahora también tiene API propia

## Estado real del frontend web

La web se inicializó con una base técnica mínima pero sólida:

```text
web/
+-- src/
|   +-- features/
|   +-- pages/
|   +-- shared/
|       +-- http/
|       +-- layout/
|       +-- ui/
+-- index.html
+-- package.json
+-- tsconfig*.json
+-- vite.config.ts
```

### Decisiones tomadas para `web/`

- React 18 + TypeScript + Vite como base de arranque
- React Router para estructurar navegación por feature
- Axios centralizado en `shared/http/apiClient.ts`
- Layout principal único para evitar duplicación de shell visual
- Páginas y servicios tipados por feature para crecer formularios y listados sin mezclar responsabilidades
- Integración funcional inicial cerrada para `usuario`, `beneficiario` y `repartidor`

### Integración web ya resuelta

- `/usuarios`: alta, edición y listado
- `/beneficiarios`: alta, edición, listado y filtros por barrio/estado
- `/repartidores`: alta, edición y listado
- CORS habilitado en backend para `http://localhost:5173`

## Implicancias para la siguiente etapa

- El backend ya cubre los casos mínimos y especiales previstos para usuario, beneficiario, donación, repartidor y reportes de zonas
- La web ya tiene fundación suficiente y primeros casos de uso reales conectados
- El siguiente paso sano es integrar `donacion`, `distribucion` y `reportes` en `web/`
