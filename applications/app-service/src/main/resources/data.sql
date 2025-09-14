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

INSERT INTO users (
  id_number, id_type_id, name, lastname, birth_date,
  address, phone, email, base_salary, role_id, password
) VALUES 
-- Usuarios que coinciden con las aplicaciones de crédito
(1234567890, 1, 'Carlos', 'Rodríguez', DATE '1985-03-15', 'Carrera 10 #25-30', '3101234567', 'carlos.rodriguez@email.com', 3500000.00, 3, '$2a$12$crDkSPkIEE3T.Ld5.X39feQP38INybGFQLvw/MqyMegqlyBL9hr3.'),
(9876543210, 1, 'María', 'González', DATE '1990-07-22', 'Calle 45 #12-67', '3209876543', 'maria.gonzalez@email.com', 2800000.00, 3, '$2a$12$crDkSPkIEE3T.Ld5.X39feQP38INybGFQLvw/MqyMegqlyBL9hr3.'),
(5555666677, 1, 'Luis', 'Martínez', DATE '1982-11-08', 'Avenida 68 #45-12', '3155556666', 'luis.martinez@email.com', 8500000.00, 3, '$2a$12$crDkSPkIEE3T.Ld5.X39feQP38INybGFQLvw/MqyMegqlyBL9hr3.'),
(1111222233, 1, 'Ana', 'López', DATE '1988-05-14', 'Transversal 15 #78-90', '3111112222', 'ana.lopez@email.com', 4200000.00, 3, '$2a$12$crDkSPkIEE3T.Ld5.X39feQP38INybGFQLvw/MqyMegqlyBL9hr3.'),
(7777888899, 1, 'Roberto', 'Herrera', DATE '1975-12-03', 'Diagonal 25 #34-56', '3177778888', 'roberto.herrera@email.com', 15000000.00, 3, '$2a$12$crDkSPkIEE3T.Ld5.X39feQP38INybGFQLvw/MqyMegqlyBL9hr3.'),
(3333444455, 1, 'Patricia', 'Jiménez', DATE '1992-09-18', 'Calle 80 #23-45', '3133334444', 'patricia.jimenez@email.com', 2100000.00, 3, '$2a$12$crDkSPkIEE3T.Ld5.X39feQP38INybGFQLvw/MqyMegqlyBL9hr3.'),
(6666777788, 1, 'Diego', 'Ramírez', DATE '1987-02-28', 'Carrera 50 #67-89', '3166667777', 'diego.ramirez@email.com', 3800000.00, 3, '$2a$12$crDkSPkIEE3T.Ld5.X39feQP38INybGFQLvw/MqyMegqlyBL9hr3.'),
(2222333344, 1, 'Sandra', 'Torres', DATE '1983-06-12', 'Avenida 30 #12-34', '3122223333', 'sandra.torres@email.com', 12000000.00, 3, '$2a$12$crDkSPkIEE3T.Ld5.X39feQP38INybGFQLvw/MqyMegqlyBL9hr3.'),
(8888999900, 1, 'Fernando', 'Vargas', DATE '1989-10-25', 'Calle 120 #45-67', '3188889999', 'fernando.vargas@email.com', 7200000.00, 3, '$2a$12$crDkSPkIEE3T.Ld5.X39feQP38INybGFQLvw/MqyMegqlyBL9hr3.'),
(4444555566, 1, 'Claudia', 'Morales', DATE '1978-04-07', 'Transversal 40 #78-90', '3144445555', 'claudia.morales@email.com', 25000000.00, 3, '$2a$12$crDkSPkIEE3T.Ld5.X39feQP38INybGFQLvw/MqyMegqlyBL9hr3.'),
(1357924680, 1, 'Andrés', 'Castro', DATE '1991-08-16', 'Diagonal 70 #23-45', '3113579246', 'andres.castro@email.com', 2500000.00, 3, '$2a$12$crDkSPkIEE3T.Ld5.X39feQP38INybGFQLvw/MqyMegqlyBL9hr3.'),
(2468135790, 1, 'Mónica', 'Ruiz', DATE '1994-01-30', 'Carrera 85 #56-78', '3124681357', 'monica.ruiz@email.com', 2200000.00, 3, '$2a$12$crDkSPkIEE3T.Ld5.X39feQP38INybGFQLvw/MqyMegqlyBL9hr3.'),
(9753186420, 1, 'Javier', 'Mendoza', DATE '1980-11-21', 'Avenida 15 #89-12', '3197531864', 'javier.mendoza@email.com', 18500000.00, 3, '$2a$12$crDkSPkIEE3T.Ld5.X39feQP38INybGFQLvw/MqyMegqlyBL9hr3.'),
(8642097531, 1, 'Liliana', 'Peña', DATE '1986-07-09', 'Calle 95 #34-56', '3186420975', 'liliana.pena@email.com', 6000000.00, 3, '$2a$12$crDkSPkIEE3T.Ld5.X39feQP38INybGFQLvw/MqyMegqlyBL9hr3.'),
(7531086429, 1, 'Ricardo', 'Salazar', DATE '1973-12-14', 'Transversal 60 #12-34', '3175310864', 'ricardo.salazar@email.com', 32000000.00, 3, '$2a$12$crDkSPkIEE3T.Ld5.X39feQP38INybGFQLvw/MqyMegqlyBL9hr3.'),
(1122334455, 1, 'Esperanza', 'Ortiz', DATE '1993-03-27', 'Diagonal 35 #67-89', '3111223344', 'esperanza.ortiz@email.com', 3200000.00, 3, '$2a$12$crDkSPkIEE3T.Ld5.X39feQP38INybGFQLvw/MqyMegqlyBL9hr3.'),
(5566778899, 1, 'Gustavo', 'Restrepo', DATE '1989-06-05', 'Carrera 25 #45-67', '3155667788', 'gustavo.restrepo@email.com', 2800000.00, 3, '$2a$12$crDkSPkIEE3T.Ld5.X39feQP38INybGFQLvw/MqyMegqlyBL9hr3.'),
(9900112233, 1, 'Beatriz', 'Aguilar', DATE '1981-09-13', 'Avenida 90 #23-45', '3199001122', 'beatriz.aguilar@email.com', 22000000.00, 3, '$2a$12$crDkSPkIEE3T.Ld5.X39feQP38INybGFQLvw/MqyMegqlyBL9hr3.'),
(3344556677, 1, 'Mauricio', 'Vega', DATE '1984-02-19', 'Calle 55 #78-90', '3133445566', 'mauricio.vega@email.com', 8500000.00, 3, '$2a$12$crDkSPkIEE3T.Ld5.X39feQP38INybGFQLvw/MqyMegqlyBL9hr3.'),
(7788990011, 1, 'Carmen', 'Delgado', DATE '1976-05-08', 'Transversal 80 #12-34', '3177889900', 'carmen.delgado@email.com', 28500000.00, 3, '$2a$12$crDkSPkIEE3T.Ld5.X39feQP38INybGFQLvw/MqyMegqlyBL9hr3.'),
(1010101010, 1, 'Alejandro', 'Navarro', DATE '1990-12-01', 'Diagonal 45 #56-78', '3110101010', 'alejandro.navarro@email.com', 4200000.00, 3, '$2a$12$crDkSPkIEE3T.Ld5.X39feQP38INybGFQLvw/MqyMegqlyBL9hr3.'),
(2020202020, 1, 'Valeria', 'Campos', DATE '1995-04-17', 'Carrera 75 #89-12', '3120202020', 'valeria.campos@email.com', 1900000.00, 3, '$2a$12$crDkSPkIEE3T.Ld5.X39feQP38INybGFQLvw/MqyMegqlyBL9hr3.'),
(3030303030, 1, 'Sergio', 'Paredes', DATE '1979-08-24', 'Avenida 55 #34-56', '3130303030', 'sergio.paredes@email.com', 16800000.00, 3, '$2a$12$crDkSPkIEE3T.Ld5.X39feQP38INybGFQLvw/MqyMegqlyBL9hr3.'),
(4040404040, 1, 'Natalia', 'Quintero', DATE '1987-01-11', 'Calle 65 #12-34', '3140404040', 'natalia.quintero@email.com', 10500000.00, 3, '$2a$12$crDkSPkIEE3T.Ld5.X39feQP38INybGFQLvw/MqyMegqlyBL9hr3.'),
(5050505050, 1, 'Rodrigo', 'Escobar', DATE '1972-10-06', 'Transversal 90 #67-89', '3150505050', 'rodrigo.escobar@email.com', 45000000.00, 3, '$2a$12$crDkSPkIEE3T.Ld5.X39feQP38INybGFQLvw/MqyMegqlyBL9hr3.'),
(6060606060, 1, 'Isabella', 'Rojas', DATE '1992-07-23', 'Diagonal 20 #45-67', '3160606060', 'isabella.rojas@email.com', 2600000.00, 3, '$2a$12$crDkSPkIEE3T.Ld5.X39feQP38INybGFQLvw/MqyMegqlyBL9hr3.'),
(7070707070, 1, 'Camilo', 'Sánchez', DATE '1988-11-15', 'Carrera 40 #78-90', '3170707070', 'camilo.sanchez@email.com', 2400000.00, 3, '$2a$12$crDkSPkIEE3T.Ld5.X39feQP38INybGFQLvw/MqyMegqlyBL9hr3.'),
(8080808080, 1, 'Paola', 'Giraldo', DATE '1974-03-29', 'Avenida 75 #23-45', '3180808080', 'paola.giraldo@email.com', 35000000.00, 3, '$2a$12$crDkSPkIEE3T.Ld5.X39feQP38INybGFQLvw/MqyMegqlyBL9hr3.'),
(9090909090, 1, 'Esteban', 'Muñoz', DATE '1985-06-18', 'Calle 35 #56-78', '3190909090', 'esteban.munoz@email.com', 12800000.00, 3, '$2a$12$crDkSPkIEE3T.Ld5.X39feQP38INybGFQLvw/MqyMegqlyBL9hr3.'),
(1212121212, 1, 'Adriana', 'Cortés', DATE '1977-09-04', 'Transversal 65 #12-34', '3112121212', 'adriana.cortes@email.com', 38000000.00, 3, '$2a$12$crDkSPkIEE3T.Ld5.X39feQP38INybGFQLvw/MqyMegqlyBL9hr3.');