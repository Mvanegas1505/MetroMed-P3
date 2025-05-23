# Simulación del Metro de Medellín con Karel

Este proyecto implementa una simulación del sistema del Metro de Medellín utilizando el entorno de programación Karel. La simulación incluye el manejo de múltiples trenes que operan en las líneas A y B del metro, con sincronización entre estaciones y control de concurrencia.

## Estructura del Proyecto

El proyecto está organizado en los siguientes archivos:

- `MiPrimerRobot.java`: Clase principal que inicializa y controla la simulación
- `Racer.java`: Clase base para los trenes
- `RacerB.java`: Implementación de los trenes de la línea B
- `RacerC.java`: Implementación de los trenes de la línea C
- `StationControl.java`: Control de sincronización de estaciones
- `TrainControl.java`: Control de posiciones y movimientos de los trenes
- `MetroMed.kwld`: Archivo de mundo de Karel con el mapa del metro

## Características

- Simulación de las líneas A y B del Metro de Medellín
- Control de concurrencia para evitar colisiones entre trenes
- Sincronización en estaciones críticas (San Antonio A/B, Cisneros)
- Manejo de paradas en estaciones
- Sistema de retorno al taller

## Requisitos

- Java JDK 8 o superior
- Karel JRobot

## Ejecución

1. Asegúrese de tener el entorno de Karel configurado correctamente
2. Compile los archivos Java:
   ```bash
   javac *.java
   ```
3. Ejecute la simulación:
   ```bash
   java MiPrimerRobot
   ```

## Control de la Simulación

1. Al iniciar, los trenes se posicionan en sus ubicaciones iniciales
2. Presione Enter para comenzar el movimiento (simulando las 4:20 AM)
3. Presione Enter nuevamente para detener los trenes en las estaciones extremas
4. Los trenes retornarán automáticamente al taller

## Implementación

### Control de Concurrencia

- Se utiliza sincronización mediante locks y monitores
- Implementación de mecanismos para evitar deadlocks
- Control de acceso a recursos compartidos (estaciones)

### Rutas

- Línea A: Niquía - La Estrella
- Línea B: San Javier - San Antonio B
- Línea C: Conexión entre líneas A y B

### Estaciones Críticas

- San Antonio A/B: Control especial para cambio de líneas
- Cisneros: Sincronización con San Antonio B
- Taller: Punto de inicio y fin de operaciones 