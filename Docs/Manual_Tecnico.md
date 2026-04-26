# Proyecto 02
### Manual Técnico
---

## Datos del Estudiante
- **Nombre:** Juan Jacobo Díaz Naola
- **Carnet:** 202408106
- **Curso:** Laboratorio de Introducción a la Programación y Computación 1  
- **Sección:** B
- **Universidad de San Carlos de Guatemala**
---

## Introducción del Programa
Este programa, llamado Sancarlista Academy, es un sistema de gestión académica desarrollado en **Java**, utilizando una interfaz gráfica tipo *Swing*. La misma permite administrar una plataforma educativa con tres roles diferenciados: **Administrador**, **Instructor** y **Estudiante**; cada uno de estos con funciones específicas relacionadas con el rol en cuestión.

El proyecto implementa una arquitectura basada en el patrón de diseño **Modelo-Vista-Controlador (MVC)**, que permite una separación clara de responsabilidades entre la lógica de negocio, los datos y la presentación visual. Esta separación facilita el mantenimiento del código, su escalabilidad y la comprensión de cada componente de forma independiente.

Entre las funcionalidades destacadas se encuentran:

- **Carga masiva de datos** desde archivos CSV.
- **Generación de reportes** en formato PDF mediante la librería iText.
- **Exportación de datos a CSV** utilizando `PrintWriter` y `FileWriter`.
- **Bitácora del sistema** que registra cada operación realizada.
- **Monitor visual en tiempo real** que muestra estadísticas del sistema mediante tres hilos concurrentes ejecutándose en segundo plano.
---
## Requisitos del Sistema
Para que el programa se pueda ejecutar correctamente, es necesario contar con los siguientes requisitos en el sistema donde se ejecutará el mismo:
- **Java:** JDK 11 o superior
- **Sistema operativo:** Windows, Linux, macOS
- **Memoria RAM:** 2 GB mínimo
- **Espacio en disco:** 1 GB mínimo (recomendado)
- El archivo **Proyecto002.JAR**
---
## Arquitectura del Sistema

El sistema implementa el patrón **Modelo-Vista-Controlador (MVC)**:

- **Model**: Contiene las clases de datos puras. No conoce ni la vista ni el controlador. Solo define atributos, getters, setters y comportamiento básico de cada entidad.

- **Controller**: Contiene toda la lógica de negocio. Recibe peticiones de la vista, opera sobre los modelos y devuelve resultados. Nunca interactúa directamente con componentes visuales.

- **View**: Contiene todos los paneles y ventanas Swing. Solo se comunica con los controladores, nunca con los modelos directamente.
---
## Estructura de Paquetes

- **model/** → Clases de datos que representan las entidades del sistema. Implementan `Serializable` para permitir la persistencia.

- **controller/** → Lógica de negocio, validaciones y operaciones CRUD. Centraliza el acceso a datos a través de `DataStore`.

- **view/** → Interfaces gráficas Swing. Cada rol tiene su propio panel con pestañas.

- **util/** → Clases utilitarias reutilizables: serialización, bitácora, carga CSV y generación de reportes.

- **threads/** → Hilos de ejecución en segundo plano que muestran información en tiempo real.
---
## Modelo de Persistencia

El sistema usa **serialización Java nativa** ( `ObjectOutputStream` / `ObjectInputStream` ).  
Cada entidad implementa **Serializable** con un **serialVersionUID** definido.

Los datos se guardan automáticamente en la carpeta **data/** después de cada operación de escritura:

| Archivo | Contenido |
|---------|-----------|
| `instructores.ser` | Arreglo de Instructor[] |
| `estudiantes.ser` | Arreglo de Estudiante[] |
| `cursos.ser` | Arreglo de Curso[] |
| `secciones.ser` | Arreglo de Seccion[] |
| `notas.ser` | Arreglo de Nota[] |

Al iniciar el sistema, `DataStore.cargarDatos()` deserializa todos los archivos y recalcula los contadores con `contarNoNulos()`.

---
## Sistema de Bitácora

Archivo de texto plano ubicado en `data/bitacora.txt`. Se escribe en modo append  
( `FileWriter(archivo, true)` ) para no sobreescribir registros anteriores.

Formato:

`[15/10/2025 09:12:03] | ADMINISTRADOR | admin | CREAR_SECCION | EXITOSA | Seccion SEC-2025-01 creada`

---
## Manejo de Archivos CSV

El sistema acepta archivos `.csv` separados por comas. No se aceptan archivos Excel `.xlsx`.

### Validaciones aplicadas a todos los CSV:
- Formato correcto de columnas
- Códigos únicos (no duplicados)
- Capacidad máxima del arreglo
- Valores numéricos válidos (créditos, notas, ponderaciones)
- Rangos correctos (nota 0-100, ponderación > 0)

---
## Generación de Reportes

- PDFs generados con iText 5. Se guardan en `reportes/` con nombre: `DD_MM_YYYY_HH_mm_ss_TipoReporte.pdf`  

- CSVs generados con `PrintWriter + FileWriter`. Mismo directorio y convención de nombre.

- Cada generación queda registrada automáticamente en la bitácora.

---
## Implementación de Hilos

Los tres hilos se crean como **daemon threads** ( `setDaemon(true)` ), lo que significa que se detienen automáticamente si el proceso principal termina.

- **Sincronización:** Los contadores estáticos compartidos ( `sesionesActivas`, `inscripcionesPendientes`) usan bloques synchronized con un objeto `lock` para evitar condiciones de carrera.

- **Bandera volatile:** La variable `activo` se declara `volatile` para garantizar visibilidad entre hilos sin necesidad de sincronización adicional.

- **Actualización de UI:** Se usa `SwingUtilities.invokeLater()` para actualizar el `JTextFieldArea` desde el hilo secundario de forma segura, ya que Swing no es thread-safe.

- **Detención:** Al cerrar sesión, cada panel llama `detener()` en los tres hilos antes de hacer logout.
---
## Conceptos POO Aplicados

- **Herencia:** Administrador, Instructor y Estudiante extienden Usuario, heredando atributos y comportamiento común.

- **Abstracción:** Usuario es abstracta y no puede instanciarse directamente. Obliga a las subclases a implementar `getRol()`.

- **Polimorfismo:** `getRol()` retorna un valor diferente en cada subclase.  
`AuthController.login()` retorna un Usuario genérico y la vista usa instanceof o getRol() para redirigir al panel correcto.

- **Encapsulamiento:** Todos los atributos son private con acceso controlado mediante getters y setters.

- **Interfaces:** Serializable en todas las entidades del modelo. Runnable en los tres hilos.
---
## Validaciones del Sistema

- **Login:** Campos no vacíos, credenciales correctas, redirección según rol.

- **Códigos únicos:** Antes de crear cualquier entidad se verifica que el código no exista ya en el arreglo.

- **Notas:** Rango 0-100, ponderación mayor a 0, sin etiquetas duplicadas para el mismo estudiante en la misma sección, instructor debe tener la sección asignada, estudiante debe estar inscrito.

- **Inscripción:** No duplicados en la misma sección, sin choques de horario con secciones ya inscritas en el mismo semestre.

- **Desasignación:** No permitida si el estudiante tiene notas registradas en esa sección.
---
## Flujo de Uso desde Cero

**1.** Iniciar el sistema **→** pantalla de login

**2.** Ingresar como Admin ( `admin / IPC1<SECCION>` )

**3.** Crear Instructores (manual o CSV)

**4.**  Crear Estudiantes (manual o CSV)

**5.** Crear Cursos (manual o CSV)

**6.**  Crear Secciones asignando curso + instructor + horario + semestre

**7.**  Estudiante inicia sesión e inscribe secciones disponibles

**8.**  Instructor inicia sesión y registra notas en sus secciones

**9.**  Estudiante consulta calificaciones y promedios

**10.**  Admin/Instructor generan reportes PDF o exportan CSV

---
**Juan Jacobo Díaz Naola, 202408106**