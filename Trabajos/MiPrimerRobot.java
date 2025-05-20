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
            System.out.println("¡Pared detectada! Tren " + trainId + " no puede avanzar.");
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
        
        System.out.println("Tren " + trainId + " movido a (" + currentStreet + "," + currentAvenue + ")");
    }

    // Resto de métodos (turnRight, run, etc.) permanecen iguales

    public void turnRight() {
        turnLeft(); 
        turnLeft(); 
        turnLeft();
    }

     public void moveCheckBeeper() {
        if (!frontIsClear()) {
        System.out.println("¡Pared detectada! Tren " + trainId + " no puede avanzar.");
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
        System.out.println("🚉 Tren " + trainId + " EN estación (" + currentStreet + "," + currentAvenue + ")");
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 3000) {
            try { Thread.sleep(100); } catch (InterruptedException e) { break; }
        }
        System.out.println("🚉 Tren " + trainId + " SALE de estación");
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
    private static final Set<Integer> primeraSalida = Collections.synchronizedSet(new HashSet<>());
    
    // Variable para almacenar el ID del tren
    private int trainId;

    public RacerB(int trainId, int street, int avenue, Direction direction, int beeps, Color color) {
        super(trainId, street, avenue, direction, beeps, color);
        this.trainId = trainId;
    }
    
    // Método para obtener el ID del tren 
    private int getTrainId() {
        return this.trainId;
    }

    @Override
    public void run() {
        InitializeRouteB();

        // Esperar señal de inicio
        while (!MiPrimerRobot.startSignal.get()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Recorrido continuo
        while (true) {
            SanJavier_SanAntonio();
            // SanAntonio_SanJavier();
        }
    }

    private void InitializeRouteB() {
        if (getStreet() == 35 && getAvenue() == 1) {
            move();
            turnLeft();
        }
        while (getAvenue() < 14) move();
        if (getAvenue() == 14) {
            turnRight(); move(); move();
            turnLeft();
        }
        System.out.println("Tren " + getTrainId() + " llegó al punto de partida: Calle " + getStreet() + ", Avenida " + getAvenue());
        moveToSanJavier();
    }

    // Locks para cada estación de San Javier
    private static final ReentrantLock[] sanJavierLocks = {
        new ReentrantLock(), // 0: 16,1
        new ReentrantLock(), // 1: 14,5
        new ReentrantLock(), // 2: 13,9
        new ReentrantLock(), // 3: 13,12
        new ReentrantLock(), // 4: 14,15
        new ReentrantLock(), // 5: 14,12
        new ReentrantLock(), // 6: 14,9
        new ReentrantLock(), // 7: 15,5
        new ReentrantLock()  // 8: 16,2
    };

    // Coordenadas de las estaciones en orden
    private static final int[][] estacionesSanJavier = {
        {16,1}, {14,5}, {13,9}, {13,12}, {14,15}, {14,12}, {14,9}, {15,5}, {16,2}
    };

    public void SanJavier_SanAntonio() {
        int n = estacionesSanJavier.length;

        // Detectar estación inicial del tren
        int miEstacion = -1;
        for (int i = 0; i < n; i++) {
            if (getStreet() == estacionesSanJavier[i][0] && getAvenue() == estacionesSanJavier[i][1]) {
                miEstacion = i;
                break;
            }
        }
        if (miEstacion == -1) return; // Seguridad

        sanJavierLocks[miEstacion].lock();

        for (int i = miEstacion; i < n - 1; i++) {
            // Solo NO espera si es la PRIMERA salida de 16,1 para este tren
            if (!(i == 0 && miEstacion == 0 && !primeraSalida.contains(getTrainId()))) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                // Marca que este tren ya hizo su primera salida
                primeraSalida.add(getTrainId());
            }

            // Esperar a que la estación siguiente esté libre
            sanJavierLocks[i + 1].lock();

            // --- Mover solo el primer paso fuera de la estación actual ---
            moverPrimerPasoFuera(i);

            // Liberar la estación actual INMEDIATAMENTE después de dejarla
            sanJavierLocks[i].unlock();

            // --- Terminar el trayecto hasta la siguiente estación ---
            moverRestoHastaEstacion(i, i + 1);
        }

        // Última estación: espera 3 segundos y libera su lock
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sanJavierLocks[n - 1].unlock();
        
        // Ahora continúa hacia San Antonio
        continuarASanAntonio();
    }

    // Mueve solo el primer paso fuera de la estación i
    private void moverPrimerPasoFuera(int i) {
        switch (i) {
            case 0: // 16,1 a 14,5 (primer paso: moveCheckBeeper() hacia el este)
                moveCheckBeeper();
                break;
            case 1: // 14,5 a 13,9 (primer paso: moveCheckBeeper() hacia el este)
                moveCheckBeeper();
                break;
            case 2: // 13,9 a 13,12 (primer paso: moveCheckBeeper() hacia el este)
                moveCheckBeeper();
                break;
            case 3: // 13,12 a 14,15 (primer paso: move() hacia el sur - SIN BEEPER)
                move();
                break;
            case 4: // 14,15 a 14,12 (primer paso: moveCheckBeeper() hacia el oeste)
                turnLeft(); turnLeft(); moveCheckBeeper();
                break;
            case 5: // 14,12 a 14,9 (primer paso: moveCheckBeeper() hacia el oeste)
                moveCheckBeeper();
                break;
            case 6: // 14,9 a 15,5 (primer paso: moveCheckBeeper() hacia el sur)
                moveCheckBeeper();
                break;
            case 7: // 15,5 a 16,2 (primer paso: moveCheckBeeper() hacia el sur)
                moveCheckBeeper();
                break;
        }
    }

    // Termina el trayecto desde después del primer paso hasta la estación destino
    private void moverRestoHastaEstacion(int i, int j) {
        // 0: 16,1 -> 1: 14,5 (ya diste un paso hacia el este)
        if (i == 0 && j == 1) {
            moveCheckBeeper(); // 16,3
            turnLeft(); // sur
            moveCheckBeeper(); 
            moveCheckBeeper(); 
            moveCheckBeeper(); 
            moveCheckBeeper(); 
            return;
        }
        // 1: 14,5 -> 2: 13,9 (ya diste un paso hacia el este)
        if (i == 1 && j == 2) {
            turnRight(); 
            moveCheckBeeper(); 
            turnLeft(); 
            moveCheckBeeper(); 
            moveCheckBeeper(); 
            moveCheckBeeper(); 
            return;
        }
        // 2: 13,9 -> 3: 13,12 (ya diste un paso hacia el este)
        if (i == 2 && j == 3) {
            moveCheckBeeper(); 
            moveCheckBeeper(); 
            return;
        }
        // 3: 13,12 -> 4: 14,15 (ya diste un paso hacia el sur - SIN BEEPER)
        if (i == 3 && j == 4) {
            move();
            turnLeft(); 
            move(); 
            turnRight(); 
            move(); 
            return;
        }
        // 4: 14,15 -> 5: 14,12 (ya diste un paso hacia el oeste)
        if (i == 4 && j == 5) {
            moveCheckBeeper(); 
            moveCheckBeeper(); 
            return;
        }
        // 5: 14,12 -> 6: 14,9 (ya diste un paso hacia el oeste)
        if (i == 5 && j == 6) {
            moveCheckBeeper(); 
            moveCheckBeeper(); 
            return;
        }
        // 6: 14,9 -> 7: 15,5 (ya diste un paso hacia el sur)
        if (i == 6 && j == 7) {
            moveCheckBeeper(); 
            turnRight(); 
            moveCheckBeeper(); 
            turnLeft(); 
            moveCheckBeeper(); 
            moveCheckBeeper();
            return;
        }
        // 7: 15,5 -> 8: 16,2 (ya diste un paso hacia el sur)
        if (i == 7 && j == 8) {
            moveCheckBeeper(); 
            moveCheckBeeper(); 
            turnRight(); 
            moveCheckBeeper();   
            return;
        }
    }

    // Continúa el recorrido hacia San Antonio después de completar el circuito en San Javier
    private void continuarASanAntonio() {
        moveCheckBeeper();
        moveCheckBeeper();
        turnLeft();
        moveCheckBeeper();
        moveCheckBeeper();
        moveCheckBeeper();
        moveCheckBeeper();
        moveCheckBeeper();
        turnRight();
        moveCheckBeeper();
        turnLeft();
        for(int i = 0; i < 9; i++) {
            moveCheckBeeper();
        }
        turnLeft();
        moveCheckBeeper();
        turnRight();
        moveCheckBeeper();
        
        System.out.println("Tren " + getTrainId() + " llegó a San Antonio");
    }

    public void SanAntonio_SanJavier() {
        turnLeft();
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
        turnRight();
        moveCheckBeeper();
        
        System.out.println("Tren " + getTrainId() + " regresó a San Javier");
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
        for(int i = 0; i < 1; i++) {
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
        
        System.out.println("Tren " + getTrainId() + " llegó a San Javier");
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