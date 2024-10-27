package data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EMG {

//TODO see how we store this

        private List<Integer> signalData;  //almacenar datos de la señal EMG
        private String filename;    //nombre archivo donde se almacenan los datos (guardarlo en txt)
        private String path;        //ruta del archivo
        private List<Integer> timestamp;   //fecha y hora

        public EMG(List<Integer> signalData, String filename, String path, List<Integer> timestamp) {
            this.signalData = signalData;
            this.filename = filename;
            this.path = path;
            this.timestamp = timestamp;
        }

    public EMG() {
        this.signalData = new ArrayList<>();
        this.filename = null;
        this.path = null;
        this.timestamp = new ArrayList<>();
    }

    public List<Integer> getSignalData() {
            return signalData;
        }

    public void setSignalData(List<Integer> signalData) {
            this.signalData = signalData;
        }

    public String getFilename() {
            return filename;
        }

    public void setFilename(String filename) {
            this.filename = filename;
        }

    public String getPath() {
            return path;
        }

    public void setPath(String path) {
            this.path = path;
        }

    public List<Integer> getTimestamp() {
            return timestamp;
        }

    public void setTimestamp(List<Integer> timestamp) {
            this.timestamp = timestamp;
        }

    @Override
    public String toString() {
        return "EMG [filename=" + filename + ", path=" + path + ", timestamp=" + timestamp + "]";
    }
    public static String listToString(List<Integer> list) {
        return list.stream()
                .map(String::valueOf)  // Convierte cada Integer a String
                .collect(Collectors.joining(","));  // Junta tod separado por comas
    }

}