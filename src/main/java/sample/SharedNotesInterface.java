package sample;

import org.json.JSONObject;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by francisco on 30/01/15.
 */
public interface SharedNotesInterface extends Remote {

    public boolean login(String email, String password) throws RemoteException;
    public boolean logout(String email) throws RemoteException;

    public boolean createUser(String jsonEncodedUserData) throws RemoteException;
    public String retrieveUser (String email) throws RemoteException;
    public boolean updateUser(String jsonEncodedUserData) throws RemoteException;
    public boolean deleteUser (String email) throws RemoteException;

    public boolean createNote (String email, String jsonEncodedNoteData) throws RemoteException;
    public String retrieveNote (String email, String noteID) throws RemoteException;
    public boolean updateNote (String email, String jsonEncodedData) throws RemoteException;
    public boolean deleteNote (String email, String noteID) throws RemoteException;

    public String listAllNotes(String email) throws RemoteException;



}
