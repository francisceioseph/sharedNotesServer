package sample;

import org.json.JSONObject;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by francisco on 30/01/15.
 */
public interface SharedNotesInterface extends Remote {

    public boolean createUser(String username, String email, String password) throws RemoteException;
    public String retrievePublicUserInformation (String email, String password) throws RemoteException;

//    public boolean updateUser(String username, String email, String password) throws RemoteException;
//    public boolean deleteUser(String username, String email, String password) throws RemoteException;

    public String authenticate(String email, String password) throws RemoteException;
    public void disconnect(String userToken) throws RemoteException;

    public JSONObject listAllNotes(String userToken) throws RemoteException;
    public boolean createNote(String userToken, JSONObject note) throws RemoteException;
    public boolean updateNote(String userToken, JSONObject note) throws RemoteException;
    public boolean deleteNote(String userToken, JSONObject note) throws RemoteException;
}
