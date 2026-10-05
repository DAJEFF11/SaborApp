# SaborApp

Aplicación Android nativa para la Pollería El Buen Sabor, desarrollada con Kotlin, layouts XML y Material Components.

## Estado del proyecto

- Sprint 1: login, menú principal, navegación e identidad visual.
- Sprint 2: SQLite, usuarios, platos y mesas.
- Sprint 3: CRUD y pedidos por mesa.
- Sprint 4: reportes, sesión, compartir cuenta y APK final.

La rama `Sprint-1` contiene exclusivamente la primera entrega.

## Credenciales temporales

- Usuario: `admin`
- Contraseña: `1234`

Estas credenciales se reemplazarán por la validación con SQLite en el Sprint 2.

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
