# Sistema de Control Escolar

Proyecto final de la asignatura Bases de Datos.

La aplicación fue desarrollada en Java y permite administrar estudiantes e inscripciones utilizando una base de datos MySQL. Para facilitar la configuración del entorno se utiliza Docker, y la conexión entre Java y MySQL se realiza mediante JDBC.

## Funcionalidades

La aplicación permite:

- Registrar, consultar, actualizar y eliminar estudiantes.
- Registrar, consultar, actualizar y eliminar inscripciones.
- Relacionar cada inscripción con un estudiante y un curso.
- Consultar información de estudiantes, cursos e inscripciones mediante una consulta JOIN.
- Evitar que un estudiante se inscriba dos veces al mismo curso en el mismo periodo.

## Estructura del proyecto

```text
src/main/java/app/
    ConexionDB.java
    Main.java
    VentanaPrincipal.java

sql/
    esquema_final.sql

docker-compose.yml
pom.xml
README.md
```

El archivo `esquema_final.sql` contiene la creación de la base de datos y de las tablas `estudiantes`, `cursos` e `inscripciones`.

## Requisitos

Para ejecutar el proyecto se necesita:

- Docker Desktop
- Java 17 o superior
- Maven, si se desea ejecutar el proyecto directamente con `mvn`

## Base de datos

La aplicación utiliza la base de datos:

```text
control_estudios
```

La configuración utilizada durante el desarrollo es:

```text
Host: localhost
Puerto: 3307
Usuario: root
Contraseña: root_password_clase
```

## Ejecución

Primero se debe levantar el servicio de MySQL desde la carpeta principal del proyecto:

```bash
docker compose up -d
```

Para comprobar que el contenedor está activo:

```bash
docker ps
```

El contenedor debe aparecer con el nombre `mysql_clase` y el puerto `3307`.

Si Maven está instalado, la aplicación puede iniciarse con:

```bash
mvn clean compile exec:java
```

También puede abrirse el proyecto desde un IDE de Java y ejecutarse desde la clase `Main`.

## Modelo de datos

El sistema utiliza tres tablas principales:

- `estudiantes`: almacena los datos de los estudiantes.
- `cursos`: contiene los cursos disponibles.
- `inscripciones`: relaciona a los estudiantes con los cursos y registra el periodo.

La tabla `inscripciones` contiene llaves foráneas hacia `estudiantes` y `cursos`, además de una restricción para evitar inscripciones duplicadas del mismo estudiante en el mismo curso y periodo.

## Consulta entre tablas

La aplicación incluye una consulta que reúne información de las tres tablas para mostrar la matrícula y nombre del estudiante, el curso, los créditos y el periodo de inscripción.

```sql
SELECT e.matricula,
       CONCAT(e.nombre, ' ', e.apellido) AS estudiante,
       c.nombre_curso,
       c.creditos,
       i.periodo
FROM inscripciones i
JOIN estudiantes e ON e.id = i.id_estudiante
JOIN cursos c ON c.id_curso = i.id_curso
ORDER BY i.id_inscripcion;
```

## Integrantes

- José Antonio Castillo Moreno
- Ignacio Antonio Peralta Muñoz
- Saúl Francisco Vargas Espinoza
