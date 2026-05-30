Nombres: Bastian Molina, Patricio Valdés

Descripcion: Sistema backend de gestión para un gimnasio construido con Spring Boot 3.3 + Java 21, organizado como 9 microservicios independientes que se comunican mediante headers HTTP (X-User-Id, X-User-Roles). 
Cada servicio tiene su propia base de datos MySQL y corre en un puerto distinto.

Funcionalidades: 
usuarios = CRUD de usuarios, activar/desactivar, cambiar rol, ver/editar perfil, cambiar contraseña
autenticacion = Login con JWT, registro de nuevos clientes
membresias = Planes, membresías por usuario, renovación, ítems incluidos
reservas = Crear reservas, confirmar/cancelar/completar, historial de estados
clases = Crear clases, inscripciones, cambiar entrenador/estado
servicios = CRUD de servicios, activar/desactivar
gestionperfil = Perfil personal, métodos de pago, bloques de disponibilidad de entrenadores
soporte = Tickets de soporte, historial de mensajes, asignación a agentes
resena = Crear reseñas con calificación (1-10), promedio por servicio

Instrucciones para ejecución:
1-Crear las bases de datos para cada microservicio
2-Lanzar cada microservicio desde su main o carpeta raiz en el orden de primero usuario luego autenticacion y los demas microservicios
3-Una vez que todos los servicios están corriendo (las tablas se crean automáticamente) esto carga los roles, estados de usuario/reserva/clase/ticket/servicio, usuarios de prueba, servicios,
planes de membresía y motivos de soporte.
4-Hacemos login con cualquiera de los usuarios de prueba: | admin@gym.cl:Admin123ADMINISTRADOR |cliente@gym.cl:Cliente123CLIENTE | entrenador@gym.cl:Entrena123ENTRENADOR |soporte@gym.clSoporte123SOPORTE|
La respuesta será un token
