package Data;

public class ACC {

//TODO see how we store this

        private String signalData;
        private String filename;
        private String path;
        private String timestamp;

        public ACC(String signalData, String filename, String path, String timestamp) {
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
            return "ACC [filename=" + filename + ", path=" + path + ", timestamp=" + timestamp + "]";
        }
}
