import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TrainControl {
    private static int[][] occupation = new int[37][22];
    private static final int[][] ESTACIONES = {
        {35,19}, {34,19}, {31,16}, {31,17}, {27,15}, {27,16}, 
        {24,13}, {24,14}, {20,11}, {20,12}, {19,14}, {18,14},
        {16,16}, {16,17}, {14,16}, {14,17}, {12,16}, {12,17},
        {11,15}, {10,15}, {9,13}, {9,14}, {6,13}, {6,14},
        {3,12}, {3,13}, {2,11}, {1,11}, {16,1}, {16,2},
        {15,5}, {14,5}, {14,9}, {13,9}, {14,12}, {13,12}, {14,15}
    };
    private static final Lock lock = new ReentrantLock();

    public static boolean isStation(int street, int avenue) {
        for (int[] estacion : ESTACIONES) {
            if (estacion[0] == street && estacion[1] == avenue) {
                return true;
            }
        }
        return false;
    }

    public static boolean reservePosition(int trainId, int street, int avenue) {
        if (!isValidPosition(street, avenue)) {
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

    public static boolean isPositionOccupied(int street, int avenue) {
        if (!isValidPosition(street, avenue)) {
            return true;
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