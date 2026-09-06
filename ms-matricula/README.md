# ms-matricula

Microservicio de gestión de matrículas. Se comunica con ms-usuarios via WebClient para validar la existencia del estudiante y apoderado antes de matricular.

## Puerto
`8083`

## Base de datos
`db_matricula` (PostgreSQL)

## Comunicación
- **WebClient → ms-usuarios**: verifica que el estudiante y apoderado existen antes de crear la matrícula.

## Configuración

`src/main/resources/application.yml`:
```yaml
server:
  port: 8083
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/db_matricula
    username: postgres
    password: postgres
```

## Ejecución
```bash
mvn clean install -DskipTests
mvn spring-boot:run
```

## Endpoints

| Método | URL | Descripción |
|---|---|---|
| GET | /api/matricula | Listar todas las matrículas |
| GET | /api/matricula/{id} | Obtener matrícula por ID |
| GET | /api/matricula/estudiante/{id} | Matrículas por estudiante |
| GET | /api/matricula/apoderado/{id} | Matrículas por apoderado |
| GET | /api/matricula/curso/{id} | Matrículas por curso |
| POST | /api/matricula | Crear matrícula |
| PATCH | /api/matricula/{id}/estado | Cambiar estado |
| DELETE | /api/matricula/{id} | Eliminar matrícula |

## Ejemplo crear matrícula
```json
POST /api/matricula
{
    "idEstudiante": 4,
    "idApoderado": 5,
    "idCurso": 1,
    "anioEscolar": 2026
}
```
> Retorna 409 si el estudiante o apoderado no existen en ms-usuarios.