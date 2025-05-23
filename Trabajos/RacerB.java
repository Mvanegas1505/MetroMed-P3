import kareltherobot.*;
import java.awt.Color;

public class RacerB extends Racer {
    private final int trainId;
    
    public RacerB(int trainId, int street, int avenue, Direction direction, int beeps, Color color) {
        super(trainId, street, avenue, direction, beeps, color);
        this.trainId = trainId;
        StationControl.registerTrainDirection(trainId, true);
    }

    private boolean enEstacionExtrema() {
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
        if (currentStreet == 13 && currentAvenue == 12) {
            try {
                StationControl.waitForSanAntonioB(trainId, currentStreet, currentAvenue);
                move();
                move();
                move();
                move();
                
                StationControl.enterSanAntonioB(trainId);
                Thread.sleep(3000);
                
                StationControl.prepareLeaveSanAntonioB(trainId);
                turnLeft();
                turnLeft();
                
                move();
                move();
                move();
                
                StationControl.completeLeaveSanAntonioB(trainId);
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        }
        
        for(int i = 0; i < 2; i++) {
            moveCheckBeeper();
            if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        }
        
        turnLeft();
        for(int i = 0; i < 5; i++) {
            moveCheckBeeper();
            if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        }
        
        turnRight();
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        
        turnLeft();
        for(int i = 0; i < 8; i++) {
            moveCheckBeeper();
            if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
            if (i == 5) {
                try {
                    StationControl.waitForSanAntonioB(trainId, currentStreet, currentAvenue);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        
        turnLeft();
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        
        turnRight();
        moveCheckBeeper();
        if (!MiPrimerRobot.startSignal.get() && enEstacionExtrema()) return;
        turnLeft();
        turnLeft();

        try {
            StationControl.enterSanAntonioB(trainId);
            Thread.sleep(3000);
            StationControl.prepareLeaveSanAntonioB(trainId);
            move();
            move();
            move();
            StationControl.completeLeaveSanAntonioB(trainId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public void sanAntonioToSanJavier() {
        try {
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
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private void moveToSanJavier() {
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

    @Override
    protected void irAlTaller() {
        irAlTallerB();
    }

    private void irAlTallerB() {
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
} 