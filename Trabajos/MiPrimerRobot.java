// Importaciones necesarias
import kareltherobot.*;
import java.awt.Color;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;



class TrainControl {

    // Matriz de 36 x 22 representando las calles y las avenidas del mundo
    private static int[][] occupation = new int[37][22]; //1-37 - 1-22

     
    // Matriz de estaciones (beeper positions)
    private static final int[][] ESTACIONES = {
        {35,19}, {34,19}, {31,16}, {31,17}, {27,15}, {27,16}, 
        {24,13}, {24,14}, {20,11}, {20,12}, {19,14}, {18,14},
        {16,16}, {16,17}, {14,16}, {14,17}, {12,16}, {12,17},
        {11,15}, {10,15}, {9,13}, {9,14}, {6,13}, {6,14},
        {3,12}, {3,13}, {2,11}, {1,11}, {16,1}, {16,2},
        {15,5}, {14,5}, {14,9}, {13,9}, {14,12}, {13,12}, {14,15}
    };

    public static boolean isStation(int street, int avenue) {
        for (int[] estacion : ESTACIONES) {
            if (estacion[0] == street && estacion[1] == avenue) {
                return true;
            }
        }
        return false;
    }
    // Lock for protecting matrix access
    private static final Lock lock = new ReentrantLock();

    // Reservar una posición del metro para evitar choques
    public static boolean reservePosition(int trainId, int street, int avenue) {
        if (!isValidPosition(street, avenue)) {
            System.out.println("Posición fuera de límites: (" + street + "," + avenue + ")");
            return false;
        }
        
        lock.lock(); // Inicio seccion critica 
        try {
            if (occupation[street][avenue] == 0) {
                occupation[street][avenue] = trainId;
                return true;
            }
            return false;
        } finally {
            lock.unlock(); //Fin de la seccion critica 
        }
    }

    // Libera posicion ocupada por un tren 
    public static void freePosition(int street, int avenue) {
        if (!isValidPosition(street, avenue)) {
            return;
        }
        
        lock.lock(); // Inicio Seccion critica 
        try {
            occupation[street][avenue] = 0; 
        } finally {
            lock.unlock(); //Fin de Seccion Critica
        }
    }

    // Verifica si una posición está ocupada
    public static boolean isPositionOccupied(int street, int avenue) {
        if (!isValidPosition(street, avenue)) {
            return true; // Considerar posiciones inválidas como ocupadas
        }
        
        lock.lock();
        try {
            return occupation[street][avenue] != 0;
        } finally {
            lock.unlock();
        }
    }

    private static boolean isValidPosition(int street, int avenue) {
        return street >= 1 && street <= 36 && avenue >= 1 && avenue <= 22;
    }

}
// Clase base Racer
class Racer extends Robot implements Runnable {
    protected int currentStreet;
    protected int currentAvenue;
    private   int trainId;
    protected boolean isAtStation = false;

    public Racer(int trainId, int street, int avenue, Direction direction, int beeps, Color color) {
        super(street, avenue, direction, beeps, color);
        this.trainId = trainId;
        this.currentStreet = street;
        this.currentAvenue = avenue;
        World.setupThread(this);
        
        // Reservar la posición inicial
        while (!TrainControl.reservePosition(trainId, currentStreet, currentAvenue)) {
            try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    public int getStreet() { 
        return currentStreet; 
    }

    public int getAvenue() { 
        return currentAvenue; 
    }

    @Override
    public void move() {
        if (!frontIsClear()) {
            
            return;
        }
        
        // Calcular próxima posición
        int nextStreet = currentStreet;
        int nextAvenue = currentAvenue;
        
        if (facingNorth()) nextStreet++;
        else if (facingSouth()) nextStreet--;
        else if (facingEast()) nextAvenue++;
        else if (facingWest()) nextAvenue--;

        // Esperar hasta que la posición esté disponible
        while (!TrainControl.reservePosition(trainId, nextStreet, nextAvenue)) {
            try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
        }

        
        // Liberar posición actual
        TrainControl.freePosition(currentStreet, currentAvenue);
        
        // Mover físicamente
        super.move();
        
        // Actualizar posición
        currentStreet = nextStreet;
        currentAvenue = nextAvenue;
        
       
    }

    // Resto de métodos (turnRight, run, etc.) permanecen iguales

    public void turnRight() {
        turnLeft(); 
        turnLeft(); 
        turnLeft();
    }

     public void moveCheckBeeper() {
        if (!frontIsClear()) {
        
        return;
    }

    // Calcular próxima posición
    int nextStreet = currentStreet;
    int nextAvenue = currentAvenue;
    
    if (facingNorth()) nextStreet++;
    else if (facingSouth()) nextStreet--;
    else if (facingEast()) nextAvenue++;
    else if (facingWest()) nextAvenue--;

    // Esperar hasta que la posición esté disponible
    while (!TrainControl.reservePosition(trainId, nextStreet, nextAvenue)) {
        try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    // ANTES de moverse: verificar si la posición actual es estación
    if (nextToABeeper() && TrainControl.isStation(currentStreet, currentAvenue)) {
       
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 3000) {
            try { Thread.sleep(100); } catch (InterruptedException e) { break; }
        }
        
    }

    // Liberar posición actual
    TrainControl.freePosition(currentStreet, currentAvenue);
    
    // Movimiento físico
    super.move();
    
    // Actualizar posición interna
    currentStreet = nextStreet;
    currentAvenue = nextAvenue;
    
    System.out.println("Tren " + trainId + " en (" + currentStreet + "," + currentAvenue + ")");
}
    

    private void InitializeRoute() {
    // Verificar si el tren ya está en el punto de partida
    if (getStreet() == 33 && getAvenue() == 14) {
        move();
        turnLeft();
        System.out.println("Tren llegó al punto de partida: Calle " + getStreet() + ", Avenida " + getAvenue());
        goToNiquia();
        return;
    }

    if (getStreet() == 34 && getAvenue() == 14) {
        move();
        move();
        turnLeft();
        System.out.println("Tren llegó al punto de partida: Calle " + getStreet() + ", Avenida " + getAvenue());
        goToNiquia();
        return;
    }

    if (getStreet() == 32 && getAvenue() == 14) {
        goToNiquia();
        return;
    }

    // Si no está en el punto de partida, moverse hacia la avenida 14
    while (getAvenue() < 14) {
        move();
        System.out.println("While 1");
    }

    turnRight();

    for (int i = 0; i < 2; i++) {
        move();
    }

    turnLeft();
    System.out.println("Tren llegó al punto de partida: Calle " + getStreet() + ", Avenida " + getAvenue());
    goToNiquia();
}

    public void goToNiquia() {
        move(); 
        move(); 
        move();
        turnLeft(); 
        move(); 
        move();
        turnRight(); 
        move(); 
        move(); 
        move();
        turnLeft(); 
        move(); 
        turnLeft(); 
        move();
        System.out.println("¡Llegue a Niquía!");
    }

    public void Niquia_Estrella(){
        moveCheckBeeper();
        moveCheckBeeper();
        moveCheckBeeper();
        turnLeft();
        moveCheckBeeper();
        moveCheckBeeper();
        moveCheckBeeper();
        for (int i = 0; i < 3; i++) {
                moveCheckBeeper(); 
            }

            turnRight();
            for (int i = 0; i < 1; i++) {
                moveCheckBeeper();
            }

            turnLeft(); 
            for (int i = 0; i < 3; i++) {
                moveCheckBeeper();
            }

            turnRight();
            for (int i = 0; i < 2; i++) {
                moveCheckBeeper(); 
            }

            turnLeft();
            for (int i = 0; i < 3; i++) {
                moveCheckBeeper();
            }

            turnRight(); 
            for (int i = 0; i < 2; i++) {
                moveCheckBeeper(); 
            }

            turnLeft(); 
            for (int i = 0; i < 5; i++) {
                moveCheckBeeper();
            }

            turnLeft(); 
            for (int i = 0; i < 5; i++) {
                moveCheckBeeper();
            }

            turnRight();
            for (int i = 0; i < 7; i++) {
                moveCheckBeeper();
            }

            turnRight();
            for (int i = 0; i < 3; i++) {
                moveCheckBeeper();
            }

            turnLeft(); 
            for (int i = 0; i < 6; i++) {
                moveCheckBeeper();
            }

            turnRight();
            moveCheckBeeper();

            turnLeft(); 
            for (int i = 0; i < 3; i++) {
                moveCheckBeeper();
            }

            turnRight();
            for (int i = 0; i < 2; i++) {
                moveCheckBeeper();
            }

            turnLeft(); 
            moveCheckBeeper();

            turnLeft(); 
            moveCheckBeeper();

        System.out.println("¡Llegué a La Estrella!");
        
        

 }

    public void Estrella_Niquia(){

        moveCheckBeeper();
        moveCheckBeeper();
        turnLeft();
        moveCheckBeeper();
        moveCheckBeeper();
        moveCheckBeeper();
        turnRight();
        moveCheckBeeper();
        turnLeft();
        for(int i = 0; i < 7; i++) {
            moveCheckBeeper();
        }
        turnRight();
        moveCheckBeeper();
        moveCheckBeeper();
        moveCheckBeeper();
        turnLeft();
        for(int i = 0; i < 10; i++) {
            moveCheckBeeper();
        }
        turnLeft();
        for(int i = 0; i < 6; i++) {
            moveCheckBeeper();
        }
        turnRight();
        moveCheckBeeper();
        moveCheckBeeper();
        moveCheckBeeper();
        turnRight();
        moveCheckBeeper();
        moveCheckBeeper();
        turnLeft();
        moveCheckBeeper();
        moveCheckBeeper();
        moveCheckBeeper();
        turnRight();
        moveCheckBeeper();
        moveCheckBeeper();
        turnLeft();
        moveCheckBeeper();
        moveCheckBeeper();
        moveCheckBeeper();
        turnRight();
        moveCheckBeeper();
        turnLeft();
        for(int i = 0; i < 7; i++) {
            moveCheckBeeper();
        }
        turnRight();
        moveCheckBeeper();
        moveCheckBeeper();
        moveCheckBeeper();
        turnLeft();
        moveCheckBeeper();
        turnLeft();
        moveCheckBeeper();
        System.out.println("¡Llegué a Niquía!");

    }

    @Override
    public void run() {
        InitializeRoute();

        while (!MiPrimerRobot.startSignal.get()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        Niquia_Estrella();
        Estrella_Niquia();
    }
}

class RacerB extends Racer {
    // Shared control for station access
    private static final Object stationLock = new Object();
    private static volatile boolean sanAntonioBOcupada = false;
    private static volatile boolean cisnerosOcupada = false;
    
    // Train ID
    private final int trainId;

    public RacerB(int trainId, int street, int avenue, Direction direction, int beeps, Color color) {
        super(trainId, street, avenue, direction, beeps, color);
        this.trainId = trainId;
    }

    @Override
    public void run() {
        initialize();

        // Wait for start signal
        while (!MiPrimerRobot.startSignal.get()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Continuous route
        while (true) {
            sanJavierToSanAntonio();
            sanAntonioToSanJavier();
        }
    }

    private void initialize() {
        // Initial positioning depending on start location
        if (getStreet() == 35 && getAvenue() == 1) {
            move();
            turnLeft();
        }
        
        // Move to avenue 14
        while (getAvenue() < 14) move();
        
        if (getAvenue() == 14) {
            turnRight();
            move(); move();
            turnLeft();
        }
        
        System.out.println("Tren " + trainId + " llegó al punto de partida: Calle " + getStreet() + ", Avenida " + getAvenue());
        moveToSanJavier();
    }

    public void sanJavierToSanAntonio() {
        System.out.println("Tren " + trainId + " iniciando ruta San Javier → San Antonio");
        
        // Station coordinates in order of visit (San Javier circuit + way to San Antonio)
        int[][] estaciones = {
            {16, 1},  // San Javier start
            {14, 5},  // Santa Lucía
            {13, 9},  // Floresta
            {13, 12}, // Estadio/Cisneros
            {14, 15}, // San Antonio B
            {14, 27}  // Final point at San Antonio
        };
        
        int estacionActual = 0;
        
        // Find current station
        for (int i = 0; i < estaciones.length; i++) {
            if (getStreet() == estaciones[i][0] && getAvenue() == estaciones[i][1]) {
                estacionActual = i;
                break;
            }
        }
        
        
        // Follow the route from current station to San Antonio
        while (estacionActual < estaciones.length) {
            // Current position
            int currentStreet = getStreet();
            int currentAvenue = getAvenue();

                moveCheckBeeper();
            moveCheckBeeper();
            turnLeft();
            for(int i = 0; i < 6; i++) {
                moveCheckBeeper();
            }
            turnRight();
            moveCheckBeeper();
            turnLeft();
            for(int i = 0; i < 7; i++) {
                moveCheckBeeper();
            }

            
            // Handle special Cisneros → San Antonio B case (requires synchronization)
            if (currentStreet == 13 && currentAvenue == 12) {
                // We're at Cisneros station, waiting to enter San Antonio B
                synchronized (stationLock) {
                    // Wait until San Antonio B is free
                    while (sanAntonioBOcupada) {
                        try {
                            System.out.println("Tren " + trainId + " esperando en Cisneros porque San Antonio B está ocupada");
                            stationLock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    
                    // Mark Cisneros as occupied during transition
                    cisnerosOcupada = true;
                    System.out.println("Tren " + trainId + " en tránsito Cisneros → San Antonio B");
                }
                
                // Move from Cisneros to San Antonio B
                move();
                move();       // South to (14,12)
                turnLeft();   // Face East
                move();       // East to (14,13)
                turnRight();
                move();       // East to (14,15) - San Antonio B
                
                // Mark arrival at San Antonio B
                synchronized (stationLock) {
                    sanAntonioBOcupada = true;
                    cisnerosOcupada = false;  // Release Cisneros after transition
                    stationLock.notifyAll();  // Notify any waiting trains
                    System.out.println("Tren " + trainId + " ha llegado a San Antonio B, Cisneros liberada");
                }
                
                // Short stop at station
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                estacionActual++;
                continue;
            }
            
            // At San Antonio B, moving toward San Antonio
            if (currentStreet == 14 && currentAvenue == 15) {
                // Already spent time at the station above, now continue to San Antonio
                turnLeft(); turnLeft(); // Turn to face West (prepare for return journey)
                
                // Release San Antonio B before leaving
                synchronized (stationLock) {
                    sanAntonioBOcupada = false;
                    stationLock.notifyAll();
                    System.out.println("Tren " + trainId + " ha liberado San Antonio B");
                }
                sanAntonioToSanJavier();
                return; // Exit the method after returning
            }
            estacionActual++;
        }
    }
    
    public void sanAntonioToSanJavier() {
        System.out.println("Tren " + trainId + " iniciando ruta San Antonio → San Javier");
        
        for(int i = 0; i < 9; i++) {
            moveCheckBeeper();
        }
        turnRight();
        moveCheckBeeper();
        turnLeft();

        
        // Move back through main corridor
        for(int i = 0; i < 6; i++) {
            moveCheckBeeper();
        }
        
        turnRight();
        moveCheckBeeper();
        moveCheckBeeper();
        turnLeft();
        
        moveCheckBeeper();
        
        turnLeft();

        moveCheckBeeper();
        
        
        System.out.println("Tren " + trainId + " ha completado regreso a San Javier");
    }
    
    
    public void moveToSanJavier() {
        // Initial positioning to San Javier station area
        for(int i = 0; i < 2; i++) {
            super.move();
        }
        
        turnRight(); 
        for(int i = 0; i < 3; i++) {
            super.move(); 
        }
        
        turnRight();
        super.move();
        
        turnLeft(); 
        for(int i = 0; i < 3; i++) {
            super.move();
        }

        turnRight();
        for(int i = 0; i < 2; i++) {
            super.move(); 
        }

        turnLeft();
        for (int i = 0; i < 3; i++) {
            super.move(); 
        }
        
        turnRight(); 
        for (int i = 0; i < 2; i++) {
            super.move(); 
        }

        turnLeft();
        for (int i = 0; i < 9; i++) {
            super.move(); 
        }

        turnRight();
        for (int i = 0; i < 4; i++) {
            super.move(); 
        }

        turnRight();
        super.move(); 

        turnLeft(); 
        for (int i = 0; i < 5; i++) {
            super.move(); 
        }

        turnRight();
        for (int i = 0; i < 2; i++) {
            super.move(); 
        }

        turnLeft(); 
        super.move();
        
        turnLeft(); 
        super.move();
        
        
    }
}
    

class RacerC extends Racer {

    public RacerC(int trainId, int street, int avenue, Direction direction, int beeps, Color color) {
        super(trainId, street, avenue, direction, beeps, color); // Agregar trainId
    }

    @Override
    public void run() {
        InitializeRouteC();
         // Esperar la señal para comenzar el movimiento
        while (!MiPrimerRobot.startSignal.get()) {
            try {
                Thread.sleep(100); // Esperar brevemente antes de verificar nuevamente
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Estrella_Niquia();
        Niquia_Estrella();
    }

    private void InitializeRouteC() {
        if (getStreet() == 34 && getAvenue() == 15) {
            move();
            turnLeft();
        }
        while (getAvenue() > 1){ 
            move();
        }
        turnLeft(); 
        move(); 
        turnLeft();
        while (getAvenue() < 14){ 
            move();
        }
        turnRight();
        move();
        move();
        turnLeft();
        System.out.println("Tren llegó al punto de partida: Calle " + getStreet() + ", Avenida " + getAvenue());
        goToLaEstrella();
    }

   public void goToLaEstrella() {
        try {
            for (int i = 0; i < 2; i++) {
                move();
            }

            turnRight(); 
            for (int i = 0; i < 3; i++) {
                move(); 
            }

            turnRight();
            for (int i = 0; i < 1; i++) {
                move();
            }

            turnLeft(); 
            for (int i = 0; i < 3; i++) {
                move();
            }

            turnRight();
            for (int i = 0; i < 2; i++) {
                move(); 
            }

            turnLeft();
            for (int i = 0; i < 3; i++) {
                move(); 
            }

            turnRight(); 
            for (int i = 0; i < 2; i++) {
                move(); 
            }

            turnLeft(); 
            for (int i = 0; i < 5; i++) {
                move();
            }

            turnLeft(); 
            for (int i = 0; i < 5; i++) {
                move(); 
            }

            turnRight();
            for (int i = 0; i < 7; i++) {
                move(); 
            }

            turnRight();
            for (int i = 0; i < 3; i++) {
                move(); 
            }

            turnLeft(); 
            for (int i = 0; i < 6; i++) {
                move(); 
            }

            turnRight();
            move(); 

            turnLeft(); 
            for (int i = 0; i < 3; i++) {
                move(); 
            }

            turnRight();
            for (int i = 0; i < 2; i++) {
                move(); 
            }

            turnLeft(); 
            move();

            turnLeft(); 
            move(); 

            System.out.println("¡Llegué a La Estrella!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// Clase principal
public class MiPrimerRobot implements Directions {

     public static final AtomicBoolean startSignal = new AtomicBoolean(false);
    public static void main(String[] args) {
        World.readWorld("MetroMed.kwld");
        World.setVisible(true);
        World.setDelay(5);

        Racer[] trenes = new Racer[] {
            new Racer(1,32, 14, East, 0, Color.BLUE),
            new Racer(2,33, 14, South, 0, Color.BLUE),
            new Racer(3,34, 14, South, 0, Color.BLUE),
            new Racer(4,34, 13, East, 0, Color.BLUE),
            new Racer(5,34, 12, East, 0, Color.BLUE),
            new Racer(6,34, 11, East, 0, Color.BLUE),
            new Racer(7,34, 10, East, 0, Color.BLUE),
            new RacerB(8,34, 9, East, 0, Color.GREEN),
            new RacerB(9,34, 8, East, 0, Color.GREEN),
            new RacerB(10,34, 7, East, 0, Color.GREEN),
            new RacerB(11,34, 6, East, 0, Color.GREEN),
            new RacerB(12,34, 5, East, 0, Color.GREEN),
            new RacerB(13,34, 4, East, 0, Color.GREEN),
            new RacerB(14,34, 3, East, 0, Color.GREEN),
            new RacerB(15,34, 2, East, 0, Color.GREEN),
            new RacerB(16,34, 1, East, 0, Color.GREEN),
            new RacerB(17,35, 1, South, 0, Color.GREEN),
            new RacerC(18,35, 2, West, 0, Color.BLUE),
            new RacerC(19,35, 3, West, 0, Color.BLUE),
            new RacerC(20,35, 4, West, 0, Color.BLUE),
            new RacerC(21,35, 5, West, 0, Color.BLUE),
            new RacerC(22,35, 6, West, 0, Color.BLUE),
            new RacerC(23, 35, 7, West, 0, Color.BLUE),
            new RacerC(24,35, 8, West, 0, Color.BLUE),
            new RacerC(25,35, 9, West, 0, Color.BLUE),
            new RacerC(26,35, 10, West, 0, Color.BLUE),
            new RacerC(27,35, 11, West, 0, Color.BLUE),
            new RacerC(28, 35, 12, West, 0, Color.BLUE),
            new RacerC(29,35, 13, West, 0, Color.BLUE),
            new RacerC(30,35, 14, West, 0, Color.BLUE),
            new RacerC(31,35, 15, West, 0, Color.BLUE),
            new RacerC(32,34, 15, North, 0, Color.BLUE),
        };

        // Mostrar mensaje para esperar la entrada del usuario
        System.out.println("Los trenes están posicionados en sus rutas respectivas.");
        System.out.println("Presiona Enter cuando sean las 4:20 para iniciar el movimiento.");
        
        // Iniciar los trenes
        for (Racer r : trenes) {
            new Thread(r).start();
        }

          // Esperar la entrada del usuario
        try {
            System.in.read(); // Espera a que el usuario presione Enter
            startSignal.set(true); // Enviar la señal para que los trenes comiencen a moverse
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}