import kareltherobot.*;
import java.awt.Color;

public class Racer extends Robot implements Runnable {
    protected int currentStreet;
    protected int currentAvenue;
    private int trainId;
    protected boolean isAtStation = false;

    public Racer(int trainId, int street, int avenue, Direction direction, int beeps, Color color) {
        super(street, avenue, direction, beeps, color);
        this.trainId = trainId;
        this.currentStreet = street;
        this.currentAvenue = avenue;
        World.setupThread(this);
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
        int nextStreet = currentStreet;
        int nextAvenue = currentAvenue;
        if (facingNorth()) nextStreet++;
        else if (facingSouth()) nextStreet--;
        else if (facingEast()) nextAvenue++;
        else if (facingWest()) nextAvenue--;

        while (!TrainControl.reservePosition(trainId, nextStreet, nextAvenue)) {
            try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
        }
        TrainControl.freePosition(currentStreet, currentAvenue);
        super.move();
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
            try { Thread.sleep(3000); } catch (InterruptedException e) { }
        }

        TrainControl.freePosition(currentStreet, currentAvenue);
        super.move();
        currentStreet = nextStreet;
        currentAvenue = nextAvenue;
    }

    protected void irAlTaller() {
        // Implementación base que puede ser sobreescrita por las subclases
    }

    @Override
    public void run() {
        // Implementación base que debe ser sobreescrita por las subclases
    }
} 