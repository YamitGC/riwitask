# RiwiTask 📋

API REST para la gestión de **usuarios** y **tareas**, construida con **Spring Boot 4** y **PostgreSQL**. Permite crear, listar, actualizar y eliminar usuarios y tareas, además de consultas de seguimiento por usuario, estado y prioridad.

---

## 📑 Tabla de contenidos

- [Arquitectura](#-arquitectura)
- [Tecnologías utilizadas](#-tecnologías-utilizadas)
- [Estructura del proyecto](#-estructura-del-proyecto)
- [Modelo de datos](#-modelo-de-datos)
- [Requisitos previos](#-requisitos-previos)
- [Ejecución paso a paso](#-ejecución-paso-a-paso)
    - [1. Clonar el repositorio](#1-clonar-el-repositorio)
    - [2. Levantar PostgreSQL con Docker](#2-levantar-postgresql-con-docker)
    - [3. Conectar la base de datos con DBeaver](#3-conectar-la-base-de-datos-con-dbeaver)
    - [4. Configurar la aplicación](#4-configurar-la-aplicación)
    - [5. Ejecutar la aplicación](#5-ejecutar-la-aplicación)
    - [6. Probar la API](#6-probar-la-api)
- [Datos de prueba (seed)](#-datos-de-prueba-seed)
- [Endpoints disponibles](#-endpoints-disponibles)
- [Manejo de errores](#-manejo-de-errores)
- [Pruebas](#-pruebas)
- [Documentación con Swagger](#-documentación-con-swagger)
- [Autor](#-autor)

---

## 🏗 Arquitectura

El proyecto sigue una **arquitectura en capas (Layered Architecture)**, típica de aplicaciones Spring Boot, donde cada capa tiene una única responsabilidad y solo se comunica con la capa inmediatamente inferior. Esto favorece el bajo acoplamiento, la testabilidad y la mantenibilidad del código.

```
┌─────────────────────────────────────────────┐
│                  Cliente (HTTP)              │
└───────────────────────┬───────────────────────┘
                         │
┌───────────────────────▼───────────────────────┐
│  CONTROLLER (controller/)                      │
│  Expone los endpoints REST, recibe requests    │
│  y valida los datos de entrada (@Valid).       │
│  Ej: TareaController, UsuarioController        │
└───────────────────────┬───────────────────────┘
                         │  usa DTOs
┌───────────────────────▼───────────────────────┐
│  DTO (dto/)                                    │
│  Objetos de transferencia de datos entre       │
│  cliente y servidor. Desacoplan la entidad     │
│  de lo que realmente se expone en la API.      │
│  Ej: TareaRequestDTO / TareaResponseDTO        │
└───────────────────────┬───────────────────────┘
                         │
┌───────────────────────▼───────────────────────┐
│  SERVICE (service/)                            │
│  Contiene la lógica de negocio: validaciones,  │
│  reglas de dominio, orquestación entre         │
│  repositorios y transformación Entity ↔ DTO.   │
│  Ej: TareaService, UsuarioService              │
└───────────────────────┬───────────────────────┘
                         │
┌───────────────────────▼───────────────────────┐
│  REPOSITORY (repository/)                      │
│  Interfaces que extienden JpaRepository.       │
│  Acceso a datos mediante Spring Data JPA       │
│  (derived queries y consultas personalizadas). │
│  Ej: TareaRepository, UsuarioRepository        │
└───────────────────────┬───────────────────────┘
                         │
┌───────────────────────▼───────────────────────┐
│  ENTITY (entity/)                              │
│  Clases mapeadas a tablas de la base de datos  │
│  mediante JPA/Hibernate (@Entity, @Table).     │
│  Ej: Tarea, Usuario                            │
└───────────────────────┬───────────────────────┘
                         │
┌───────────────────────▼───────────────────────┐
│         PostgreSQL (Docker container)          │
└─────────────────────────────────────────────────┘
```

Capas transversales:

- **`enums/`**: enumeraciones de dominio (`EstadoTarea`, `Prioridad`) usadas por entidades y DTOs.
- **`exception/`**: excepciones personalizadas (`ResourceNotFoundException`, `DupicateEmailException`, `InvalidDataException`) y un `@RestControllerAdvice` (`GlobalExceptionHandler`) que centraliza el manejo de errores devolviendo respuestas HTTP consistentes (`ErrorResponse`).
- **`config/`**: configuración de la aplicación. `DataSeeder` implementa `CommandLineRunner` para poblar la base de datos con usuarios y tareas de ejemplo al iniciar (solo si está vacía).

Este flujo garantiza que:

1. El **Controller** nunca accede directamente al repositorio ni a las entidades.
2. El **Service** nunca conoce detalles HTTP (eso es responsabilidad del Controller).
3. Las **Entities** nunca se exponen directamente al cliente; siempre se traducen a DTOs.

---

## 🛠 Tecnologías utilizadas

| Tecnología | Uso |
|---|---|
| **Java 21** | Lenguaje base |
| **Spring Boot 4.1.1** | Framework principal |
| **Spring Data JPA / Hibernate** | Persistencia y ORM |
| **PostgreSQL** | Base de datos relacional (en Docker) |
| **H2** | Base de datos en memoria para tests |
| **Lombok** | Reducción de código boilerplate (getters/setters/constructores) |
| **springdoc-openapi (Swagger UI)** | Documentación interactiva de la API |
| **Jakarta Bean Validation** | Validación de DTOs (`@NotBlank`, `@NotNull`, `@Email`) |
| **JUnit 5 + Mockito** | Pruebas unitarias |
| **Maven** | Gestión de dependencias y build |
| **Docker** | Contenerización de PostgreSQL |
| **DBeaver** | Cliente visual para explorar la base de datos |

---

## 📂 Estructura del proyecto

```
riwitask/
├── src/
│   ├── main/
│   │   ├── java/com/springboot/riwitask/
│   │   │   ├── config/          # DataSeeder (datos iniciales)
│   │   │   ├── controller/      # Controladores REST
│   │   │   ├── dto/             # Request/Response DTOs
│   │   │   ├── entity/          # Entidades JPA
│   │   │   ├── enums/           # EstadoTarea, Prioridad
│   │   │   ├── exception/       # Excepciones + manejador global
│   │   │   ├── repository/      # Interfaces JpaRepository
│   │   │   ├── service/         # Lógica de negocio
│   │   │   └── RiwitaskApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── schema.sql       # DDL de referencia (usuarios, tareas)
│   └── test/
│       ├── java/...service/     # Tests unitarios con Mockito
│       └── resources/
│           └── application.properties  # Config para tests (H2)
├── pom.xml
└── README.md
```

---

## 🧬 Modelo de datos

**Usuario**

| Campo | Tipo | Restricciones |
|---|---|---|
| id | Long | PK, autogenerado |
| nombre | String | obligatorio |
| email | String | obligatorio, único, formato válido |
| password | String | obligatorio |
| activo | Boolean | por defecto `true` |

**Tarea**

| Campo | Tipo | Restricciones |
|---|---|---|
| id | Long | PK, autogenerado |
| titulo | String | obligatorio |
| descripcion | String | opcional, máx. 1000 caracteres |
| estado | Enum (`EstadoTarea`) | `PENDIENTE`, `EN_PROCESO`, `COMPLETADA` |
| prioridad | Enum (`Prioridad`) | `BAJA`, `MEDIA`, `ALTA` |
| fechaCreacion | LocalDateTime | asignada automáticamente al crear |
| usuario | Usuario (FK) | relación `@ManyToOne`, obligatoria |

Relación: **un Usuario puede tener muchas Tareas** (1:N).

---

## ✅ Requisitos previos

Antes de empezar asegúrate de tener instalado:

- [Java 21 (JDK)](https://adoptium.net/)
- [Maven](https://maven.apache.org/) (o usar el wrapper `./mvnw` si lo agregas al proyecto)
- [Docker y Docker Compose](https://www.docker.com/products/docker-desktop/)
- [DBeaver](https://dbeaver.io/download/) (cliente para visualizar la base de datos)
- [Git](https://git-scm.com/)

---

## 🚀 Ejecución paso a paso

### 1. Clonar el repositorio

```bash
git clone https://github.com/YamitGC/riwitask.git
cd riwitask
```

### 2. Levantar PostgreSQL con Docker

El proyecto se conecta por defecto a `localhost:5433` con las credenciales definidas en `application.properties`. La forma más simple es levantar un contenedor de PostgreSQL con esos mismos valores.

**Opción A — Docker Compose (recomendado)**

Crea un archivo `docker-compose.yml` en la raíz del proyecto:

```yaml
version: "3.8"
services:
  postgres:
    image: postgres:17-alpine
    container_name: riwitask-postgres
    restart: unless-stopped
    environment:
      POSTGRES_DB: riwitask_db
      POSTGRES_USER: riwitask_user
      POSTGRES_PASSWORD: riwitask_password
    ports:
      - "5433:5432"
    volumes:
      - riwitask_pgdata:/var/lib/postgresql/data

volumes:
  riwitask_pgdata:
```

Levanta el contenedor:

```bash
docker compose up -d
```

**Opción B — Docker run directo**

```bash
docker run --name riwitask-postgres \
  -e POSTGRES_DB=riwitask_db \
  -e POSTGRES_USER=riwitask_user \
  -e POSTGRES_PASSWORD=riwitask_password \
  -p 5433:5432 \
  -v riwitask_pgdata:/var/lib/postgresql/data \
  -d postgres:17-alpine
```

Verifica que el contenedor esté corriendo:

```bash
docker ps
```

> 💡 El puerto expuesto es `5433` (no el `5432` por defecto) para evitar conflictos si ya tienes PostgreSQL instalado localmente. Esto coincide con `spring.datasource.url` en `application.properties`.

### 3. Conectar la base de datos con DBeaver

1. Abre **DBeaver** → `Database` → `New Database Connection`.
2. Selecciona **PostgreSQL**.
3. Configura la conexión:
    - **Host**: `localhost`
    - **Port**: `5433`
    - **Database**: `riwitask_db`
    - **Username**: `riwitask_user`
    - **Password**: `riwitask_password`
4. Haz clic en **Test Connection** (DBeaver puede pedirte descargar el driver de PostgreSQL la primera vez, acéptalo).
5. Guarda y conecta. Ahí podrás ver las tablas `usuarios` y `tareas` una vez la aplicación las haya creado (ver siguiente paso).

### 4. Configurar la aplicación

El archivo `src/main/resources/application.properties` ya está configurado para apuntar al contenedor levantado en el paso 2:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/riwitask_db
spring.datasource.username=riwitask_user
spring.datasource.password=riwitask_password
spring.jpa.hibernate.ddl-auto=update
server.port=8080
```

Si cambiaste algún valor en tu `docker-compose.yml` (puerto, usuario, contraseña, nombre de base de datos), actualiza este archivo para que coincida.

> ℹ️ `spring.jpa.hibernate.ddl-auto=update` hace que Hibernate cree/actualice automáticamente las tablas a partir de las entidades. El archivo `schema.sql` se incluye como referencia del DDL equivalente.

### 5. Ejecutar la aplicación

Con Maven instalado localmente:

```bash
mvn clean install
mvn spring-boot:run
```

O bien, empaquetando y ejecutando el `.jar`:

```bash
mvn clean package -DskipTests
java -jar target/riwitask-0.0.1-SNAPSHOT.jar
```

Al iniciar, verás en consola el mensaje del `DataSeeder` confirmando que se cargaron los datos de prueba (solo la primera vez, si la tabla `usuarios` está vacía):

```
>>> [DataSeeder] Datos iniciales cargados con éxito para pruebas.
```

La aplicación quedará disponible en:

```
http://localhost:8080
```

### 6. Probar la API

Puedes usar **Postman**, **Insomnia**, `curl` o la interfaz de **Swagger UI** (ver [sección correspondiente](#-documentación-con-swagger)).

Ejemplo rápido con `curl`:

```bash
curl http://localhost:8080/api/usuarios
curl http://localhost:8080/api/tareas
```

---

## 🌱 Datos de prueba (seed)

Al primer arranque, `DataSeeder` inserta automáticamente:

- **3 usuarios**: Ana Torres, Carlos Mendoza (activos) y Sofía Ramírez (inactiva).
- **5 tareas** asociadas a esos usuarios, con distintos estados (`PENDIENTE`, `EN_PROCESO`, `COMPLETADA`), prioridades y fechas de creación, pensadas para probar las consultas de seguimiento (en especial la de "pendientes recientes").

Si quieres volver a sembrar los datos, elimina el contenido de las tablas (o reinicia el volumen de Docker) y vuelve a ejecutar la aplicación.

---

## 📡 Endpoints disponibles

### Usuarios (`/api/usuarios`)

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/api/usuarios` | Crear un usuario |
| `GET` | `/api/usuarios` | Listar todos los usuarios |
| `GET` | `/api/usuarios/{id}` | Obtener un usuario por id |
| `PUT` | `/api/usuarios/{id}` | Actualizar un usuario |
| `DELETE` | `/api/usuarios/{id}` | Eliminar un usuario |

### Tareas (`/api/tareas`)

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/api/tareas` | Crear una tarea |
| `GET` | `/api/tareas` | Listar todas las tareas |
| `GET` | `/api/tareas/{id}` | Obtener una tarea por id |
| `PUT` | `/api/tareas/{id}` | Actualizar una tarea |
| `DELETE` | `/api/tareas/{id}` | Eliminar una tarea |
| `GET` | `/api/tareas/usuario/{usuarioId}` | Tareas de un usuario específico |
| `GET` | `/api/tareas/estado/{estado}` | Tareas filtradas por estado |
| `GET` | `/api/tareas/prioridad/{prioridad}` | Tareas filtradas por prioridad |
| `GET` | `/api/tareas/usuario/{usuarioId}/estado/{estado}` | Tareas de un usuario filtradas por estado |
| `GET` | `/api/tareas/usuario/{usuarioId}/pendientes-recientes` | **Requerimiento especial**: tareas pendientes de un usuario, ordenadas por fecha de creación descendente |

**Ejemplo de body para crear una tarea (`POST /api/tareas`):**

```json
{
  "titulo": "Implementar autenticación JWT",
  "descripcion": "Preparar seguridad para el siguiente sprint",
  "estado": "PENDIENTE",
  "prioridad": "ALTA",
  "usuarioId": 1
}
```

**Ejemplo de body para crear un usuario (`POST /api/usuarios`):**

```json
{
  "nombre": "Ana Torres",
  "email": "ana.torres@riwitask.com",
  "password": "pass1234",
  "activo": true
}
```

---

## ⚠️ Manejo de errores

El proyecto centraliza el manejo de errores mediante `GlobalExceptionHandler`, devolviendo un cuerpo JSON consistente (`ErrorResponse`) con `timestamp`, `status`, `error` y `message`:

| Excepción | Código HTTP | Caso |
|---|---|---|
| `ResourceNotFoundException` | `404 Not Found` | Usuario o tarea no encontrada |
| `DupicateEmailException` | `409 Conflict` | Email ya registrado |
| `InvalidDataException` | `400 Bad Request` | Datos inválidos |
| `MethodArgumentNotValidException` | `400 Bad Request` | Falla de validación en el DTO (`@Valid`) |

---

## 🧪 Pruebas

El proyecto incluye pruebas unitarias con **JUnit 5** y **Mockito** para la capa de servicios (`TareaServiceTest`, `UsuarioServiceTest`), usando **H2** en memoria para el contexto de Spring Boot (`RiwitaskApplicationTests`).

Ejecutar todas las pruebas:

```bash
mvn test
```

---

## 📖 Documentación con Swagger

Gracias a `springdoc-openapi-starter-webmvc-ui`, la documentación interactiva de la API está disponible una vez la aplicación esté corriendo en:

```
http://localhost:8080/swagger-ui.html
```

Y el contrato OpenAPI en formato JSON:

```
http://localhost:8080/v3/api-docs
```

---

## 👤 Autor

**YamitGC**
Repositorio: [github.com/YamitGC/riwitask](https://github.com/YamitGC/riwitask)