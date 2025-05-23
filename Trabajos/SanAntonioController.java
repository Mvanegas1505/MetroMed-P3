public class SanAntonioController {
    private static final Object stationLock = new Object();
    
    // Estado de ocupación de las estaciones críticas
    private static volatile boolean sanAntonioBOcupada = false;
    private static volatile boolean cisnerosOcupada = false;
    
    /**
     * Solicita acceso a la estación San Antonio B
     * Si está ocupada, el tren esperará en Cisneros
     */
    public static void waitForSanAntonioB(int trainId) {
        synchronized (stationLock) {
            // Esperar mientras San Antonio B esté ocupada
            while (sanAntonioBOcupada) {
                try {
                    System.out.println("Tren " + trainId + " esperando en Cisneros por San Antonio B ocupada");
                    stationLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            // Marcar ambas estaciones durante transición
            cisnerosOcupada = true;
            System.out.println("Tren " + trainId + " en tránsito Cisneros → San Antonio B");
        }
    }
    
    /**
     * Confirma llegada a San Antonio B
     */
    public static void arrivedAtSanAntonioB(int trainId) {
        synchronized (stationLock) {
            sanAntonioBOcupada = true;
            cisnerosOcupada = false;  // Liberar Cisneros
            stationLock.notifyAll();  // Notificar a trenes esperando
            System.out.println("Tren " + trainId + " ha llegado a San Antonio B");
        }
    }
    
    /**
     * Libera la estación San Antonio B al partir
     */
    public static void releaseSanAntonioB(int trainId) {
        synchronized (stationLock) {
            sanAntonioBOcupada = false;
            stationLock.notifyAll();
            System.out.println("Tren " + trainId + " ha liberado San Antonio B");
        }
    }
    
    /**
     * Verifica si Cisneros está ocupada
     */
    public static boolean isCisnerosOcupada() {
        synchronized (stationLock) {
            return cisnerosOcupada;
        }
    }
    
    /**
     * Verifica si San Antonio B está ocupada
     */
    public static boolean isSanAntonioBOcupada() {
        synchronized (stationLock) {
            return sanAntonioBOcupada;
        }
    }
}