import org.json.JSONArray;
import org.json.JSONObject;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by francisco on 30/01/15.
 */
public interface SharedNotesInterface extends Remote {

    //How to generate UUID
    //String uniqueID = UUID.randomUUID().toString();

    public boolean createUser(String username, String email, String password) throws RemoteException;
    public boolean authenticate(String email, String password) throws RemoteException;
    public void disconnect(String email) throws RemoteException;

    public JSONObject listAllNotes(String email, String password) throws RemoteException;
    public boolean createNote(String email, String password, JSONObject note) throws RemoteException;
    public boolean updateNote(String email, JSONObject note) throws RemoteException;
    public boolean deleteNote(String email, JSONObject note) throws RemoteException;
    public boolean deleteNote(String email, int indexNote) throws RemoteException;
    public JSONObject retrieveNote(String email, int indexNote) throws RemoteException;

}
