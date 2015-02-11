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
    private final String USERSCOOKIES_PATH = "USERS/CookieS.json";
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
            user = this.createJSONUser(username, email, password);
            success = this.storeUser(user);
        }

        return success;
    }

    @Override
    public String authenticate(String email, String password) throws RemoteException {
        String cookieValue = null;
        JSONObject user = this.retrieveUserByEmail(email);
        JSONObject userCookie = this.retrieveCookieByEmail(email);

        //TODO retrieve Cookies file until attribute a new UUID to the logging user.

        if (userCookie == null) {

            if (user != null) {
                String storedPassword = user.getString("password");

                if (storedPassword.equals(password)) {
                    cookieValue = UUID.randomUUID().toString();
                    this.storeUserCookie(cookieValue, email);
                }
            }
        }
        else
        {
            cookieValue = userCookie.getString(email);
        }

        return cookieValue;
    }

    @Override
    public void disconnect(String userCookie) throws RemoteException {

    }

    @Override
    public JSONObject listAllNotes(String userCookie) throws RemoteException {
        return null;
    }

    @Override
    public boolean createNote(String userCookie, JSONObject note) throws RemoteException {
        return false;
    }

    @Override
    public boolean updateNote(String userCookie, JSONObject note) throws RemoteException {
        return false;
    }

    @Override
    public boolean deleteNote(String userCookie, JSONObject note) throws RemoteException {
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

    private boolean storeUserCookie(String userCookie, String email) {
        boolean success = false;
        JSONObject usersCookies = this.readJSONFile(this.USERSCOOKIES_PATH);
        JSONObject Cookie = new JSONObject();

        File userDirectory = new File("USERS");

        if (! userDirectory.exists()){
            userDirectory.mkdir();
        }

        if (usersCookies == null){
            usersCookies = new JSONObject();
        }

        Cookie.put(email, userCookie);
        usersCookies.put(email, Cookie);

        success = this.writeJSONFile(usersCookies, this.USERSCOOKIES_PATH);

        return success;
    }

    /*
     * Cria um JSONObject usando as informações
     * passadas como parâmetros.
     */
    private JSONObject createJSONUser(String username, String email, String password) {
        JSONObject user = new JSONObject();
        String userUUID = UUID.randomUUID().toString();
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

    private JSONObject retrieveCookieByEmail(String email) {
        JSONObject CookiesDictionary = this.readJSONFile(this.USERSCOOKIES_PATH);
        JSONObject Cookie = null;

        if (CookiesDictionary != null){
            try {
                Cookie = CookiesDictionary.getJSONObject(email);
            }
            catch (JSONException e){
                System.out.println("JSON parsing error...");
            }
        }

        return Cookie;
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
