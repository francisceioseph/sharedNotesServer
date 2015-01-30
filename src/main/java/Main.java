import java.rmi.RemoteException;

/**
 * Created by francisco on 30/01/15.
 */
public class Main {
    public static void main (String args[])
    {
        Singleton.INSTANCE.setNameServerIPAddess("localhost");
        Singleton.INSTANCE.setNameServerPort(55555);

        Register register = null;
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
