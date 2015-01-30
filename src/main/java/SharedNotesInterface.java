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

    public ArrayList<JSONArray> listAllNotes(String username) throws RemoteException;
    public boolean createNote(JSONObject note) throws RemoteException;
    public boolean updateNote(JSONObject note) throws RemoteException;
    public boolean deleteNote(JSONObject note) throws RemoteException;
    public boolean deleteNote(int indexNote) throws RemoteException;
    public JSONObject retrieveNote(int indexNote) throws RemoteException;

}
