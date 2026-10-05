# SaborApp

Aplicación Android nativa para la Pollería El Buen Sabor, desarrollada con Kotlin, layouts XML y Material Components.

## Estado del proyecto

- Sprint 1: login, menú principal, navegación e identidad visual. ✅
- Sprint 2: SQLite, usuarios, platos y mesas. ✅
- Sprint 3: CRUD y pedidos por mesa.
- Sprint 4: reportes, sesión, compartir cuenta y APK final.

La rama `Sprint-2` contiene la segunda entrega y conserva todo lo realizado en el Sprint 1.

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
