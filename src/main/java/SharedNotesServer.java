import org.json.JSONArray;
import org.json.JSONObject;

import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by francisco on 30/01/15.
 */
public class SharedNotesServer implements SharedNotesInterface {

    @Override
    public boolean createUser(String username, String email, String password) throws RemoteException {
        return false;
    }

    @Override
    public boolean authenticate(String email, String password) throws RemoteException {
        return false;
    }

    @Override
    public void disconnect(String email) throws RemoteException {

    }

    @Override
    public ArrayList<JSONArray> listAllNotes(String username) throws RemoteException {
        return null;
    }

    @Override
    public boolean createNote(JSONObject note) throws RemoteException {
        return false;
    }

    @Override
    public boolean updateNote(JSONObject note) throws RemoteException {
        return false;
    }

    @Override
    public boolean deleteNote(JSONObject note) throws RemoteException {
        return false;
    }

    @Override
    public boolean deleteNote(int indexNote) throws RemoteException {
        return false;
    }

    @Override
    public JSONObject retrieveNote(int indexNote) throws RemoteException {
        return null;
    }
}
