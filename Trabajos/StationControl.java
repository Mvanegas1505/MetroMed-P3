import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StationControl {
    private static final Object stationLock = new Object();
    private static volatile boolean sanAntonioBOcupada = false;
    private static volatile boolean cisnerosOcupada = false;
    private static volatile int currentTrainInSanAntonioB = -1;
    private static volatile boolean isTrainLeavingSanAntonioB = false;
    private static final Map<Integer, Boolean> trainDirections = new ConcurrentHashMap<>();

    public static void registerTrainDirection(int trainId, boolean towardsSanAntonio) {
        trainDirections.put(trainId, towardsSanAntonio);
    }

    public static void waitForSanAntonioB(int trainId, int currentStreet, int currentAvenue) throws InterruptedException {
        synchronized (stationLock) {
            while (sanAntonioBOcupada || isTrainLeavingSanAntonioB || 
                   TrainControl.isPositionOccupied(14, 13) || 
                   TrainControl.isPositionOccupied(14, 14)) {
                stationLock.wait();
            }
            sanAntonioBOcupada = true;
            currentTrainInSanAntonioB = trainId;
        }
    }

    public static void enterSanAntonioB(int trainId) {
        synchronized (stationLock) {
            cisnerosOcupada = false;
            stationLock.notifyAll();
        }
    }

    public static void prepareLeaveSanAntonioB(int trainId) {
        synchronized (stationLock) {
            if (currentTrainInSanAntonioB == trainId) {
                isTrainLeavingSanAntonioB = true;
            }
        }
    }

    public static void completeLeaveSanAntonioB(int trainId) {
        synchronized (stationLock) {
            if (currentTrainInSanAntonioB == trainId) {
                sanAntonioBOcupada = false;
                isTrainLeavingSanAntonioB = false;
                currentTrainInSanAntonioB = -1;
                stationLock.notifyAll();
            }
        }
    }
} 