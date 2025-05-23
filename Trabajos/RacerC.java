import kareltherobot.*;
import java.awt.Color;

public class RacerC extends Racer {
    public RacerC(int trainId, int street, int avenue, Direction direction, int beeps, Color color) {
        super(trainId, street, avenue, direction, beeps, color);
    }

    private boolean enEstacionExtrema() {
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
        while (getAvenue() > 1) { 
            move();
        }
        turnLeft();
        move();
        turnLeft();
        while (getAvenue() < 14) { 
            move();
        }
        turnRight();
        move();
        move();
        turnLeft();
        goToLaEstrella();
    }

    private void goToLaEstrella() {
        for (int i = 0; i < 2; i++) {
            move();
        }

        turnRight();
        for (int i = 0; i < 3; i++) {
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
    }

    public void Niquia_Estrella() {
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
    }

    public void Estrella_Niquia() {
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
    }

    @Override
    protected void irAlTaller() {
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