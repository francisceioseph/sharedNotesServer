package sample;

import sample.SharedNotesServer;
import sample.SingletonServer;

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
            SingletonServer.INSTANCE.registry = LocateRegistry.createRegistry(SingletonServer.INSTANCE.getNameServerPort());
            success = true;

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return success;
    }

    public boolean register(){
        boolean success = false;

        try{

            SingletonServer.INSTANCE.registry.rebind(String.format("rmi://%s:%d/SharedNotes",
                            SingletonServer.INSTANCE.getNameServerIPAddess(),
                            SingletonServer.INSTANCE.getNameServerPort()),
                    this.server);

            success = true;
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
        return success;
    }
}
