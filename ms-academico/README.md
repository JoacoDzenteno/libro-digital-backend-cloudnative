# ms-academico

Microservicio de gestión académica: cursos, asignaturas y carga horaria.

## Puerto
`8082`

## Base de datos
`db_academico` (PostgreSQL)

## Configuración

`src/main/resources/application.yml`:
```yaml
server:
  port: 8082
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/db_academico
    username: postgres
    password: postgres
```

## Ejecución
```bash
mvn clean install -DskipTests
mvn spring-boot:run
```

## Endpoints

### Cursos
| Método | URL | Descripción |
|---|---|---|
| GET | /api/academico/cursos | Listar cursos |
| GET | /api/academico/cursos/{id} | Obtener curso |
| GET | /api/academico/cursos/nivel/{nivel} | Cursos por nivel |
| POST | /api/academico/cursos | Crear curso |
| PUT | /api/academico/cursos/{id} | Actualizar curso |
| DELETE | /api/academico/cursos/{id} | Eliminar curso |

### Asignaturas
| Método | URL | Descripción |
|---|---|---|
| GET | /api/academico/asignaturas | Listar asignaturas |
| POST | /api/academico/asignaturas | Crear asignatura |
| PUT | /api/academico/asignaturas/{id} | Actualizar asignatura |
| DELETE | /api/academico/asignaturas/{id} | Eliminar asignatura |

### Carga Horaria
| Método | URL | Descripción |
|---|---|---|
| GET | /api/academico/cargas | Listar cargas |
| GET | /api/academico/cargas/curso/{id} | Cargas por curso |
| GET | /api/academico/cargas/profesor/{id} | Cargas por profesor |
| POST | /api/academico/cargas | Crear carga horaria |
| DELETE | /api/academico/cargas/{id} | Eliminar carga |