import java.net.InetAddress;

public class Player {
    private final InetAddress inetAddress;
    private final String name;
    private boolean ready;

    public Player(InetAddress inetAddress, String name) {
        this.inetAddress = inetAddress;
        this.name = name;
        this.ready = false;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public String getName() {
        return name;
    }
}
