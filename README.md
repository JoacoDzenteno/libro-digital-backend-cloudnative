# Libro Digital · Backend

Sistema de libro de clases digital en microservicios, base de trabajo para
**DSY1107 Desarrollo Cloud Native I**. El código viene del proyecto de FullStack3
y sobre él se va a montar autenticación con Microsoft Entra ID.

El frontend vive en su propio repositorio: **libro-digital-frontend-cloudnative**.

---

## Antes de partir

Necesitas instalado y funcionando:

| Herramienta | Versión | Cómo comprobar |
|---|---|---|
| Java | **17** (no 11, no 21) | `java -version` |
| Maven | 3.9+ | `mvn -v` |
| Docker Desktop | corriendo | `docker ps` |
| Git | cualquiera | `git --version` |

Si `java -version` te dice otra cosa que 17, el proyecto no va a compilar.

---

## Levantarlo (la ruta corta)

**1. Compilar los ocho proyectos.** Los Dockerfile solo copian el JAR ya
construido, así que este paso es obligatorio antes de `docker compose`.

```bash
mvn clean package -DskipTests -f eureka-server/pom.xml
mvn clean package -DskipTests -f api-gateway/pom.xml
mvn clean package -DskipTests -f ms-usuarios/pom.xml
mvn clean package -DskipTests -f ms-academico/pom.xml
mvn clean package -DskipTests -f ms-matricula/pom.xml
mvn clean package -DskipTests -f ms-libro-digital/pom.xml
mvn clean package -DskipTests -f ms-comunicaciones/pom.xml
mvn clean package -DskipTests -f ms-reportes/pom.xml
```

La primera vez se demora bastante porque descarga las dependencias.

**2. Levantar la infraestructura y los servicios.**

```bash
docker compose up -d postgres rabbitmq eureka-server api-gateway \
  ms-usuarios ms-academico ms-matricula ms-libro-digital \
  ms-comunicaciones ms-reportes
```

> El `docker-compose.yml` también trae SonarQube con su propia base de datos.
> No hace falta para desarrollar y se come cerca de 2 GB de RAM, por eso el
> comando de arriba nombra solo lo necesario. Si quieres todo: `docker compose up -d --build`.

**3. Comprobar que quedó arriba.**

- Eureka: http://localhost:8761 — deben aparecer 7 servicios en estado **UP**
- Gateway: http://localhost:8080
- RabbitMQ: http://localhost:15672 (`guest` / `guest`)

Dale un minuto: los microservicios tardan en registrarse en Eureka.

**4. Crear los datos de prueba.** Las bases arrancan **vacías**. Sin este paso no
hay ningún usuario y no vas a poder iniciar sesión.

```bash
./scripts/seed-usuarios.sh
```

En Windows, ejecútalo desde **Git Bash**. Si prefieres PowerShell, el equivalente
para un usuario es:

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/auth/register `
  -ContentType 'application/json' `
  -Body '{"email":"admin@colegio.cl","rol":"ADMINISTRATIVO","persona":{"nombre":"Admin","apellido":"Colegio","rut":"11111111-1","email":"admin@colegio.cl"}}'
```

### Usuarios que deja el script

| Email | Contraseña | Rol |
|---|---|---|
| admin@colegio.cl | `11111111-1` | ADMINISTRATIVO |
| profesor@colegio.cl | `22222222-2` | PROFESOR |
| estudiante@colegio.cl | `33333333-3` | ESTUDIANTE |
| apoderado@colegio.cl | `44444444-4` | APODERADO |

La contraseña es el RUT porque así está hecho hoy el registro. Eso va a cambiar
cuando entre Entra ID: la contraseña dejará de vivir en esta base de datos.

---

## Arquitectura

```
Frontend (Vite, :5173)
        |
   API Gateway (:8080)  -- única puerta de entrada, valida el token
        |
   Eureka (:8761)  <- todos los servicios se registran acá
        |
   ms-usuarios (:8081)   ms-academico (:8082)   ms-matricula (:8083)
   ms-libro-digital (:8084)   ms-comunicaciones (:8085)   ms-reportes (:8086)

PostgreSQL (:5432)   RabbitMQ (:5672, panel :15672)
```

| Servicio | Puerto | Base de datos | Qué hace |
|---|---|---|---|
| eureka-server | 8761 | — | Descubrimiento de servicios |
| api-gateway | 8080 | — | Puerta de entrada y validación del token |
| ms-usuarios | 8081 | db_usuarios | Usuarios y autenticación |
| ms-academico | 8082 | db_academico | Cursos, asignaturas, carga horaria |
| ms-matricula | 8083 | db_matricula | Matrículas |
| ms-libro-digital | 8084 | db_libro_digital | Asistencia, notas, hoja de vida |
| ms-comunicaciones | 8085 | db_comunicaciones | Mensajería |
| ms-reportes | 8086 | db_reportes | Reportes |

Rutas del gateway: `/api/auth/**` y `/api/usuarios/**` → ms-usuarios ·
`/api/academico/**` → ms-academico · `/api/matricula/**` → ms-matricula ·
`/api/libro/**` → ms-libro-digital · `/api/comunicaciones/**` → ms-comunicaciones ·
`/api/reportes/**` → ms-reportes

Cada microservicio publica su Swagger en `http://localhost:{puerto}/swagger-ui/index.html`.

Stack: Java 17 · Spring Boot 3.5.13 · Spring Cloud 2025.0.2 · PostgreSQL 17 ·
RabbitMQ · JaCoCo · Docker.

---

## Cómo trabajamos

Una rama por persona, para no pisarnos:

| Rama | Quién | Qué toca |
|---|---|---|
| `feature/identidad-msal` | A | Entra ID y sesión en el frontend |
| `feature/bff-security` | B | Spring Security en el api-gateway |
| `feature/cloud-infra` | C | AWS, interceptor y entrega |

```bash
git checkout main
git pull
git checkout feature/<la-tuya>
```

Sube tu rama apenas la crees para que el resto vea en qué andas.

**No subas al repositorio** los identificadores del tenant de Entra ID: van por
el canal del grupo. Y revisa antes de cada push que no se cuele nada compilado:

```bash
git ls-files | grep -E 'target/|node_modules'   # no debe devolver nada
```

---

## Levantarlo sin Docker

Si prefieres correr los servicios a mano necesitas PostgreSQL 17 en
`localhost:5432` con usuario y clave `postgres`, RabbitMQ en `localhost:5672`
con `guest`/`guest`, y las seis bases creadas:

```sql
CREATE DATABASE db_usuarios;
CREATE DATABASE db_academico;
CREATE DATABASE db_matricula;
CREATE DATABASE db_libro_digital;
CREATE DATABASE db_comunicaciones;
CREATE DATABASE db_reportes;
```

Después, **Eureka primero** y el resto en cualquier orden:

```bash
cd eureka-server && mvn spring-boot:run
cd api-gateway   && mvn spring-boot:run
cd ms-usuarios   && mvn spring-boot:run    # y los otros cinco
```

Si un servicio arranca antes que Eureka no se registra y hay que reiniciarlo.

---

## Pruebas y cobertura

```bash
cd ms-usuarios && mvn clean test
```

El reporte queda en `target/site/jacoco/index.html`. Hay pruebas unitarias
(JUnit 5 + Mockito) sobre la capa `service` en los seis microservicios.

---

## Si algo no levanta

| Síntoma | Causa probable | Qué hacer |
|---|---|---|
| Un servicio no aparece en Eureka | Arrancó antes que Eureka | `docker compose restart <servicio>` |
| No puedo iniciar sesión | Las bases están vacías | Correr `./scripts/seed-usuarios.sh` |
| `docker compose` no toma mis cambios | Los Dockerfile copian el JAR ya construido | `mvn clean package -DskipTests` y después `--build` |
| CORS bloqueado en el navegador | El front no está en `localhost:5173` | Revisar `CorsConfig` del api-gateway |
| `Connection refused` a Postgres | El servicio partió antes que la base | `docker compose restart <servicio>` |
| Puerto ocupado | Otro proceso usa 5432 u 8080 | `docker compose down` y revisar qué lo tiene tomado |
