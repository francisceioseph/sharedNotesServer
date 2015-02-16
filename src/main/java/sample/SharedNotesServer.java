package sample;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by francisco on 30/01/15.
 */
public class SharedNotesServer extends UnicastRemoteObject implements SharedNotesInterface {
    private final String USERLIST_PATH = "USERS/USERLIST.json";
    private final String USERLOGGED_LIST = "USERS/LOGGED_LIST.json";
    private final String NOTELIST_PATH = "NOTES/%s.json";

    protected SharedNotesServer() throws RemoteException {
        super();
    }

    @Override
    public boolean login(String email, String password) throws RemoteException {
        boolean success = false;
        JSONObject user = this.retrieveUserByEmail(email);
        String storedPassword = user.getString("password");

        if (password.equals(storedPassword) && !password.isEmpty()){
            success = this.checkin(email);
        }

        return success;
    }

    @Override
    public boolean logout(String email) throws RemoteException {
        boolean success = false;
        File usersDirectory = new File("USERS");

        if (!usersDirectory.exists())
            success = true;
        else{
            success = this.checkout(email);
        }
        return success;
    }

    @Override
    public boolean createUser(String jsonEncodedUserData) throws RemoteException {
        JSONObject userToCreate = new JSONObject(jsonEncodedUserData);
        JSONObject storedUser = this.retrieveUserByEmail(userToCreate.getString("email"));
        boolean success = false;

        if (storedUser == null){
            success = this.storeUser(userToCreate);
        }

        return success;
    }

    @Override
    public String retrieveUser(String email) throws RemoteException {
        JSONObject user = this.retrieveUserByEmail(email);
        String userEncodedData = null;

        if (user != null){
            user.remove("password");
            userEncodedData = user.toString(3);
        }

        return userEncodedData ;
    }

    @Override
    public boolean updateUser(String jsonEncodedUserData) throws RemoteException {
        boolean success;
        JSONObject userToUpdate = new JSONObject(jsonEncodedUserData);

        success = this.storeUser(userToUpdate);

        return success;
    }

    @Override
    public boolean deleteUser(String email) throws RemoteException {
        JSONObject usersDictionary = this.listAllUsers();

        usersDictionary.remove(email);

        return this.writeJSONFile(usersDictionary, this.USERLIST_PATH);
    }

    @Override
    public boolean createNote(String email, String jsonEncodedNoteData) throws RemoteException {
        JSONObject userNote = new JSONObject(jsonEncodedNoteData);
        return this.storeNote(email, userNote);
    }

    @Override
    public String retrieveNote(String email, String noteID) throws RemoteException {
        JSONObject notesFromUser = this.listAllNotesFromUser(email);
        JSONObject note = notesFromUser.getJSONObject(noteID);
        String noteData = null;

        if (note == null){
            noteData = note.toString(3);
        }

        return noteData;
    }

    @Override
    public boolean updateNote(String email, String jsonEncodedData) throws RemoteException {
        JSONObject userNote = new JSONObject(jsonEncodedData);
        return this.storeNote(email, userNote);
    }

    @Override
    public boolean deleteNote(String email, String noteID) throws RemoteException {
        JSONObject notesFromUser = this.listAllNotesFromUser(email);
        String path = String.format(this.NOTELIST_PATH, email);

        notesFromUser.remove(noteID);

        return this.writeJSONFile(notesFromUser, path);
    }

    @Override
    public String listAllNotes(String email) throws RemoteException {
        return this.listAllNotesFromUser(email).toString(3);
    }

    /*****************************************************************************************************************
     *
     * Internal server methods to make my life easy
     *
     ********************************************n*********************************************************************/

    private JSONObject listAllUsers(){
        JSONObject userDictionary = this.readJSONFile(this.USERLIST_PATH);

        if (userDictionary == null){
            userDictionary = new JSONObject();
        }


        return userDictionary;
    }

    private JSONObject listAllNotesFromUser(String email){
        File notesDirectory = new File("NOTES");
        JSONObject notesFromUser = null;

        if (!notesDirectory.exists()){
            notesDirectory.mkdir();
        }

        String notesPath = String.format(this.NOTELIST_PATH, email);
        notesFromUser = this.readJSONFile(notesPath);

        if (notesFromUser == null){
            notesFromUser = new JSONObject();
            this.writeJSONFile(notesFromUser, String.format(this.NOTELIST_PATH, email));
        }

        return notesFromUser;
    }

    private boolean checkin(String email){
        File usersDirectory = new File("USERS");
        JSONArray loggedUsers;

        if (!usersDirectory.exists())
            usersDirectory.mkdir();

        JSONObject loggedList = this.readJSONFile(this.USERLOGGED_LIST);

        if (loggedList == null){
            loggedList = new JSONObject();
            loggedUsers = new JSONArray();
        }
        else {
            loggedUsers = loggedList.getJSONArray("users");
        }

        loggedUsers.put(email);
        loggedList.put("users", loggedUsers);

        return this.writeJSONFile(loggedList, this.USERLOGGED_LIST);
    }

    private boolean checkout(String email) {

        JSONObject loggedList = this.readJSONFile(this.USERLOGGED_LIST);
        JSONArray loggedUsers = loggedList.getJSONArray("users");

        for (int i = 0; i < loggedUsers.length(); i++){
            if (loggedUsers.getString(i).equals(email)){
                loggedUsers.remove(i);
            }
        }

        loggedList.put("users", loggedUsers);
        return this.writeJSONFile(loggedList, this.USERLOGGED_LIST);
    }

    private boolean storeNote(String email, JSONObject note) {
        boolean success;
        JSONObject notesFromUser = this.listAllNotesFromUser(email);
        String notesPath = String.format(this.NOTELIST_PATH, email);

        if (notesFromUser == null){
            notesFromUser = new JSONObject();
        }

        notesFromUser.put(note.getString("ID"), note);
        success = this.writeJSONFile(notesFromUser, notesPath);

        return  success;
    }

    private boolean storeUser(JSONObject user){

        boolean success = false;
        JSONObject usersDictionary = this.readJSONFile(this.USERLIST_PATH);
        File userDirectory = new File("USERS");

        if (! userDirectory.exists()){
            userDirectory.mkdir();
        }

        if (usersDictionary == null){
            usersDictionary = new JSONObject();
        }

        usersDictionary.put(user.getString("email"), user);
        success = this.writeJSONFile(usersDictionary, this.USERLIST_PATH);

        return success;
    }

    private JSONObject retrieveUserByEmail(String email){
        JSONObject usersDictionary = this.readJSONFile(this.USERLIST_PATH);
        JSONObject user = null;

        if (usersDictionary != null){
            try {
                user = usersDictionary.getJSONObject(email);
            }
            catch (JSONException e){
                System.out.println("JSON parsing error...");
            }
        }
        return user;
    }

    /*
     * Realiza a leitura de um arquivo json
     * que contenha um array e retorna-o na
     * forma de um JSONObject.
     * Caso o arquivo não exista, retornará
     * um objeto nulo.
     *
     * O método é colocado como synchronized
     * para evitar condições de corrida.
     *
     */
    private synchronized JSONObject readJSONFile(String path) {
        File file = new File(path);
        JSONObject jsonObject = null;

        try {
            URI uri = file.toURI();
            InputStream fileStream = uri.toURL().openStream();
            JSONTokener Tokener = new JSONTokener(fileStream);
            jsonObject = new JSONObject(Tokener);
            fileStream.close();
        } catch (MalformedURLException e) {
            System.out.println("URL mal formada...");
        }
        catch (IOException e) {
            System.out.println("Arquivo JSON nâo encontrado...");
        }

        return jsonObject;
    }

    /*
     * Realiza a escrita de um JSONArray em um arquivo
     * de texto afim de ser utilizado posteriormente.
     *
     * O método é colocado como sinchronized para evitar
     * condições de corrida.
     */
    private synchronized boolean writeJSONFile(JSONObject jsonObject, String filename) {
        boolean success = false;
        BufferedWriter writer;
        File file;

        file = new File(filename);

        try {

            writer = new BufferedWriter(new FileWriter(file));
            writer.write(jsonObject.toString(3));
            writer.flush();
            writer.close();
            success = true;

        } catch (IOException e) {
            System.out.println("Erro na escrita do JSON.");
        }

        return success;
    }

}
