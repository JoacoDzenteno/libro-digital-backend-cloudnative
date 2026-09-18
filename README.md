# Libro Digital · Backend

Sistema de libro de clases digital en microservicios, base de trabajo para
**DSY1107 Desarrollo Cloud Native I**. El código viene del proyecto de FullStack3
y sobre él se montó autenticación con Microsoft Entra ID.

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

## Autenticación con Microsoft Entra ID

El login local (email + contraseña guardada en la base) fue reemplazado por completo.
Ahora la identidad la maneja Microsoft Entra ID:

- El **frontend** usa MSAL para redirigir al login de Microsoft y obtener un token JWT.
- El **api-gateway** valida ese token contra el tenant de Entra ID (issuer, firma,
  audiencia) y convierte el claim `roles` del token en authorities de Spring Security.
- Cada ruta del gateway exige el rol correspondiente (`ADMINISTRATIVO`, `PROFESOR`,
  `ESTUDIANTE`, `APODERADO`); sin token responde `401`, con rol insuficiente responde
  `403`, ambos con cuerpo JSON.
- `ms-usuarios` expone `GET /api/usuarios/me`, que lee el claim `oid` del token y
  devuelve el usuario interno asociado (columna `azure_oid`).

Los usuarios de prueba del tenant (mismo dominio para los cuatro):

| Email | Rol |
|---|---|
| admin@librodigitalcloudnative.onmicrosoft.com | ADMINISTRATIVO |
| profesor@librodigitalcloudnative.onmicrosoft.com | PROFESOR |
| estudiante@librodigitalcloudnative.onmicrosoft.com | ESTUDIANTE |
| apoderado@librodigitalcloudnative.onmicrosoft.com | APODERADO |

Las credenciales del tenant (TENANT_ID, API_CLIENT_ID, issuer-uri, etc.) se
comparten por el canal del grupo, nunca en el repositorio.

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
hay ningún usuario asociado a los perfiles de Entra ID.

```bash
./scripts/seed-usuarios.sh
```

En Windows, ejecútalo desde **Git Bash**.

---

## Arquitectura