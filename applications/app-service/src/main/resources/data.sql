INSERT INTO id_type (name, description) VALUES
('Cédula de Ciudadanía', 'Documento de identificación para ciudadanos colombianos mayores de edad'),
('Cédula de Extranjería', 'Documento de identificación para extranjeros residentes en Colombia'),
('Pasaporte', 'Documento de identificación internacional para viajes y trámites'),
('Tarjeta de Identidad', 'Documento de identificación para menores de edad'),
('NIT', 'Número de Identificación Tributaria para personas jurídicas'),
('Registro Civil', 'Documento de identificación para menores de 7 años'),
('Permiso Especial de Permanencia', 'Documento temporal para migrantes venezolanos'),
('Documento Nacional de Identidad', 'Documento de identificación de otros países');

INSERT INTO roles (name, description) VALUES
('ADMIN', 'Administrador del sistema con acceso completo a todas las funcionalidades'),
('ASESOR', 'Asesor comercial con permisos para gestionar clientes y realizar ventas'),
('CLIENTE', 'Cliente del sistema con acceso limitado a sus propios datos y servicios');

INSERT INTO users (
  id_number, id_type_id, name, lastname, birth_date,
  address, phone, email, base_salary, role_id, password
) VALUES (
  10000001,
  1,
  'Administrador',
  'General',
  DATE '1990-01-01',
  'Calle 123',
  '3001234567',
  'admin@crediya.com',
  0.00,
  1,
  '$2a$12$crDkSPkIEE3T.Ld5.X39feQP38INybGFQLvw/MqyMegqlyBL9hr3.'
);