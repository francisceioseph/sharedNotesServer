/**
 * Created by Francisco José A. C. Souza on 30/01/15.
 */
public enum Singleton {
    INSTANCE;
    private String nameServerIPAddess;
    private int nameServerPort;

    public int getNameServerPort() {
        return nameServerPort;
    }

    public void setNameServerPort(int nameServerPort) {
        this.nameServerPort = nameServerPort;
    }

    public String getNameServerIPAddess() {
        return nameServerIPAddess;
    }

    public void setNameServerIPAddess(String nameServerIPAddess) {
        this.nameServerIPAddess = nameServerIPAddess;
    }
}
