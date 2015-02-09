package sample;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by francisco on 30/01/15.
 */
public class SharedNotesServer extends UnicastRemoteObject implements SharedNotesInterface {
    private final String USERLIST_PATH = "USERS/USERLIST.json";
    private final String NOTELIST_PATH = "USERSNOTES/%s.json";

    protected SharedNotesServer() throws RemoteException {
        super();
    }

    @Override
    public boolean createUser(String username, String email, String password) throws RemoteException {
        boolean success = false;
        JSONObject user;

        user = this.retrieveUserByEmail(email);

        if (user == null){
            user = new JSONObject();
            user.put("name", username);
            user.put("email", email);
            user.put("password", password);

            success = this.storeUser(user);
        }

        return success;
    }

    @Override
    public String authenticate(String email, String password) throws RemoteException {
        return null;
    }

    @Override
    public void disconnect(String userToken) throws RemoteException {

    }

    @Override
    public JSONObject listAllNotes(String userToken) throws RemoteException {
        return null;
    }

    @Override
    public boolean createNote(String userToken, JSONObject note) throws RemoteException {
        return false;
    }

    @Override
    public boolean updateNote(String userToken, JSONObject note) throws RemoteException {
        return false;
    }

    @Override
    public boolean deleteNote(String userToken, JSONObject note) throws RemoteException {
        return false;
    }

    /*****************************************************************************************************************
     *
     * Internal server methods to make my life easy
     *
     *****************************************************************************************************************/

    private boolean storeNote(JSONObject user, JSONObject note, JSONObject notes) {
        boolean success = false;
        String noteID = note.getString("noteID");
        String filePath = String.format(this.NOTELIST_PATH, user.getString("userID"));
        File notesDirectory = new File("USERSNOTES");

        if (!notesDirectory.exists()){
            notesDirectory.mkdir();
        }

        notes.put(noteID, note);
        success = this.writeJSONFile(notes, filePath);
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

    /*
     * Cria um JSONObject usando as informações
     * passadas como parâmetros.
     */
    private JSONObject createJSONUser(String username, String email, String password) {
        JSONObject user = new JSONObject();
        String userUUID = UUID.randomUUID().toString();

        user.put("userID", userUUID);
        user.put("username", username);
        user.put("email", email);
        user.put("password", password);

        return user;
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
            JSONTokener tokener = new JSONTokener(fileStream);
            jsonObject = new JSONObject(tokener);
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
