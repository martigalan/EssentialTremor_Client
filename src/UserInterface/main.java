package UserInterface;
import jdbc.ConnectionManager;
import jdbc.JDBCStateManager;
import jdbc.JDBCTreatmentManager;

public class main {
    public static ConnectionManager connectionManager;
    public static JDBCStateManager stateManager;
    public static JDBCTreatmentManager treatmentManager;

    public static void main(String[] args) {
        connectionManager= new ConnectionManager();
        stateManager = new JDBCStateManager(connectionManager);
        treatmentManager = new JDBCTreatmentManager(connectionManager);

        //add state and treatment to tables
        stateManager.addState();
        treatmentManager.addTreatment();
    }
}
