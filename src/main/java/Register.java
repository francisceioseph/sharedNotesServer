import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/**
 * Created by francisco on 30/01/15.
 */
public class Register {
    private SharedNotesServer server;

    public Register(SharedNotesServer server) {
        this.server = server;
    }

    public boolean startRMIRegistry(){
        boolean success = false;
        try {

            LocateRegistry.createRegistry(Singleton.INSTANCE.getNameServerPort());
            success = true;

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return success;
    }

    public boolean register(){
        boolean success = false;

        try{
            Naming.rebind(String.format("//%s:%d/shared_notes", Singleton.INSTANCE.getNameServerIPAddess(), Singleton.INSTANCE.getNameServerPort()), this.server);
            server.createUser("joao", "joao@uol.com.br", "badiba200");
            success = true;
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return success;
    }
}
