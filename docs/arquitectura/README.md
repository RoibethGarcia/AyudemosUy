# Arquitectura

## Decisión vigente

El repositorio evoluciona a un monorepo con:

- `backend/` como núcleo del sistema
- `desktop/` como cliente JavaFX futuro
- `web/` como cliente React futuro

## Principios

- La lógica de negocio vive en el backend
- Los clientes consumen la API, no los repositorios ni JPA
- Desktop y web deben poder evolucionar sin duplicar reglas del negocio

## Estructura interna actual del backend

Las features expuestas siguen esta convención:

```text
feature/
+-- api/
¦   +-- dto/
¦   +-- mapper/
+-- application/
¦   +-- command/
¦   +-- exception/
+-- domain/
+-- infrastructure/
```

## Estado real por feature

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
- Expone alta de donaciones
- Maneja subtipos `Alimento` y `Articulo` con validaciones de negocio

### `distribucion`

- Feature completa a nivel backend
- Expone alta, consulta, modificación y listado por estado/zona
- Usa `@EntityGraph` para evitar problemas de carga diferida al mapear relaciones hacia la API
- Depende de `beneficiario`, `donacion` y `repartidor`

### `repartidor`

- Está parcial
- Hoy solo existen `domain/` e `infrastructure/`
- Se usa como relación de `distribucion`, pero todavía no tiene casos de uso propios

### `reportes`

- No existe todavía como feature explícita
- El reporte de zonas con mayor número de distribuciones sigue pendiente

## Implicancias para la siguiente etapa

- El backend ya cubre los casos mínimos de usuario, beneficiario, donación y distribución
- La prioridad técnica pasa a ser consolidar la migración monorepo y cerrar los casos especiales pendientes
- Antes de iniciar clientes conviene estabilizar el contrato API del backend y decidir el rol futuro de `repartidor`