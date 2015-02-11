package sample;

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

        try {
            register = new Register(new SharedNotesServer());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        register.startRMIRegistry();
        boolean s = register.register();

        if (s)
            System.out.println("Server UP!!!");
        else
            System.out.println("Server ERROR!!!");

    }
}
