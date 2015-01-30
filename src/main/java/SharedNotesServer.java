import org.json.JSONArray;
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
        JSONObject user;
        JSONArray arrayOfUsers;

        arrayOfUsers = this.readJSONFile("users.json");

        if (arrayOfUsers == null){

            user = this.createJSONUser(username, email, password);
            arrayOfUsers = new JSONArray();
            arrayOfUsers.put(user);

            this.writeJSONFile(arrayOfUsers);
        }
        else{
            System.out.println("Arquivo JSON encontrado...");
        }
        return success;
    }

    private JSONObject createJSONUser(String username, String email, String password) {
        JSONObject user = new JSONObject();

        user.put("username", username);
        user.put("email", email);
        user.put("password", password);

        return user;
    }

    private JSONArray readJSONFile(String path) {
        File file = new File(path);
        JSONArray array = null;

        try {
            URI uri = file.toURI();
            JSONTokener tokener = new JSONTokener(uri.toURL().openStream());
            array = new JSONArray(tokener);
        } catch (MalformedURLException e) {
            System.out.println("URL mal formada...");
        }
        catch (IOException e) {
            System.out.println("Arquivo JSON nâo encontrado...");
        }

        return array;
    }

    private boolean writeJSONFile(JSONArray arrayOfUsers) {
        boolean success = false;
        BufferedWriter writer;
        File file;

        file = new File("users.json");

        try {

            writer = new BufferedWriter(new FileWriter(file));
            writer.write(arrayOfUsers.toString(0));
            writer.close();
            success = true;

        } catch (IOException e) {
            System.out.println("Erro na escrita do JSON.");
        }

        return success;
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
}
