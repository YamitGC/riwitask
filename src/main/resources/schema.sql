CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE
    );

CREATE TABLE IF NOT EXISTS tareas (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descripcion VARCHAR(1000),
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('PENDIENTE', 'EN_PROCESO', 'COMPLETADA')),
    prioridad VARCHAR(10) NOT NULL CHECK (prioridad IN ('BAJA', 'MEDIA', 'ALTA')),
    fecha_creacion TIMESTAMP NOT NULL,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id)
    );

CREATE INDEX IF NOT EXISTS idx_tareas_usuario ON tareas(usuario_id);
CREATE INDEX IF NOT EXISTS idx_tareas_estado ON tareas(estado);
CREATE INDEX IF NOT EXISTS idx_tareas_prioridad ON tareas(prioridad);
