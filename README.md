# Libro Digital Backend - Colegio Bernardo O'Higgins

Backend del sistema de libro de clases digital basado en arquitectura de microservicios.

## Tecnologías
- Java 17
- Spring Boot 3.5.13
- Spring Cloud 2025.0.2
- PostgreSQL 17
- RabbitMQ
- Maven
- Docker

## Microservicios

| Servicio          |Puerto| Descripción                               |
|-------------------|------|-------------------------------------------|
| eureka-server     | 8761 | Service Discovery                         |
| api-gateway       | 8080 | API Gateway + JWT                         |
| ms-usuarios       | 8081 | Gestión de usuarios y autenticación       |
| ms-academico      | 8082 | Cursos, asignaturas y carga horaria       |
| ms-matricula      | 8083 | Matrículas de estudiantes                 |
| ms-libro-digital  | 8084 | Asistencia, calificaciones y hoja de vida |
| ms-comunicaciones | 8085 | Mensajería entre actores                  |
| ms-reportes       | 8086 | Generación de reportes                    |

## Opción 1: Levantar con Docker (recomendado)

Requiere tener Docker Desktop instalado y corriendo.

```bash
mvn clean package -DskipTests   # en cada microservicio, para generar los JAR
docker compose up -d --build
```

Esto levanta automáticamente Postgres (con las 6 bases de datos ya creadas), RabbitMQ, Eureka, el Gateway y los 6 microservicios, todos conectados entre sí.

Verificación:
- Eureka: `http://localhost:8761` (deben aparecer los 7 servicios en estado UP)
- Gateway: `http://localhost:8080`
- RabbitMQ: `http://localhost:15672` (usuario/clave: `guest`/`guest`)

## Opción 2: Levantar manualmente (sin Docker)

### Requisitos previos
- Java 17 instalado
- Maven 3.9+ instalado
- PostgreSQL 17 instalado y corriendo
- RabbitMQ instalado y corriendo (puerto 5672), requerido por ms-libro-digital, ms-comunicaciones y ms-reportes
- Bases de datos creadas (ver sección de configuración)

### Bases de datos requeridas

Ejecutar en PostgreSQL:

```sql
CREATE DATABASE db_usuarios;
CREATE DATABASE db_academico;
CREATE DATABASE db_matricula;
CREATE DATABASE db_libro_digital;
CREATE DATABASE db_comunicaciones;
CREATE DATABASE db_reportes;
```

### Orden de ejecución

Levantar los servicios en este orden:

**1. Eureka Server:**
```bash
cd eureka-server
mvn spring-boot:run
```

**2. API Gateway:**
```bash
cd api-gateway
mvn spring-boot:run
```

**3. Microservicios (en cualquier orden):**
```bash
cd ms-usuarios && mvn spring-boot:run
cd ms-academico && mvn spring-boot:run
cd ms-matricula && mvn spring-boot:run
cd ms-libro-digital && mvn spring-boot:run
cd ms-comunicaciones && mvn spring-boot:run
cd ms-reportes && mvn spring-boot:run
```

## Probar la API

Cada microservicio expone su documentación Swagger en:
http://localhost:{puerto}/swagger-ui/index.html

Para probar el flujo completo de autenticación, pasando por el Gateway:
POST http://localhost:8080/api/auth/login

{

"email": "tu_email",

"password": "tu_password"

}

## Ejecutar pruebas unitarias y generar reporte de cobertura

Cada microservicio incluye pruebas unitarias (JUnit 5 + Mockito) sobre la capa `service`, con cobertura medida por JaCoCo.

Para correr los tests y generar el reporte, parado en la carpeta del microservicio:

```bash
mvn clean test
```

El reporte de cobertura HTML se genera automáticamente en:
target/site/jacoco/index.html

Ábrelo en cualquier navegador para ver el detalle de cobertura por clase y método. Los reportes ya generados también están disponibles en el repositorio de documentación, dentro de `Documentacion/CoberturaJaCoCo/{microservicio}/index.html`.
