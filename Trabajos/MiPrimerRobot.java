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
import java.util.concurrent.ConcurrentHashMap;

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

    public void turnRight() {
        turnLeft(); 
        turnLeft(); 
        turnLeft();
    }

    public void moveCheckBeeper() {
        if (!frontIsClear()) {
            return;
        }
        int nextStreet = currentStreet;
        int nextAvenue = currentAvenue;
        if (facingNorth()) nextStreet++;
        else if (facingSouth()) nextStreet--;
        else if (facingEast()) nextAvenue++;
        else if (facingWest()) nextAvenue--;
        while (!TrainControl.reservePosition(trainId, nextStreet, nextAvenue)) {
            try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
        }
        if (nextToABeeper() && TrainControl.isStation(currentStreet, currentAvenue)) {
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < 3000) {
                try { Thread.sleep(100); } catch (InterruptedException e) { break; }
            }
        }
        TrainControl.freePosition(currentStreet, currentAvenue);
        super.move();
        currentStreet = nextStreet;
        currentAvenue = nextAvenue;
        
    }

    private void InitializeRoute() {
        if (getStreet() == 33 && getAvenue() == 14) {
            move();
            turnLeft();
            
            goToNiquia();
            return;
        }
        if (getStreet() == 34 && getAvenue() == 14) {
            move();
            move();
            turnLeft();
           
            goToNiquia();
            return;
        }
        if (getStreet() == 32 && getAvenue() == 14) {
            goToNiquia();
            return;
        }
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
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        turnLeft();
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        for (int i = 0; i < 3; i++) {
            moveCheckBeeper(); 
            if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        }
        turnRight();
        for (int i = 0; i < 1; i++) {
            moveCheckBeeper();
            if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        }
        turnLeft(); 
        for (int i = 0; i < 3; i++) {
            moveCheckBeeper();
            if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        }
        turnRight();
        for (int i = 0; i < 2; i++) {
            moveCheckBeeper(); 
            if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        }
        turnLeft();
        for (int i = 0; i < 3; i++) {
            moveCheckBeeper();
            if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        }
        turnRight(); 
        for (int i = 0; i < 2; i++) {
            moveCheckBeeper(); 
            if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        }
        turnLeft(); 
        for (int i = 0; i < 5; i++) {
            moveCheckBeeper();
            if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        }
        turnLeft(); 
        for (int i = 0; i < 5; i++) {
            moveCheckBeeper();
            if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        }
        turnRight();
        for (int i = 0; i < 7; i++) {
            moveCheckBeeper();
            if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        }
        turnRight();
        for (int i = 0; i < 3; i++) {
            moveCheckBeeper();
            if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        }
        turnLeft(); 
        for (int i = 0; i < 6; i++) {
            moveCheckBeeper();
            if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        }
        turnRight();
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        turnLeft(); 
        for (int i = 0; i < 3; i++) {
            moveCheckBeeper();
            if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        }
        turnRight();
        for (int i = 0; i < 2; i++) {
            moveCheckBeeper();
            if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        }
        turnLeft(); 
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        turnLeft(); 
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        System.out.println("¡Llegué a La Estrella!");
    }

    public void Estrella_Niquia(){
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        turnLeft();
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        turnRight();
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        turnLeft();
        for(int i = 0; i < 7; i++) {
            moveCheckBeeper();
            if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        }
        turnRight();
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        turnLeft();
        for(int i = 0; i < 10; i++) {
            moveCheckBeeper();
            if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        }
        turnLeft();
        for(int i = 0; i < 6; i++) {
            moveCheckBeeper();
            if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        }
        turnRight();
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        turnRight();
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        turnLeft();
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        turnRight();
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        turnLeft();
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        turnRight();
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        turnLeft();
        for(int i = 0; i < 7; i++) {
            moveCheckBeeper();
            if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        }
        turnRight();
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        turnLeft();
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        turnLeft();
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        System.out.println("¡Llegué a Niquía!");
    }

    // --- Cambios para parada en estación extrema ---
    private boolean enEstacionExtrema() {
        return (getStreet() == 35 && getAvenue() == 19) || (getStreet() == 1 && getAvenue() == 11);
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
        // Bucle infinito de rutas, pero si se pide detener, termina en estación extrema
        while (true) {
            Niquia_Estrella();
            if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) break;
            Estrella_Niquia();
            if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) break;
        }

        while (!MiPrimerRobot.goToTaller.get()) {
            try {  
                Thread.sleep(100); 
            } catch (InterruptedException e) { 
                e.printStackTrace(); 
            }
        }
        irAlTaller();
    }

    public void irAlTaller() {
    
    if (getStreet() == 35 && getAvenue() == 19) {
        for (int i = 0; i < 3; i++) {
            move();
        }
        turnLeft(); 
        for (int i = 0; i < 2; i++) {
            move();
        }
        turnRight();
        move();
        turnRight();
        for (int i = 0; i < 2; i++) {
            move();
        }
        turnLeft();
        for (int i = 0; i < 14; i++) {
            move();
        }
        turnLeft();
        move();
        turnLeft();
        for (int i = 0; i < 13; i++) {
            move();
        }
        turnRight();
        move();
        move();
    }
    if (getStreet() == 1 && getAvenue() == 11) {
        for (int i = 0; i < 2; i++) {
            move();
        }
        turnLeft(); 
        for (int i = 0; i < 3; i++) {
            move();
        }
        turnRight();
        move();
        turnLeft();
        for (int i = 0; i < 6; i++) {
            move();
        }
        turnRight();
        for (int i = 0; i < 3; i++) {
            move();
        }
        turnLeft();
        for (int i = 0; i < 9; i++) {
            move();
        }
        turnLeft();
        for (int i = 0; i < 5; i++) {
            move();
        }
        turnRight();
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
        for (int i = 0; i < 3; i++) {
            move();
        }
        turnRight();
        move();
        turnLeft();
        for (int i = 0; i < 5; i++) {
            move();
        }
        turnLeft();
        for (int i = 0; i < 2; i++) {
            move();
        }
        turnRight();
        for (int i = 0; i < 2; i++) {
            move();
        }
        turnLeft();
        for (int i = 0; i < 14; i++) {
            move();
        }
        turnLeft();
        move();
        turnLeft();
        for (int i = 0; i < 13; i++) {
            move();
        }
        turnRight();
        move();
        move();
    } 
} 
}

class StationControl {
    private static final Object stationLock = new Object();
    private static volatile boolean sanAntonioBOcupada = false;
    private static volatile boolean cisnerosOcupada = false;
    private static volatile int currentTrainInSanAntonioB = -1;
    private static volatile boolean isTrainLeavingSanAntonioB = false;
    private static final Map<Integer, Boolean> trainDirections = new ConcurrentHashMap<>(); // true = hacia San Antonio, false = hacia San Javier
    
    public static void registerTrainDirection(int trainId, boolean towardsSanAntonio) {
        trainDirections.put(trainId, towardsSanAntonio);
    }
    
    public static void waitForSanAntonioB(int trainId, int currentStreet, int currentAvenue) throws InterruptedException {
        synchronized (stationLock) {
            while (sanAntonioBOcupada || isTrainLeavingSanAntonioB || 
                   TrainControl.isPositionOccupied(14, 13) || 
                   TrainControl.isPositionOccupied(14, 14)) {
                System.out.println("Tren " + trainId + " esperando en Cisneros porque San Antonio B está ocupada o en proceso de liberación");
                stationLock.wait();
            }
            
            // Reservar San Antonio B y marcar como ocupada
            sanAntonioBOcupada = true;
            currentTrainInSanAntonioB = trainId;
            System.out.println("Tren " + trainId + " ha reservado San Antonio B");
        }
    }
    
    public static void enterSanAntonioB(int trainId) {
        synchronized (stationLock) {
            cisnerosOcupada = false;
            System.out.println("Tren " + trainId + " ha entrado a San Antonio B");
            stationLock.notifyAll();
        }
    }
    
    public static void prepareLeaveSanAntonioB(int trainId) {
        synchronized (stationLock) {
            if (currentTrainInSanAntonioB == trainId) {
                isTrainLeavingSanAntonioB = true;
                System.out.println("Tren " + trainId + " preparándose para salir de San Antonio B");
            }
        }
    }
    
    public static void completeLeaveSanAntonioB(int trainId) {
        synchronized (stationLock) {
            if (currentTrainInSanAntonioB == trainId) {
                sanAntonioBOcupada = false;
                isTrainLeavingSanAntonioB = false;
                currentTrainInSanAntonioB = -1;
                System.out.println("Tren " + trainId + " ha liberado completamente San Antonio B");
                stationLock.notifyAll();
            }
        }
    }
}

class RacerB extends Racer {
    private final int trainId;
    
    public RacerB(int trainId, int street, int avenue, Direction direction, int beeps, Color color) {
        super(trainId, street, avenue, direction, beeps, color);
        this.trainId = trainId;
        StationControl.registerTrainDirection(trainId, true);
    }

    public void irAlTallerB() {
        if (getStreet() == 14 && getAvenue() == 15) {
        turnLeft();
        turnLeft();
        StationControl.prepareLeaveSanAntonioB(this.trainId);
        move();
        move();
        move();
        StationControl.completeLeaveSanAntonioB(this.trainId);
        sanAntonioToSanJavier();
        
    }
    if (getStreet() == 16 && getAvenue() == 1) {
        for (int i = 0; i < 2; i++) {
            move();
        }
        turnLeft(); 
        for (int i = 0; i < 5; i++) {
            move();
        }
        turnRight();
        move();
        turnLeft();
        for (int i = 0; i < 5; i++) {
            move();
        }
        turnLeft();
        for (int i = 0; i < 5; i++) {
            move();
        }
        turnRight();
        move();
        turnLeft();
        for (int i = 0; i < 4; i++) {
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
        for (int i = 0; i < 3; i++) {
            move();
        }
        turnRight();
        move();
        turnLeft();
        for (int i = 0; i < 5; i++) {
            move();
        }
        turnLeft();
        for (int i = 0; i < 2; i++) {
            move();
        }
        turnRight();
        for (int i = 0; i < 2; i++) {
            move();
        }
        turnLeft();
        for (int i = 0; i < 14; i++) {
            move();
        }
        turnLeft();
        move();
        turnLeft();
        for (int i = 0; i < 13; i++) {
            move();
        }
        turnRight();
        move();
        move();
    }
    }

    // --- Cambios para parada en estación extrema ---
    private boolean enEstacionExtrema() {
        // San Javier: (16,1), San Antonio: (14,15)
        return (getStreet() == 16 && getAvenue() == 1) || (getStreet() == 14 && getAvenue() == 15);
    }

    @Override
    public void run() {
        initialize();
        while (!MiPrimerRobot.startSignal.get()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Bucle infinito de rutas, pero si se pide detener, termina en estación extrema
        while (true) {
            sanJavierToSanAntonio();
            if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) break;
            sanAntonioToSanJavier();
            if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) break;
        }

        while (!MiPrimerRobot.goToTaller.get()) {
            try { 
                Thread.sleep(100);
            } catch (InterruptedException e) { 
                e.printStackTrace(); 
            }  
        }
        irAlTallerB();
    }

    private void initialize() {
        if (getStreet() == 35 && getAvenue() == 1) {
            move();
            turnLeft();
        }
        while (getAvenue() < 14) move();
        if (getAvenue() == 14) {
            turnRight();
            move(); move();
            turnLeft();
        }
        moveToSanJavier();
    }

    public void sanJavierToSanAntonio() {
        System.out.println("Tren " + trainId + " iniciando ruta San Javier → San Antonio");
        
        // En Cisneros (13,12)
        if (currentStreet == 13 && currentAvenue == 12) {
            try {
                StationControl.waitForSanAntonioB(trainId, currentStreet, currentAvenue);
                
                // Movimiento hacia San Antonio B - NO GIRAR en Cisneros
                move(); // (14,12)
                move(); // (14,13)
                move(); // (14,14)
                move(); // (14,15) - San Antonio B
                
                StationControl.enterSanAntonioB(trainId);
                
                // Parada en estación
                Thread.sleep(3000);
                
                // Preparar para salir - AQU es donde debe girar
                StationControl.prepareLeaveSanAntonioB(trainId);
                turnLeft(); 
                turnLeft();
                
                // Movimiento de salida
                move(); // (14,14)
                move(); // (14,13)
                move(); // (14,12)
                
                StationControl.completeLeaveSanAntonioB(trainId);
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        }
        
        // Ruta correcta desde San Javier (16,1) hasta San Antonio B
        // Dos movimientos iniciales
        for(int i = 0; i < 2; i++) {
            moveCheckBeeper();
            if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        }
        
        // Giro a la izquierda y 5 movimientos
        turnLeft();
        for(int i = 0; i < 5; i++) {
            moveCheckBeeper();
            if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        }
        
        // Giro a la derecha y un movimiento
        turnRight();
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        
        // Giro a la izquierda y 8 movimientos (en el #6 llega a Cisneros)
        turnLeft();
        for(int i = 0; i < 8; i++) {
            moveCheckBeeper();
            if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
            // Si estamos en el sexto movimiento, estamos en Cisneros
            if (i == 5) {
                try {
                    StationControl.waitForSanAntonioB(trainId, currentStreet, currentAvenue);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        
        // Giro a la izquierda y un movimiento
        turnLeft();
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        
        // Giro a la derecha y movimiento final a San Antonio B
        turnRight();
        moveCheckBeeper(); // Llegada a San Antonio B
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        turnLeft();
        turnLeft(); 

        try {
            StationControl.enterSanAntonioB(trainId);
            // Parada en estación
            Thread.sleep(3000);
            
            // Preparar para salir
            StationControl.prepareLeaveSanAntonioB(trainId);
            
            
            // Movimiento de salida
            move(); // (14,14)
            move(); // (14,13)
            move(); // (14,12)
            
            StationControl.completeLeaveSanAntonioB(trainId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public void sanAntonioToSanJavier() {
        try {
            // Asegurarse de que el tren anterior haya salido completamente
            Thread.sleep(500);
            
            for(int i = 0; i < 6; i++) {
                moveCheckBeeper();
                if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
            }
            
            
            
            turnRight();
            moveCheckBeeper(); 
            if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
            
            
            turnLeft();
            for(int i = 0; i < 6; i++) {
                moveCheckBeeper();
                if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
            }
            
            turnRight();
            moveCheckBeeper(); 
            if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
            moveCheckBeeper();
            if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
            
            turnLeft();
            
             moveCheckBeeper();
            if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
            
            turnLeft();
           
            moveCheckBeeper();
            if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
            
            
            System.out.println("Tren " + trainId + " llego a San Javier");
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public void moveToSanJavier() {
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
        for(int i = 0; i < 3; i++) {
            super.move();
        }
        turnRight();
        for(int i = 0; i < 2; i++) {
            super.move();
        }
        turnLeft();
        for(int i = 0; i < 9; i++) {
            super.move();
        }
        turnRight();
        for(int i = 0; i < 4; i++) {
            super.move();
        }
        turnRight();
        super.move();
        turnLeft();
        for(int i = 0; i < 5; i++) {
            super.move();
        }
        turnRight();
        for(int i = 0; i < 2; i++) {
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
        super(trainId, street, avenue, direction, beeps, color);
    }

    // --- Cambios para parada en estación extrema ---
    private boolean enEstacionExtrema() {
        // Niquía: (32,14), La Estrella: (14,1)
        return (getStreet() == 35 && getAvenue() == 19) || (getStreet() == 1 && getAvenue() == 11);
    }

    @Override
    public void run() {
        InitializeRouteC();
        while (!MiPrimerRobot.startSignal.get()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Bucle infinito de rutas, pero si se pide detener, termina en estación extrema
        while (true) {
            Estrella_Niquia();
            if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) break;
            Niquia_Estrella();
            if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) break;
        }

        while (!MiPrimerRobot.goToTaller.get()) {
            try { 
                Thread.sleep(100); 
                } catch (InterruptedException e) { 
                e.printStackTrace(); 
                }
        }
        irAlTaller();
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
    public static final AtomicBoolean goToTaller = new AtomicBoolean(false);

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
            java.util.Scanner scanner = new java.util.Scanner(System.in);

            scanner.nextLine(); // Espera a que el usuario presione Enter
            startSignal.set(true); // Enviar la señal para que los trenes comiencen a moverse

            // --- Cambios para parada en estación extrema ---
            System.out.println("Trenes en movimiento. Presiona Enter para detenerlos en la estación extrema más cercana.");
            scanner.nextLine(); // Espera a que el usuario presione Enter (fin)
            startSignal.set(false);
            goToTaller.set(true); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}