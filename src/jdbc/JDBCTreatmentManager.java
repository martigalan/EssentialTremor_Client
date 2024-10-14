package jdbc;

import ifaces.TreatmentManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import Pojos.Treatment;

public class JDBCTreatmentManager implements TreatmentManager {
    private ConnectionManager cM;

    public JDBCTreatmentManager(ConnectionManager cManager) {
        this.cM = cManager;
    }

    @Override
    public void addTreatment() {
        for (Treatment treatment : Treatment.values()) {
            try {
                String sql = "INSERT INTO treatment (type, description) SELECT ?, ? WHERE NOT EXISTS (SELECT 1 FROM treatment WHERE type = ? LIMIT 1)";
                PreparedStatement prep = cM.getConnection().prepareStatement(sql);
                prep.setString(1, treatment.name()); // Inserts the name of the treatment (e.g., SURGERY, PROPRANOLOL)
                prep.setString(2, treatment.getDescription()); // Inserts the description
                prep.setString(3, treatment.name()); // Checks if the treatment type already exists
                prep.executeUpdate();
                prep.close();
            } catch (SQLException ex) {
                Logger.getLogger(JDBCTreatmentManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

