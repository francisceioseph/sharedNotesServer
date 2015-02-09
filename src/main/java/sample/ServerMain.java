package sample;

import sample.Register;
import sample.SharedNotesServer;
import sample.SingletonServer;

import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;

/**
 * Created by francisco on 30/01/15.
 */
public class ServerMain {
    public static void main (String args[])
    {
        SingletonServer.INSTANCE.setNameServerIPAddess("localhost");
        SingletonServer.INSTANCE.setNameServerPort(56789);

        Register register = null;

        //if (System.getSecurityManager() == null) System.setSecurityManager(new RMISecurityManager());

        try {
            register = new Register(new SharedNotesServer());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        register.startRMIRegistry();
        boolean s = register.register();

        System.out.print(s);
    }
}
