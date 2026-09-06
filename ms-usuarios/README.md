# ms-usuarios

Microservicio de gestión de usuarios y autenticación JWT.

## Puerto
`8081`

## Base de datos
`db_usuarios` (PostgreSQL)

## Funcionalidades
- Registro de usuarios con contraseña temporal (RUT)
- Autenticación con JWT
- Gestión de personas y usuarios
- Actualización de perfil y contraseña

## Configuración

`src/main/resources/application.yml`:
```yaml
server:
  port: 8081
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/db_usuarios
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
| POST | /api/auth/login | Iniciar sesión |
| POST | /api/auth/register | Registrar usuario (solo admin) |
| GET | /api/usuarios | Listar todos los usuarios |
| GET | /api/usuarios/{id} | Obtener usuario por ID |
| PUT | /api/usuarios/{id}/perfil | Actualizar perfil |
| DELETE | /api/usuarios/{id} | Eliminar usuario |

## Ejemplo registro
```json
POST /api/auth/register
{
    "email": "jperez@colegio.cl",
    "rol": "PROFESOR",
    "persona": {
        "nombre": "Juan",
        "apellido": "Pérez",
        "rut": "12345678-9",
        "email": "juan.perez@gmail.com",
        "telefono": "912345678",
        "direccion": "Av. Principal 123"
    }
}
```
> La contraseña temporal será el RUT: `12345678-9`