package test;

import bITalino.BITalinoException;
import bITalino.Frame;
import data.ACC;
import data.EMG;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import bITalino.BITalino;
import pojos.Patient;

import javax.bluetooth.RemoteDevice;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BITalinoTest {
    private BITalino bitalino ;


    @Test
    public void testBITalinoConnection() {
        bitalino = null; //inicializamos objeto
        Frame[] frame; //almacena datos adquiridos del dispositivo
        try {
            bitalino = new BITalino();

            Vector<RemoteDevice> devices = bitalino.findDevices(); //is used to manage detected Bluetooth devices

            String macAddress = "20:17:11:20:51:27"; //se puede cambiar por IP Address de tu BITalino

            //Sampling rate, should be 10, 100 or 1000
            int SamplingRate = 100;
            bitalino.open(macAddress, SamplingRate);

            //check if isOpen() method is true (if is connected, this method returns true)
            assertTrue(bitalino.isOpen(), "Couldn't stablish connexion with BITalino.");

            int[] channelsToAcquire = {0, 5}; //0, EMG. 5, ACC
            bitalino.start(channelsToAcquire);

            //Objects EMG and ACC
            ACC acc = new ACC();
            EMG emg = new EMG();

            //Read in total 10000000 times
            for (int j = 0; j < 100; j++) {

                //Each time read a block of 10 samples
                int block_size = 10;
                frame = bitalino.read(block_size);


                assertNotNull(frame, "It has not received data");
                assertEquals(block_size, frame.length);


                //Print the samples
                for (int i = 0; i < frame.length; i++) {

                    acc.getTimestamp().add(j * block_size + i);
                    emg.getTimestamp().add(j * block_size + i);

                    emg.getSignalData().add(frame[i].analog[0]);
                    acc.getSignalData().add(frame[i].analog[1]);
                }
            }
            //stop acquisition
            bitalino.stop();

            bitalino.close();

        } catch (BITalinoException ex) {
            Logger.getLogger(Patient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Throwable ex) {
            fail("Error: " + ex.getMessage());
        }
    }
}

