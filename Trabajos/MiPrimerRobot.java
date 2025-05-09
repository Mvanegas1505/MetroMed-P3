// Importaciones necesarias
import kareltherobot.*;
import java.awt.Color;
import java.util.concurrent.Semaphore;

// Control de trenes usando semáforos
class TrainControl {
    private static final int MAX_STREET = 36;
    private static final int MAX_AVENUE = 21;
    
    // Matriz de semáforos (1 por cada posición)
    private static Semaphore[][] positionSemaphores = new Semaphore[MAX_STREET + 1][MAX_AVENUE + 1];
    
    // Matriz para rastrear qué posiciones están ocupadas
    private static boolean[][] occupiedPositions = new boolean[MAX_STREET + 1][MAX_AVENUE + 1];
    
    // Objeto para sincronizar el acceso a occupiedPositions
    private static final Object lockOccupied = new Object();
    
    // Inicializar todos los semáforos
    static {
        for (int i = 0; i <= MAX_STREET; i++) {
            for (int j = 0; j <= MAX_AVENUE; j++) {
                positionSemaphores[i][j] = new Semaphore(1, true);
            }
        }
    }
    
    // Adquirir permiso para entrar a una posición
    public static void GetPosition(int street, int avenue) throws InterruptedException {
        if (isValidPosition(street, avenue)) {
            positionSemaphores[street][avenue].acquire();
            synchronized (lockOccupied) {
                occupiedPositions[street][avenue] = true;
            }
        } else {
            System.out.println("Posición fuera de límites: (" + street + "," + avenue + ")");
        }
    }
    
    // Liberar una posición
    public static void FreePosition(int street, int avenue) {
        if (isValidPosition(street, avenue)) {
            synchronized (lockOccupied) {
                occupiedPositions[street][avenue] = false;
            }
            positionSemaphores[street][avenue].release();
        }
    }
    
    // Verificar si una posición está ocupada
    public static boolean isPositionOccupied(int street, int avenue) {
        if (isValidPosition(street, avenue)) {
            synchronized (lockOccupied) {
                return occupiedPositions[street][avenue];
            }
        }
        return false;
    }
    
    // Verificar que una posición esté dentro de los límites
    private static boolean isValidPosition(int street, int avenue) {
        return street >= 0 && street <= MAX_STREET && avenue >= 0 && avenue <= MAX_AVENUE;
    }
}

// Clase base Racer
class Racer extends Robot implements Runnable {
    protected int currentStreet;
    protected int currentAvenue;
    protected boolean isAtStation = false;

    public Racer(int street, int avenue, Direction direction, int beeps, Color color) {
        super(street, avenue, direction, beeps, color);
        World.setupThread(this);
        this.currentStreet = street;
        this.currentAvenue = avenue;
        try {
            TrainControl.GetPosition(currentStreet, currentAvenue);
        } catch (InterruptedException e) {
            System.out.println("Error al inicializar posición: " + e.getMessage());
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
            System.out.println("¡Pared detectada! No puedo moverme.");
            return;
        }
        int nextStreet = currentStreet;
        int nextAvenue = currentAvenue;
        if (facingNorth()) {
            nextStreet--;
        }
        else if (facingSouth()) {
            nextStreet++;
        }
        else if (facingEast()) {
            nextAvenue++;
        }
        else if (facingWest()) {
            nextAvenue--;
        }

        boolean acquired = false;
        while (!acquired) {
            try {
                TrainControl.GetPosition(nextStreet, nextAvenue);
                acquired = true;
            } catch (InterruptedException e) {
                System.out.println("Interrupción al adquirir posición: " + e.getMessage());
            }
        }

        super.move();
        TrainControl.FreePosition(currentStreet, currentAvenue);
        currentStreet = nextStreet;
        currentAvenue = nextAvenue;
        isAtStation = nextToABeeper();
        System.out.println("Movido a (" + currentStreet + "," + currentAvenue + ")");
    }

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
    public RacerB(int street, int avenue, Direction direction, int beeps, Color color) {
        super(street, avenue, direction, beeps, color);
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
    public RacerC(int street, int avenue, Direction direction, int beeps, Color color) {
        super(street, avenue, direction, beeps, color);
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
            new Racer(32, 14, East, 0, Color.BLUE),
            new Racer(33, 14, South, 0, Color.BLUE),
            new Racer(34, 14, South, 0, Color.BLUE),
            new Racer(34, 13, East, 0, Color.BLUE),
            new Racer(34, 12, East, 0, Color.BLUE),
            new Racer(34, 11, East, 0, Color.BLUE),
            new Racer(34, 10, East, 0, Color.BLUE),
            new RacerB(34, 9, East, 0, Color.GREEN),
            new RacerB(34, 8, East, 0, Color.GREEN),
            new RacerB(34, 7, East, 0, Color.GREEN),
            new RacerB(34, 6, East, 0, Color.GREEN),
            new RacerB(34, 5, East, 0, Color.GREEN),
            new RacerB(34, 4, East, 0, Color.GREEN),
            new RacerB(34, 3, East, 0, Color.GREEN),
            new RacerB(34, 2, East, 0, Color.GREEN),
            new RacerB(34, 1, East, 0, Color.GREEN),
            new RacerB(35, 1, South, 0, Color.GREEN),
            new RacerC(35, 2, West, 0, Color.BLUE),
            new RacerC(35, 3, West, 0, Color.BLUE),
            new RacerC(35, 4, West, 0, Color.BLUE),
            new RacerC(35, 5, West, 0, Color.BLUE),
            new RacerC(35, 6, West, 0, Color.BLUE),
            new RacerC(35, 7, West, 0, Color.BLUE),
            new RacerC(35, 8, West, 0, Color.BLUE),
            new RacerC(35, 9, West, 0, Color.BLUE),
            new RacerC(35, 10, West, 0, Color.BLUE),
            new RacerC(35, 11, West, 0, Color.BLUE),
            new RacerC(35, 12, West, 0, Color.BLUE),
            new RacerC(35, 13, West, 0, Color.BLUE),
            new RacerC(35, 14, West, 0, Color.BLUE),
            new RacerC(35, 15, West, 0, Color.BLUE),
        };
        
        // Iniciar los trenes
        for (Racer r : trenes) {
            new Thread(r).start();
        }
    }
}
