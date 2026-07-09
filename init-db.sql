-- Script de inicialización de bases de datos
-- Se ejecuta automáticamente cuando MySQL arranca en Docker por primera vez.
-- Crea todas las bases de datos necesarias para cada microservicio.

CREATE DATABASE IF NOT EXISTS usuario       CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS membresia     CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS reserva       CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS clase         CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS servicio      CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS bloque_disponibilidad CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS ticket        CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS resena        CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Dar permisos al usuario de la aplicación sobre todas las bases
GRANT ALL PRIVILEGES ON usuario.*       TO 'gym_user'@'%';
GRANT ALL PRIVILEGES ON membresia.*     TO 'gym_user'@'%';
GRANT ALL PRIVILEGES ON reserva.*       TO 'gym_user'@'%';
GRANT ALL PRIVILEGES ON clase.*         TO 'gym_user'@'%';
GRANT ALL PRIVILEGES ON servicio.*      TO 'gym_user'@'%';
GRANT ALL PRIVILEGES ON bloque_disponibilidad.* TO 'gym_user'@'%';
GRANT ALL PRIVILEGES ON ticket.*        TO 'gym_user'@'%';
GRANT ALL PRIVILEGES ON resena.*        TO 'gym_user'@'%';

FLUSH PRIVILEGES;
