-Enlace de la api (https://6ae2c4a7bbb54129229a5e0a.mockapi.io/)


-Los endpoints serán recursos y usuarios directamente.


-Link de la APK de la aplicacion. (https://drive.google.com/drive/folders/1AeUE15cMEyaYcLo5tC-pJxfinzrCGF1W?usp=drive_link).

# AprendeApp

Sistema de Gestión de Recursos de Aprendizaje (libros, videos y artículos de Ingeniería de Sistemas) para Android, desarrollado en **Kotlin** con **Jetpack Compose** y arquitectura **MVC**. Los datos se consumen desde una API REST dinámica (MockAPI.io) con soporte completo de operaciones CRUD, autenticación con roles y persistencia de datos de usuario.

> Tercer Desafío Práctico – Desarrollo de Software para Móviles (DSM) – Universidad Don Bosco, Ciclo II 2026.

**Autor:** Alexander Argueta (`Argueta1939`)

---

## Funcionalidades

### Registro y login
- Registro de usuario con selección de rol (**Estudiante** o **Docente**).
- Login y logout. La sesión se conserva al cerrar la app (SharedPreferences).
- Las contraseñas se almacenan en el backend como hash **SHA-256**, nunca en texto plano.
- Validación segura de contraseñas con checklist en tiempo real:
  - Mínimo 12 caracteres
  - Al menos una letra mayúscula
  - Al menos una letra minúscula
  - Al menos un número
  - Al menos un carácter especial (`! @ # $ % ^ & *`)

### Roles

| Rol | Permisos |
|---|---|
| **Estudiante** | Ver recursos, buscar, filtrar, ordenar, marcar favoritos y calificar (1 a 5 estrellas). |
| **Docente** | Pantalla de administración para crear, editar y eliminar recursos (POST, PUT y DELETE). |

### Gestión de recursos
- **Visualización:** lista de tarjetas con imagen, título, descripción corta, tipo, ID, enlace y calificación promedio.
- **Búsqueda avanzada:** por ID, título o tipo.
- **Filtro** por tipo (libro, video, artículo) y **ordenamiento** por título (A-Z / Z-A) o tipo.
- **Agregar:** formulario con validación de campos.
- **Modificar:** formulario prellenado con los datos del recurso.
- **Eliminar:** con diálogo de confirmación.
- Los enlaces de los recursos se abren desde la tarjeta.

### Interacción de datos
- **Favoritos:** cada estudiante guarda sus recursos favoritos localmente (por usuario) y los ve en una pantalla dedicada.
- **Calificación:** el estudiante califica de 1 a 5 estrellas. Se recalcula el promedio y se actualiza en el backend (PUT), por lo que todos los usuarios ven el mismo promedio.

### UI / UX
- Interfaz con Material 3 y navegación inferior (Navigation Compose).
- Feedback visual: toasts, diálogos de confirmación, indicadores de carga, animaciones de color en las reglas de contraseña.
- Todos los textos están centralizados en `strings.xml`.

---

## Arquitectura MVC

```
app/src/main/java/com/example/aprendeapp/
├── model/          # Modelos de datos
│   ├── Recurso.kt
│   └── Usuario.kt
├── view/           # Pantallas y componentes (Jetpack Compose)
│   ├── LoginScreen.kt
│   ├── RegisterScreen.kt
│   ├── HomeScreen.kt
│   ├── FavoritosScreen.kt
│   ├── AdminScreen.kt
│   ├── RecursoCard.kt
│   ├── RecursoFormDialog.kt
│   └── AppNavigation.kt
├── controller/     # Lógica de negocio (ViewModels)
│   ├── AuthController.kt
│   └── RecursoController.kt
├── network/        # Capa de red (Retrofit)
│   ├── ApiService.kt
│   └── RetrofitClient.kt
├── utils/          # Utilidades
│   ├── PasswordValidator.kt
│   ├── HashUtils.kt
│   ├── SessionManager.kt
│   └── UserDataManager.kt
└── MainActivity.kt
```

- **Model:** define las entidades `Recurso` y `Usuario`.
- **View:** pantallas declarativas en Compose que solo muestran estado y envían eventos.
- **Controller:** `AuthController` y `RecursoController` contienen la lógica, llaman a la API y exponen el estado a la vista.

---

## Tecnologías

- Kotlin
- Jetpack Compose y Material 3
- Navigation Compose
- Retrofit 2 + Gson + OkHttp Logging Interceptor
- Corrutinas de Kotlin
- Coil (carga de imágenes)
- SharedPreferences (sesión y favoritos)
- MockAPI.io (backend REST)

---

## API (MockAPI.io)

Base URL: `https://6ae2c4a7bbb54129229a5e0a.mockapi.io/`

### Recurso `recursos`

| Campo | Tipo |
|---|---|
| id | String |
| titulo | String |
| descripcion | String |
| tipo | String (`libro`, `video`, `articulo`) |
| enlace | String |
| imagen | String |
| calificacionPromedio | Number |
| totalVotos | Number |

### Recurso `usuarios`

| Campo | Tipo |
|---|---|
| id | String |
| nombre | String |
| email | String |
| password | String (hash SHA-256) |
| rol | String (`estudiante`, `docente`) |

### Endpoints utilizados

| Método | Endpoint | Uso |
|---|---|---|
| GET | `/recursos` | Listar recursos |
| GET | `/recursos/:id` | Obtener un recurso |
| POST | `/recursos` | Crear recurso (docente) |
| PUT | `/recursos/:id` | Editar recurso (docente) y actualizar calificación |
| DELETE | `/recursos/:id` | Eliminar recurso (docente) |
| GET | `/usuarios?email=` | Login y verificación de correo existente |
| POST | `/usuarios` | Registro de usuario |

Ejemplo de JSON:

```json
{
  "id": "1",
  "titulo": "Clean Code",
  "descripcion": "Guía clásica para escribir código limpio, legible y mantenible.",
  "tipo": "libro",
  "enlace": "https://www.oreilly.com/library/view/clean-code-a/9780136083238/",
  "imagen": "https://picsum.photos/seed/1/400/300",
  "calificacionPromedio": 0,
  "totalVotos": 0
}
```

## Cómo ejecutar el proyecto

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/Argueta1939/AprendeApp.git
   ```
2. Abrir la carpeta en **Android Studio**.
3. Esperar a que Gradle sincronice las dependencias.
4. Conectar un dispositivo físico (con depuración USB) o iniciar un emulador.
5. Pulsar **Run**.

**Requisitos:** Android Studio reciente, Android 7.0 (API 24) o superior y conexión a internet.

### Probar la app
1. Registrar un usuario **docente** (por ejemplo, contraseña `Segura#Clave123`) y usar la pestaña **Administrar** para crear, editar y eliminar recursos.
2. Cerrar sesión y registrar un usuario **estudiante** para buscar, filtrar, marcar favoritos y calificar.

---

## Video demostrativo

Enlace al video de la defensa (máx. 15 minutos): _agregar enlace aquí_
