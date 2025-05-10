// Importaciones necesarias
import kareltherobot.*;
import java.awt.Color;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class TrainControl {

    private static final int MAX_STREET = 36;
    private static final int MAX_AVENUE = 21;

    // Matriz para rastrear qué tren ocupa cada posición (0 = vacío, >0 = ID del tren)
    private static int[][] occupation = new int[MAX_STREET + 1][MAX_AVENUE + 1];
    
    // Lock para proteger el acceso a la matriz
    private static final Lock lock = new ReentrantLock();

    // Intenta reservar una posición para un tren específico
    public static boolean reservePosition(int trainId, int street, int avenue) {
        if (!isValidPosition(street, avenue)) {
            System.out.println("Posición fuera de límites: (" + street + "," + avenue + ")");
            return false;
        }
        
        lock.lock();
        try {
            if (occupation[street][avenue] == 0) {
                occupation[street][avenue] = trainId;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    // Libera una posición
    public static void freePosition(int street, int avenue) {
        if (!isValidPosition(street, avenue)) {
            return;
        }
        
        lock.lock();
        try {
            occupation[street][avenue] = 0;
        } finally {
            lock.unlock();
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
        return street >= 1 && street <= MAX_STREET && avenue >= 1 && avenue <= MAX_AVENUE;
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

    @Override
    public void run() {
        InitializeRoute();
    }
}

class RacerB extends Racer {
    public RacerB(int trainId, int street, int avenue, Direction direction, int beeps, Color color) {
        super(trainId, street, avenue, direction, beeps, color); // Agregar trainId
    }

    @Override
    public void run() {
        InitializeRouteB();
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
        System.out.println("Tren llegó al punto de partida: Calle " + getStreet() + ", Avenida " + getAvenue());
        moveToSanJavier();
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
        
        System.out.println("Llegué a San Javier");
    }
}

class RacerC extends Racer {

    public RacerC(int trainId, int street, int avenue, Direction direction, int beeps, Color color) {
        super(trainId, street, avenue, direction, beeps, color); // Agregar trainId
    }

    @Override
    public void run() {
        InitializeRouteC();
    }

    private void InitializeRouteC() {
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
    public static void main(String[] args) {
        World.readWorld("MetroMed.kwld");
        World.setVisible(true);
        World.setDelay(10);

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
        };
        
        // Iniciar los trenes
        for (Racer r : trenes) {
            new Thread(r).start();
        }
    }
}
