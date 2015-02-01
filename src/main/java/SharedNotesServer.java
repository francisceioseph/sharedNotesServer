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

        Object soughtUser;
        JSONObject user;
        JSONObject usersDictionary;

        //lê o arquivo com os dados dos usuários

        usersDictionary = this.readJSONFile(this.USERLIST_PATH);

        user = this.createJSONUser(username, email, password);

        /*
         * Caso o arquivo json lido não contenha nenhum dado
         * cria-se um novo JSONObject para representar as
         * informações dos usuários no sistema.
         *
         */
        if (usersDictionary == null) {
            usersDictionary = new JSONObject();
        }

        //Testa se o usuário em questão já possui um registro.

        try {
            soughtUser = usersDictionary.get(email);
        }catch (JSONException e){
            soughtUser = null;
        }

        //Caso não possua um registro, ele será cadastrado.
        //Do contrário, não.

        if (soughtUser == null){
            System.out.println("Store new user complete!");
            this.storeUser(user, usersDictionary);
            success = true;
        }
        else
            System.out.println("Store new user can't complete!");

        return success;
    }

    @Override
    public boolean authenticate(String email, String password) throws RemoteException {
        boolean success = false;

        JSONObject usersRegister = this.readJSONFile(this.USERLIST_PATH);
        JSONObject userData;

        try{
            userData = (JSONObject) usersRegister.get(email);
            String archivedPassword = (String)userData.get("password");

            if (archivedPassword.equals(password)){
                success = true;
                System.out.println("User authenticated");
            }
            else{
                System.out.println("User not authenticated");
            }

        }
        catch (JSONException e){
            System.out.print("No user found...");
        }

        return success;
    }

    @Override
    public void disconnect(String email) throws RemoteException {

    }



    @Override
    public JSONObject listAllNotes(String email, String password) throws RemoteException {
        JSONObject notes = null;
        boolean isAuthenticated = this.authenticate(email, password);

        if(isAuthenticated){

            JSONObject user = this.retrieveUserByEmail(email);

            String notesFileName = String.format(this.NOTELIST_PATH, user.getString("userID"));

            notes = this.readJSONFile(notesFileName);
        }
        else{

            System.out.println("Erro de Autenticação");

        }

        return notes;
    }



    @Override
    public boolean createNote(String email, String password, JSONObject note) throws RemoteException {
        boolean success = false;
        JSONObject notes = this.listAllNotes(email, password);
        JSONObject user = this.retrieveUserByEmail(email);

        if (user != null) {

            if (notes == null){
                notes = new JSONObject();
            }

            success = this.storeNote(user, note, notes);
        }

        return success;
    }

    @Override
    public boolean updateNote(String email, String password, JSONObject note) throws RemoteException {
        boolean success = false;
        JSONObject notes = this.listAllNotes(email, password);
        JSONObject user = this.retrieveUserByEmail(email);

        if(user != null){
            notes.remove(note.getString("noteID"));
            success = this.storeNote(user, note, notes);
        }

        return success;
    }

    @Override
    public boolean deleteNote(String email, String password, JSONObject note) throws RemoteException {
        boolean success = false;
        JSONObject notes = this.listAllNotes(email, password);
        JSONObject user = this.retrieveUserByEmail(email);

        if(user != null) {
            notes.remove(note.getString("noteID"));
            String filename = String.format(this.NOTELIST_PATH, user.getString("userID"));
            success = this.writeJSONFile(notes, filename);
        }
            return success;
    }

    @Override
    public JSONObject retrieveNote(String email, int indexNote) throws RemoteException {
        return null;
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

    private void storeUser(JSONObject user, JSONObject usersDictionary){

        File userDirectory = new File("USERS");

        if (! userDirectory.exists()){
            userDirectory.mkdir();
        }

        usersDictionary.put(user.getString("email"), user);
        this.writeJSONFile(usersDictionary, this.USERLIST_PATH);
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
