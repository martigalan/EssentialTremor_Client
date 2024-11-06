package pojos;

import java.io.Serializable;

public class User implements Serializable {

    /**
     * Identifier to serialize User
     */
    private static final long serialVersionUID = 2L;
    /**
     * Username to login into the application
     */
    public String username;
    /**
     * Password to login into the application
     */
    public byte[] password;

    /**
     * Constructor
     * @param username username identification
     * @param password password identification
     */
    public User(String username, byte[] password) {
        this.username=username;
        this.password=password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }
    /**
     * String representation of User
     * @return String to represent User
     */
    @Override
    public String toString() {
        return "User{" + "username=" + username + ", password=" + password;
    }
}
