-- 1. Usuario ADMINISTRATIVO
INSERT INTO persona (id, nombre, apellido, rut, email) 
VALUES (1, 'Admin', 'Colegio', '12345678-9', 'admin@librodigitalcloudnative.onmicrosoft.com');

INSERT INTO usuario (id, azure_oid, email, password, rol, persona_id) 
VALUES (1, '25d1c515-c216-4499-a3ee-1e9031ecd2fd', 'admin@librodigitalcloudnative.onmicrosoft.com', '123456', 'ADMINISTRATIVO', 1);

-- 2. Usuario PROFESOR
INSERT INTO persona (id, nombre, apellido, rut, email) 
VALUES (2, 'Profesor', 'Gonzalez', '98765432-1', 'profe@librodigitalcloudnative.onmicrosoft.com');

INSERT INTO usuario (id, azure_oid, email, password, rol, persona_id) 
VALUES (2, '9f3c4e23-a5ce-4bd9-9216-5e58bf8f1fa0', 'profe@librodigitalcloudnative.onmicrosoft.com', '123456', 'PROFESOR', 2);

-- 3. Usuario ESTUDIANTE
INSERT INTO persona (id, nombre, apellido, rut, email) 
VALUES (3, 'Juan', 'Perez', '11223344-5', 'alumno@librodigitalcloudnative.onmicrosoft.com');

INSERT INTO usuario (id, azure_oid, email, password, rol, persona_id) 
VALUES (3, 'c7b0f9d8-2d5e-48fa-a8ba-e7ac3c955bbc', 'alumno@librodigitalcloudnative.onmicrosoft.com', '123456', 'ESTUDIANTE', 3);

-- 4. Usuario APODERADO
INSERT INTO persona (id, nombre, apellido, rut, email) 
VALUES (4, 'Maria', 'Perez', '55667788-9', 'apoderado@librodigitalcloudnative.onmicrosoft.com');

INSERT INTO usuario (id, azure_oid, email, password, rol, persona_id) 
VALUES (4, 'fc9b71a4-6c93-4e72-b045-7bb5fac400ea', 'apoderado@librodigitalcloudnative.onmicrosoft.com', '123456', 'APODERADO', 4);