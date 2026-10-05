# SaborApp

Aplicación Android nativa para la Pollería El Buen Sabor, desarrollada con Kotlin, layouts XML y Material Components.

## Estado del proyecto

- Sprint 1: login, menú principal, navegación e identidad visual. ✅
- Sprint 2: SQLite, usuarios, platos y mesas. ✅
- Sprint 3: CRUD y pedidos por mesa. ✅
- Sprint 4: reportes, sesión, compartir cuenta y APK final. ✅

La rama `Sprint-4` contiene la entrega final y conserva todo lo realizado en los sprints anteriores.

## Credenciales temporales

- Administrador: `admin` / `1234`
- Mozo: `mozo` / `1234`

Las credenciales se validan contra la base de datos local `saborapp.db`.

## Sprint 2

- Base de datos SQLite versión 1 con las tablas `usuario`, `plato` y `mesa`.
- Login parametrizado mediante DAO.
- Registro y listado persistente de platos por categoría y nombre.
- Registro y listado de mesas en una grilla de tres columnas.
- Validación de precios, capacidad y números de mesa duplicados.

## Sprint 3

- Edición, eliminación y búsqueda de platos por nombre.
- Migración de SQLite a versión 2 sin borrar los catálogos existentes.
- Tablas `pedido` y `detalle_pedido` con claves foráneas.
- Apertura de pedidos por mesa y registro del precio histórico de cada plato.
- Subtotales y total recalculados dentro de transacciones.
- Diferenciación visual entre mesas libres y ocupadas.
- Cuenta detallada y cierre transaccional que libera la mesa.

## Sprint 4

- Reporte diario de ventas, cantidad de pedidos y ticket promedio.
- Ranking de los cinco platos más vendidos y ventas agrupadas por mesa.
- Estado vacío cuando todavía no existen ventas cerradas en el día.
- Cuenta y resumen de ventas compartibles mediante cualquier aplicación compatible.
- Sesión persistente con cierre de sesión seguro.
- Interfaz minimalista renovada con gradientes, tarjetas, iconos vectoriales y estados visuales.
- APK release firmado compatible desde Android 7.0 (API 24).

## Requisitos

- Android Studio
- Android SDK 24 o superior
- JDK incluido con Android Studio

## Compilación

En Windows:

```powershell
.\gradlew.bat assembleDebug
```

El APK de depuración se genera en `app/build/outputs/apk/debug/`.

Para una entrega final ya compilada, consulta `releases/SaborApp-v1.0.apk`.

La compilación local de una nueva versión release requiere un archivo ignorado
`keystore.properties` y su almacén de claves. Esos secretos no se publican en Git.
