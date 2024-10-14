package Data;

public class EMG {

//TODO see how we store this

        private String signalData;  //almacenar datos de la señal EMG
        private String filename;    //nombre archivo donde se almacenan los datos (guardarlo en txt)
        private String path;        //ruta del archivo
        private String timestamp;   //fecha y hora

        public EMG(String signalData, String filename, String path, String timestamp) {
            this.signalData = signalData;
            this.filename = filename;
            this.path = path;
            this.timestamp = timestamp;
        }

        public String getSignalData() {
            return signalData;
        }

        public void setSignalData(String signalData) {
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

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return "EMG [filename=" + filename + ", path=" + path + ", timestamp=" + timestamp + "]";
        }
}