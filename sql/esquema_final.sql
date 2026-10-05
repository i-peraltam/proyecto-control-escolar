CREATE DATABASE IF NOT EXISTS control_estudios;
USE control_estudios;

CREATE TABLE IF NOT EXISTS estudiantes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    matricula VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    correo VARCHAR(100),
    fecha_inscripcion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cursos (
    id_curso INT AUTO_INCREMENT PRIMARY KEY,
    nombre_curso VARCHAR(100) NOT NULL,
    creditos INT NOT NULL
);

CREATE TABLE IF NOT EXISTS inscripciones (
    id_inscripcion INT AUTO_INCREMENT PRIMARY KEY,
    id_estudiante INT NOT NULL,
    id_curso INT NOT NULL,
    periodo VARCHAR(20) NOT NULL,
    CONSTRAINT uq_inscripcion UNIQUE (id_estudiante, id_curso, periodo),
    CONSTRAINT fk_inscripcion_estudiante
        FOREIGN KEY (id_estudiante) REFERENCES estudiantes(id) ON DELETE CASCADE,
    CONSTRAINT fk_inscripcion_curso
        FOREIGN KEY (id_curso) REFERENCES cursos(id_curso) ON DELETE CASCADE
);

INSERT INTO cursos (nombre_curso, creditos)
SELECT 'Bases de Datos', 8
WHERE NOT EXISTS (SELECT 1 FROM cursos WHERE nombre_curso='Bases de Datos');

INSERT INTO cursos (nombre_curso, creditos)
SELECT 'Programacion Estructurada', 8
WHERE NOT EXISTS (SELECT 1 FROM cursos WHERE nombre_curso='Programacion Estructurada');

INSERT INTO cursos (nombre_curso, creditos)
SELECT 'Arquitectura de Software', 8
WHERE NOT EXISTS (SELECT 1 FROM cursos WHERE nombre_curso='Arquitectura de Software');

-- Consulta JOIN de evidencia
SELECT
    e.matricula,
    CONCAT(e.nombre, ' ', e.apellido) AS estudiante,
    c.nombre_curso,
    c.creditos,
    i.periodo
FROM inscripciones i
JOIN estudiantes e ON e.id = i.id_estudiante
JOIN cursos c ON c.id_curso = i.id_curso
ORDER BY i.id_inscripcion;
