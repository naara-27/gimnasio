# Gimnasio - Sistema de Gestión v1.0

Sistema web de gestión para gimnasios desarrollado con **Java Spring Boot**, **Spring Security**, **Thymeleaf** y **JPA/H2**. Permite administrar clientes, clases, reservaciones y procesos de recuperación de contraseña con autenticación y autorización basada en roles.

---

## 📋 Introducción

Este proyecto es una aplicación web para la gestión integral de un gimnasio. Incluye módulos de clientes, clases, reservaciones, autenticación de usuarios y recuperación de contraseña. Está construido con tecnologías Java modernas y sigue una arquitectura basada en capas (Controlador, Servicio, Repositorio, Modelo).

**Versión actual:** 1.0

---

## 🛠️ Tecnologías utilizadas

- **Java 27**
- **Spring Boot 4.1.2**
- **Spring Security** (autenticación y autorización por roles)
- **Spring Data JPA** (persistencia con base de datos H2)
- **Thymeleaf** (motor de plantillas HTML)
- **Bootstrap 5.3.3** (estilos y componentes visuales)
- **Maven** (gestión de dependencias y construcción)
- **H2 Database** (base de datos embebida en desarrollo)

---

## 📁 Estructura del proyecto

```
src/main/java/com/proyecto/gimnasio/
├── config/             # Configuración de inicialización de datos
├── controller/         # Controladores MVC (peticiones HTTP)
├── model/              # Entidades JPA (Cliente, ClaseGimnasio, Reservacion)
├── repository/         # Interfaces de acceso a datos (JpaRepository)
├── security/           # Configuración de seguridad y detalles de usuario
└── service/            # Lógica de negocio y reglas de validación
```

```text
src/main/resources/templates/
├── clases/             # Vistas de gestión de clases
├── clientes/           # Vistas de clientes (lista, detalle, formulario, perfil)
├── login.html          # Página de inicio de sesión
├── recuperar-password.html   # Solicitud de recuperación de contraseña
├── reset-password.html       # Restablecimiento de contraseña
└── reservaciones/      # Vistas de reservaciones
```

---

## 🚀 Módulos principales

### 1. Gestión de Clientes
- Listado, creación, edición y eliminación de clientes (solo ADMIN).
- Consulta de detalle de cliente.
- Perfil propio autenticado (`/perfil`) donde el cliente puede editar sus datos personales (nombre, cédula, teléfono, correo y contraseña).
- Validaciones de negocio: cédula y correo únicos, rol restringido.

### 2. Autenticación y Seguridad
- Login con correo y contraseña (`/login`).
- Roles soportados: `ADMIN` y `CLIENTE`.
- Protección de rutas mediante Spring Security.
- CSRF habilitado (excepto consola H2).
- Cierre de sesión (`/logout`).

### 3. Recuperación de Contraseña
- Flujo público (`/recuperar-password`): el usuario ingresa su correo.
- Validación de existencia del correo en la base de datos.
- Si existe, se redirige al formulario de reestablecimiento (`/reset-password`).
- El cliente ingresa nueva contraseña y confirmación, se valida coincidencia y se guarda encriptada con **BCrypt**.
- Al finalizar, redirección al login con mensaje de éxito.

### 4. Gestión de Clases y Reservaciones
- CRUD de clases de gimnasio.
- Creación y consulta de reservaciones.
- Cancelación de reservaciones (ADMIN y CLIENTE).
- Mis reservaciones (solo CLIENTE autenticado).

---

## 🔐 Seguridad y Control de Acceso

| Ruta | Rol requerido | Descripción |
|------|--------------|-------------|
| `/login` | Público | Inicio de sesión |
| `/recuperar-password`, `/reset-password` | Público | Recuperación de contraseña |
| `/perfil`, `/clientes/perfil` | CLIENTE, ADMIN | Perfil del usuario autenticado |
| `/clientes` | Autenticado | Listado de clientes |
| `/clientes/{id}` | Autenticado (dueño o ADMIN) | Detalle de cliente |
| `/clientes/nuevo`, `/clientes/*/editar`, etc. | ADMIN | Gestión de clientes |
| `/reservaciones/nueva`, `/reservaciones/mis-reservaciones` | CLIENTE | Reservaciones del cliente |
| `/reservaciones`, `/reservaciones/*/cancelar` | ADMIN, CLIENTE | Gestión de reservaciones |

- Cada cliente solo puede consultar y modificar **su propio perfil**. Cualquier intento de manipular el ID de otro cliente mediante parámetros HTTP es rechazado.
- El administrador tiene acceso total a la gestión de clientes y reservaciones.

---

## ▶️ Cómo ejecutar el proyecto

### Requisitos previos
- JDK 27 instalado
- Maven (o usar `./mvnw` incluido)

### Pasos
1. Clonar el repositorio.
2. Ejecutar la aplicación:
   ```bash
   ./mvnw spring-boot:run
   ```
3. Acceder en el navegador: `http://localhost:8080`

### Credenciales de prueba (cargadas automáticamente)
- **Administrador:** `admin@gimnasio.com` / `admin123` (rol ADMIN)
- **Cliente:** `maria@gimnasio.com` / `maria123` (rol CLIENTE)

---

## 🧪 Pruebas

El proyecto incluye pruebas unitarias y de integración ubicadas en `src/test/java`. Se ejecutan con:

```bash
./mvnw test
```

Entre las pruebas destacadas:
- Restricción de ownership en el perfil del cliente (no puede editar perfiles ajenos).
- Acceso del administrador a cualquier perfil.

---

## 📦 Versiones

| Versión | Fecha | Descripción |
|---------|-------|-------------|
| 1.0 | Noviembre 2026 | Versión inicial del sistema de gestión de gimnasio |

---

## 🔜 Coming soon

Esta es la versión **1.0** del sistema de gestión de gimnasio. En futuras actualizaciones se planea incluir:

- Módulo de pagos y membresías.
- Historial de asistencia de clientes.
- Notificaciones por correo electrónico.
- Exportación de reportes a PDF/Excel.
- Interfaz responsive mejorada para dispositivos móviles.
- API REST para integración con aplicaciones móviles.

---

Desarrollado con Spring Boot + Thymeleaf 💙