import kareltherobot.*;
import java.awt.color.*;;

class Racer extends Robot implements Runnable {
    public Racer(int Street, int Avenue, Direction direction, int beeps) {
        super(Street, Avenue, direction, beeps);
        World.setupThread(this);
    }

    public void race() {
        for(int i = 0; i < 14; i++){
        super.move();
        }
        turnRight();
        super.move();
        super.move();
        super.turnLeft();
        super.move();
        super.move();
        super.turnLeft();
        super.move();
        turnRight();
        super.move();
        super.move();
    }

    public void turnRight(){
        super.turnLeft();
        super.turnLeft();
        super.turnLeft();
    }

    @Override
    public void run() {
        race(); // Ejecuta la lógica del robot
    }
}

class RacerB extends Robot implements Runnable {
    private int currentStreet; // Variable para rastrear la calle actual
    private int currentAvenue; // Variable para rastrear la avenida actual

    public RacerB(int street, int avenue, Direction direction, int beeps) {
        super(street, avenue, direction, beeps);
        World.setupThread(this);
        this.currentStreet = street; // Inicializar la calle
        this.currentAvenue = avenue; // Inicializar la avenida
    }

    public int getStreet() {
        return currentStreet; // Retorna la calle actual
    }

    public int getAvenue() {
        return currentAvenue; // Retorna la avenida actual
    }

    @Override
    public void move() {
        super.move(); // Llama al método original de `Robot`

        // Actualiza la posición del robot según la dirección actual
        if (facingNorth()) {
            currentStreet--;
        } else if (facingSouth()) {
            currentStreet++;
        } else if (facingEast()) {
            currentAvenue++;
        } else if (facingWest()) {
            currentAvenue--;
        }
    }

    public void moveToSanJavier() {
        // Moverse desde el taller (calle 34, avenida 1) hacia San Javier
        while (getStreet() != 24) { // San Javier está en la calle 24
            move();
        }
        while (getAvenue() != 13) { // San Javier está en la avenida 13
            move();
        }

        // Simular llegada a la estación
        System.out.println("Llegué a San Javier en: Calle " + getStreet() + ", Avenida " + getAvenue());
    }

    @Override
    public void run() {
        moveToSanJavier(); // Ejecuta la lógica para ir a San Javier
    }
}

public class MiPrimerRobot implements Directions {
    public static void main(String[] args) {
        // Configuración del mundo
        World.readWorld("MetroMed.kwld");
        World.setVisible(true);

        // Crear dos robots
        Racer Karel1 = new Racer(35, 1, East, 0);
        RacerB Karel2 = new RacerB(34, 1, East, 0);
        // Cambiar el color del segundo robot a azul
       

        // Iniciar ambos robots como hilos
        Thread thread1 = new Thread(Karel1);
        Thread thread2 = new Thread(Karel2);
        
        
        thread1.start();
        thread2.start();
        
    }
}
