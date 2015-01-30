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

/**
 * Created by francisco on 30/01/15.
 */
public class SharedNotesServer extends UnicastRemoteObject implements SharedNotesInterface {

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

        usersDictionary = this.readJSONFile("users.json");
        user = this.createJSONUser(username, email, password);

        /*
         * Caso o arquivo json lido não contenha nenhum dado
         * cria-se um novo JSONObject para representar as
         * informações dos usuários no sistema.
         *
         */
        if (usersDictionary == null)
            usersDictionary = new JSONObject();

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

        JSONObject usersRegister = this.readJSONFile("users.json");
        JSONObject userData;

        try{
            userData = (JSONObject) usersRegister.get(email);
            String archivedPassword = (String)userData.get("password");

            if (archivedPassword.equals(password))
            {
                success = true;
                System.out.println("User authenticated");
            }
            else
            {
                System.out.println("User not authenticated");
            }

        }
        catch (JSONException e){
            System.out.print("No user found...");
        }

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
    public boolean createNote(String username, JSONObject note) throws RemoteException {
        return false;
    }

    @Override
    public boolean updateNote(String username, JSONObject note) throws RemoteException {
        return false;
    }

    @Override
    public boolean deleteNote(String username, JSONObject note) throws RemoteException {
        return false;
    }

    @Override
    public boolean deleteNote(String username, int indexNote) throws RemoteException {
        return false;
    }

    @Override
    public JSONObject retrieveNote(String username, int indexNote) throws RemoteException {
        return null;
    }

    private void storeUser(JSONObject user, JSONObject usersDictionary){
        usersDictionary.put(user.getString("email"), user);
        this.writeJSONFile(usersDictionary, "users.json");
    }

    /*
     * Cria um JSONObject usando as informações
     * passadas como parâmetros.
     */
    private JSONObject createJSONUser(String username, String email, String password) {
        JSONObject user = new JSONObject();

        user.put("username", username);
        user.put("email", email);
        user.put("password", password);

        return user;
    }

    /*
     * Realiza a leitura de um arquivo json
     * que contenha um array e retorna-o na
     * forma de um JSONArray.
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
            JSONTokener tokener = new JSONTokener(uri.toURL().openStream());
            jsonObject = new JSONObject(tokener);
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
            writer.write(jsonObject.toString(0));
            writer.flush();
            writer.close();
            success = true;

        } catch (IOException e) {
            System.out.println("Erro na escrita do JSON.");
        }

        return success;
    }

}
