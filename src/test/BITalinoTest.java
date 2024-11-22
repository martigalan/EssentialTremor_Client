package test;

import bITalino.BITalinoException;
import bITalino.Frame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import bITalino.BITalino;

import javax.bluetooth.RemoteDevice;
import java.util.Vector;

public class BITalinoTest {
    private BITalino bitalino;

    @BeforeEach
    public void setUp() {
        bitalino = new BITalino();
    }

    @Test
    public void testBITalinoConnection() {
        try {
            // Looks for devices
            Vector<RemoteDevice> devices = bitalino.findDevices();
            assertNotNull(devices, "There must be devices, the list should not be null.");
            assertFalse(devices.isEmpty(), "No devices found.");

            String macAddress = "20:17:11:20:51:27";

            int samplingRate = 100;
            bitalino.open(macAddress, samplingRate);

            // There should not be exceptions thrown
            assertDoesNotThrow(() -> bitalino.open(macAddress, samplingRate),
                    "Can't open connexion with BITalino.");

            // Read to verify that it works
            int blockSize = 10;
            Frame[] frames = bitalino.read(blockSize);

            assertNotNull(frames, "No messages received.");
            assertTrue(frames.length > 0, "Block is empty.");

            // Sees if any value is valid
            assertTrue(frames[0].analog.length > 0, "No valid values.");

            bitalino.close();
            assertDoesNotThrow(() -> bitalino.close(), "Error when closing connexion.");

        } catch (BITalinoException ex) {
            fail("Error with the connexion of BITalino: " + ex.getMessage());
        } catch (Exception ex) {
            fail("Error: " + ex.getMessage());
        }
    }
}

