-- Mapeo entre las cuentas del tenant de Entra ID y los usuarios internos.
-- El vinculo es azure_oid, no el email: por eso este archivo es lo que permite
-- que /api/usuarios/me devuelva el id numerico que usa el resto del sistema.
--
-- ON CONFLICT hace que se pueda ejecutar en cada arranque sin reventar.

INSERT INTO persona (id, nombre, apellido, rut, email) VALUES
  (1, 'Admin',    'Colegio',  '12345678-9', 'admin@librodigitalcloudnative.onmicrosoft.com'),
  (2, 'Javiera',  'Perez',    '98765432-1', 'profesor@librodigitalcloudnative.onmicrosoft.com'),
  (3, 'Matias',   'Rojas',    '11223344-5', 'estudiante@librodigitalcloudnative.onmicrosoft.com'),
  (4, 'Carolina', 'Soto',     '55667788-9', 'apoderado@librodigitalcloudnative.onmicrosoft.com')
ON CONFLICT (id) DO NOTHING;

INSERT INTO usuario (id, azure_oid, email, password, rol, persona_id) VALUES
  (1, '25d1c515-c216-4499-a3ee-1e9031ecd2fd', 'admin@librodigitalcloudnative.onmicrosoft.com',      'sin-uso', 'ADMINISTRATIVO', 1),
  (2, '9f3c4e23-a5ce-4bd9-9216-5e58bf8f1fa0', 'profesor@librodigitalcloudnative.onmicrosoft.com',   'sin-uso', 'PROFESOR',       2),
  (3, 'c7b0f9d8-2d5e-48fa-a8ba-e7ac3c955bbc', 'estudiante@librodigitalcloudnative.onmicrosoft.com', 'sin-uso', 'ESTUDIANTE',     3),
  (4, 'fc9b71a4-6c93-4e72-b045-7bb5fac400ea', 'apoderado@librodigitalcloudnative.onmicrosoft.com',  'sin-uso', 'APODERADO',      4)
ON CONFLICT (id) DO NOTHING;

-- Al insertar ids a mano, la secuencia de Postgres no avanza y el proximo
-- usuario creado por la aplicacion chocaria. Esto la deja al dia.
SELECT setval(pg_get_serial_sequence('persona', 'id'), COALESCE((SELECT MAX(id) FROM persona), 1));
SELECT setval(pg_get_serial_sequence('usuario', 'id'), COALESCE((SELECT MAX(id) FROM usuario), 1));