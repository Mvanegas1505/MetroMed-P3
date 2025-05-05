import kareltherobot.*;
import java.awt.Color;
//import java.util.concurrent.locks;
//import java.util.concurrent.semaphores;


class Racer extends Robot implements Runnable {
    private int currentStreet; // Variable para rastrear la calle actual
    private int currentAvenue; // Variable para rastrear la avenida actual
    public Racer(int Street, int Avenue, Direction direction, int beeps, Color color) {
        super(Street, Avenue, direction, beeps, color);
        World.setupThread(this);
        this.currentStreet = Street; 
        this.currentAvenue = Avenue;
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

    private void InitializeRoute() {
    if(getAvenue()==14 && getStreet()==32){
        goToNiquia();
    } else {
    // Ir hacia la avenida 14
    while (getAvenue() < 14) {
        move();
        System.out.println("While 1");
    }
    turnRight();
    for (int i = 0; i < 2; i++) {
        move();    
    }
    turnLeft();
    goToNiquia();
}
}


    public void goToNiquia() {
        // Moverse hacia el este para acceder a la línea principal
        for(int i = 0; i < 2; i++) {
            super.move();
        }
        
        // Ahora estamos cerca de la línea principal del metro
        turnRight(); // Girar hacia el sur
        for(int i = 0; i < 3; i++) {
            super.move(); // Moverse hacia el sur siguiendo la línea del metro
        }
        
        // Ahora debemos estar cerca de la estación San Antonio (intersección central)
        turnRight(); // Girar hacia el este
        for(int i = 0; i < 1; i++) {
            super.move(); // Moverse hacia el este
        }
        
        turnLeft(); // Girar hacia el norte
        // Moverse hacia el norte siguiendo la línea hasta Niquía
        for(int i = 0; i < 3; i++) {
            super.move();
        }

        turnRight();
        for(int i = 0; i < 2; i++) {
            super.move(); // Moverse hacia el este
        }

        turnLeft(); // Girar hacia el sur
        for (int i = 0; i < 3; i++) {
            super.move(); // Moverse hacia el sur
        }
        
        turnRight(); // Girar hacia el este
        for (int i = 0; i < 2; i++) {
            super.move(); // Moverse hacia el este
        }

        turnLeft(); // Girar hacia el norte
        for (int i = 0; i < 5; i++) {
            super.move(); // Moverse hacia el norte
        }
        
        turnLeft(); // Girar hacia el este
        for (int i = 0; i < 5; i++) {
            super.move(); // Moverse hacia el este
        }

        turnRight();
        for (int i = 0; i < 7; i++) {
            super.move(); // Moverse hacia el sur
        }

        turnRight();
        for (int i = 0; i < 3; i++) {
            super.move(); // Moverse hacia el este
        }

        turnLeft(); // Girar hacia el norte
        for (int i = 0; i < 6; i++) {
            super.move(); // Moverse hacia el norte
        }

        turnRight();
        super.move(); // Moverse hacia el este

        turnLeft(); // Girar hacia el sur
        for (int i = 0; i < 3; i++) {
            super.move(); // Moverse hacia el sur
        }

        turnRight();
        for (int i = 0; i < 2; i++) {
            super.move(); // Moverse hacia el este
        }

        turnLeft(); // Girar hacia el norte
        super.move();

        turnLeft(); // Girar hacia el este
        for (int i = 0; i < 3; i++) {
            super.move(); // Moverse hacia el este
        }

        turnLeft(); // Girar hacia el sur
        for (int i = 0; i < 3; i++) {
            super.move(); // Moverse hacia el sur
        }

        turnRight();
        super.move(); // Moverse hacia el este  

        turnLeft(); // Girar hacia el sur
        for (int i = 0; i < 6; i++) {
            super.move(); // Moverse hacia el sur
        }

        turnRight();
        for (int i = 0; i < 3; i++) {
            super.move(); // Moverse hacia el este
        }

        turnLeft(); // Girar hacia el norte
        for (int i = 0; i < 9; i++) {
            super.move(); // Moverse hacia el norte
        }
        
        turnLeft(); // Girar hacia el este
        for (int i = 0; i < 5; i++) {
            super.move(); // Moverse hacia el este
        }

        turnRight();
        for (int i = 0; i < 3; i++) {
            super.move(); // Moverse hacia el sur
        }
        
        turnRight();
        for (int i = 0; i < 2; i++) {
            super.move(); // Moverse hacia el este
        }

        turnLeft(); // Girar hacia el norte
        for (int i = 0; i < 3; i++) {
            super.move(); // Moverse hacia el norte
        }

        turnRight();
        for (int i = 0; i < 2; i++) {
            super.move(); // Moverse hacia el este
        }
        
        turnLeft(); // Girar hacia el sur
        for (int i = 0; i < 3; i++) {
            super.move(); // Moverse hacia el sur
        }

        turnRight();
        super.move(); // Moverse hacia el este  

        turnLeft(); // Girar hacia el sur
        for (int i = 0; i < 6; i++) {
            super.move(); // Moverse hacia el sur
        }

        turnRight();
        for (int i = 0; i < 3; i++) {
            super.move(); // Moverse hacia el este
        }

        turnLeft(); // Girar hacia el norte
        super.move(); // Moverse hacia el norte

        turnLeft(); // Girar hacia el norte
        super.move(); // Moverse hacia el norte
        
        // Llegamos a Niquía
        System.out.println("¡Llegue a Niquia!");
    }

    public void turnRight() {
        super.turnLeft();
        super.turnLeft();
        super.turnLeft();
    }
    // private void faceNorth() {
    // while (!facingNorth()) {
    //     turnLeft();
    //     }
    // }

    // private void faceEast() {
    //     while (!facingEast()) {
    //         turnLeft();
    //     }
    // }

    // private void faceSouth() {
    //     while (!facingSouth()) {
    //         turnLeft();
    //     }
    // }

    // private void faceWest() {
    //     while (!facingWest()) {
    //         turnLeft();
    //     }
    // }


    @Override
    public void run() {
        InitializeRoute();
        //goToNiquia(); // Ejecuta la ruta hacia Niquía
    }
}

 
class RacerB extends Robot implements Runnable {
    private int currentStreet; // Variable para rastrear la calle actual
    private int currentAvenue; // Variable para rastrear la avenida actual

    public RacerB(int street, int avenue, Direction direction, int beeps, Color color) {
        super(street, avenue, direction, beeps, color);
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
         // Moverse hacia el este para acceder a la línea principal
         for(int i = 0; i < 2; i++) {
            super.move();
        }
        
        // Ahora estamos cerca de la línea principal del metro
        turnRight(); // Girar hacia el sur
        for(int i = 0; i < 3; i++) {
            super.move(); // Moverse hacia el sur siguiendo la línea del metro
        }
        
        // Ahora debemos estar cerca de la estación San Antonio (intersección central)
        turnRight(); // Girar hacia el este
        for(int i = 0; i < 1; i++) {
            super.move(); // Moverse hacia el este
        }
        
        turnLeft(); // Girar hacia el norte
        // Moverse hacia el norte siguiendo la línea hasta Niquía
        for(int i = 0; i < 3; i++) {
            super.move();
        }

        turnRight();
        for(int i = 0; i < 2; i++) {
            super.move(); // Moverse hacia el este
        }

        turnLeft(); // Girar hacia el sur
        for (int i = 0; i < 3; i++) {
            super.move(); // Moverse hacia el sur
        }
        
        turnRight(); // Girar hacia el este
        for (int i = 0; i < 2; i++) {
            super.move(); // Moverse hacia el este
        }

        turnLeft(); // Girar hacia el norte
        for (int i = 0; i < 9; i++) {
            super.move(); // Moverse hacia el norte
        }

        turnRight();
        for (int i = 0; i < 4; i++) {
            super.move(); // Moverse hacia el sur
        }

        turnRight();
        super.move(); // Moverse hacia el este

        turnLeft(); // Girar hacia el sur
        for (int i = 0; i < 5; i++) {
            super.move(); // Moverse hacia el sur
        }

        turnRight();
        for (int i = 0; i < 2; i++) {
            super.move(); // Moverse hacia el este
        }

        turnLeft(); // Girar hacia el norte
        super.move();
        
        turnLeft(); // Girar hacia el este
        super.move(); // Moverse hacia el este
        
        // Simular llegada a la estación
        System.out.println("Llegué a San Javier");
    }

    public void turnRight() {
        super.turnLeft();
        super.turnLeft();
        super.turnLeft();
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
        World.setDelay(10); // Ajusta la velocidad de los robots

        // Crear dos robots
        Racer Karel1 = new Racer(32, 14, East, 0, Color.BLUE);
        //RacerB Karel2 = new RacerB(32, 14, East, 0, Color.GREEN);
        Racer Karel3 = new Racer(34, 10, East, 0, Color.BLUE);

        // Iniciar ambos robots como hilos
        Thread thread1 = new Thread(Karel1);
        //Thread thread2 = new Thread(Karel2);
        Thread thread3 = new Thread(Karel3);
        
        
        thread1.start();
        //thread2.start();
        thread3.start();
    }   
}

// public void goToLaEstrella() {
//     // Primero navegar desde la posición inicial hasta la línea principal del metro
//     // Asumiendo que comienza en la calle 35, avenida 1, mirando al este
    
//     // Moverse hacia el este para acceder a la línea principal
//     for(int i = 0; i < 2; i++) {
//         super.move();
//     }
    
//     // Ahora estamos cerca de la línea principal del metro
//     turnRight(); // Girar hacia el sur
//     for(int i = 0; i < 3; i++) {
//         super.move(); // Moverse hacia el sur siguiendo la línea del metro
//     }
    
//     // Ahora debemos estar cerca de la estación San Antonio (intersección central)
//     turnRight(); // Girar hacia el este
//     for(int i = 0; i < 1; i++) {
//         super.move(); // Moverse hacia el este
//     }
    
//     turnLeft(); // Girar hacia el norte
//     // Moverse hacia el norte siguiendo la línea hasta Niquía
//     for(int i = 0; i < 3; i++) {
//         super.move();
//     }

//     turnRight();
//     for(int i = 0; i < 2; i++) {
//         super.move(); // Moverse hacia el este
//     }

//     turnLeft(); // Girar hacia el sur
//     for (int i = 0; i < 3; i++) {
//         super.move(); // Moverse hacia el sur
//     }
    
//     turnRight(); // Girar hacia el este
//     for (int i = 0; i < 2; i++) {
//         super.move(); // Moverse hacia el este
//     }

//     turnLeft(); // Girar hacia el norte
//     for (int i = 0; i < 5; i++) {
//         super.move(); // Moverse hacia el norte
//     }
    
//     turnLeft(); // Girar hacia el este
//     for (int i = 0; i < 5; i++) {
//         super.move(); // Moverse hacia el este
//     }

//     turnRight();
//     for (int i = 0; i < 7; i++) {
//         super.move(); // Moverse hacia el sur
//     }

//     turnRight();
//     for (int i = 0; i < 3; i++) {
//         super.move(); // Moverse hacia el este
//     }

//     turnLeft(); // Girar hacia el norte
//     for (int i = 0; i < 6; i++) {
//         super.move(); // Moverse hacia el norte
//     }

//     turnRight();
//     super.move(); // Moverse hacia el este

//     turnLeft(); // Girar hacia el sur
//     for (int i = 0; i < 3; i++) {
//         super.move(); // Moverse hacia el sur
//     }

//     turnRight();
//     for (int i = 0; i < 2; i++) {
//         super.move(); // Moverse hacia el este
//     }

//     turnLeft(); // Girar hacia el norte
//     super.move();

//     turnLeft(); // Girar hacia el este
//     super.move(); // Moverse hacia el este
    
//     // Llegamos a La Estrella
//     System.out.println("¡Llegue a La Estrella!");
// }


// class Taller {
//     private static Semaphore semaforoTaller = new Semaphore(1, true); // Para controlar salida del taller
//     private static int contadorTrenes = 0;
    
//     public static void salirDelTaller(Robot tren) {
//         try {
//             semaforoTaller.acquire();
//             // Mover el tren a la posición de salida
//             while (tren.avenue() < 3) {
//                 tren.move();
//             }
//             contadorTrenes++;
//         } catch (InterruptedException e) {
//             e.printStackTrace();
//         } finally {
//             semaforoTaller.release();
//         }
//     }
// }

// class ControlTrenes {
//     private static Semaphore[] semaforosCalles = new Semaphore[36]; // Calles 1-35
//     private static Semaphore[] semaforosAvenidas = new Semaphore[22]; // Avenidas 1-21
    
//     static {
//         for (int i = 0; i < semaforosCalles.length; i++) {
//             semaforosCalles[i] = new Semaphore(1, true);
//         }
//         for (int i = 0; i < semaforosAvenidas.length; i++) {
//             semaforosAvenidas[i] = new Semaphore(1, true);
//         }
//     }
    
//     public static void moverTren(Robot tren, int nuevaCalle, int nuevaAvenida) {
//         try {
//             // Adquirir permisos para la nueva posición
//             semaforosCalles[nuevaCalle].acquire();
//             semaforosAvenidas[nuevaAvenida].acquire();
            
//             // Liberar permisos de la posición actual
//             if (tren.street() > 0 && tren.avenue() > 0) {
//                 semaforosCalles[tren.street()].release();
//                 semaforosAvenidas[tren.avenue()].release();
//             }
            
//             // Realizar el movimiento
//             tren.move();
            
//         } catch (InterruptedException e) {
//             e.printStackTrace();
//         }
//     }
// }