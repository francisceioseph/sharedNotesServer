import org.json.JSONObject;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.UUID;

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
            server.createUser("joano", "ana_gatinha@uol.com.br", "badiba200");
            server.authenticate("ana_gatinha@uol.com.br", "badiba200");

            JSONObject obj = new JSONObject();
            obj.put("noteID", UUID.randomUUID().toString());
            obj.put("title", "shanya");
            obj.put("text", "shanya2");

            server.createNote("ana_gatinha@uol.com.br", "badiba200", obj);

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
