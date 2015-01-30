import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by francisco on 30/01/15.
 */
public interface SharedNotesInterface extends Remote {

    public boolean authenticate(String email, String password) throws RemoteException;
    public boolean createUser(String username, String email, String password) throws RemoteException;
    public void disconnect(String email) throws RemoteException;


}
