# Sistema de Control Escolar - Proyecto Integrador de Bases de Datos

Proyecto de escritorio en Java + Swing con MySQL 8.0 y Docker.

## Incluye
- CRUD completo de Estudiantes.
- CRUD completo de Inscripciones (selecciona una fila para cargarla y poder actualizarla o eliminarla).
- Tabla Cursos para relacionar las inscripciones.
- Restriccion UNIQUE (id_estudiante, id_curso, periodo).
- Consulta JOIN entre estudiantes, cursos e inscripciones.
- DDL completo en `sql/esquema_final.sql`.

## Requisitos
- Docker Desktop
- Java 17
- Maven 3.9+ (opcional si se ejecuta desde un IDE)

## Ejecutar

1. Desde la raiz del proyecto:

```bash
docker compose up -d
```

2. Verificar:

```bash
docker ps
```

Debe aparecer el contenedor `mysql_clase` con el puerto `3307`.

3. Ejecutar la aplicacion:

```bash
mvn clean compile exec:java
```

## Credenciales de desarrollo
- Host: localhost
- Puerto: 3307
- Base: control_estudios
- Usuario: root
- Contrasena: root_password_clase

## Consultas CRUD

### Estudiantes
```sql
INSERT INTO estudiantes(matricula,nombre,apellido,correo) VALUES(?,?,?,?);
SELECT id,matricula,nombre,apellido,correo FROM estudiantes ORDER BY id;
UPDATE estudiantes SET matricula=?,nombre=?,apellido=?,correo=? WHERE id=?;
DELETE FROM estudiantes WHERE id=?;
```

### Inscripciones
```sql
INSERT INTO inscripciones(id_estudiante,id_curso,periodo) VALUES(?,?,?);
SELECT id_inscripcion,id_estudiante,id_curso,periodo FROM inscripciones ORDER BY id_inscripcion;
UPDATE inscripciones SET id_estudiante=?,id_curso=?,periodo=? WHERE id_inscripcion=?;
DELETE FROM inscripciones WHERE id_inscripcion=?;
```

### Consulta JOIN
```sql
SELECT e.matricula,
       CONCAT(e.nombre,' ',e.apellido) AS estudiante,
       c.nombre_curso,
       c.creditos,
       i.periodo
FROM inscripciones i
JOIN estudiantes e ON e.id = i.id_estudiante
JOIN cursos c ON c.id_curso = i.id_curso
ORDER BY i.id_inscripcion;
```

## Evidencias que faltan generar en la computadora del equipo
1. `docker compose up -d` y `docker ps`.
2. Crear y actualizar estudiante.
3. Crear y actualizar inscripcion.
4. Mostrar consulta JOIN.
5. Eliminar inscripcion y estudiante.
6. Grabar video de demostracion.

## GitHub
Despues de probar el proyecto:

```bash
git init
git add .
git commit -m "Entrega final proyecto integrador"
git branch -M main
git remote add origin TU_URL_DEL_REPOSITORIO
git push -u origin main
```
