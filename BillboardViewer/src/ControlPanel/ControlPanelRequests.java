package ControlPanel;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Properties;

/***
 * A class that manages requests to the server and translates
 * the data into a format fit for the main form.
 */
public class ControlPanelRequests {

    private String sessionToken;
    private Mock mock;
    private String host;
    private int port;
    public ControlPanelRequests(String propertyFilePath) throws IOException{
        sessionToken = "";
        mock = new Mock();
        Properties props = new Properties();
        FileInputStream fileInputStream = new FileInputStream(propertyFilePath + "/db.props");
        props.load(fileInputStream);
        fileInputStream.close();

        host = props.getProperty("server.hostname");
        port = Integer.parseInt(props.getProperty("server.port"));
        System.out.println(host);
        System.out.println(port);
    }

    /**
     * Attempts to log into the server with the given user credentials.
     * @param user a string of the username
     * @param pass a string of the hashed password
     * @return a boolean of whether the given credentials were valid
     */
    public boolean login(String user, String pass){
        try {
            if (sessionToken.length() > 0){
                return false;
            }
            Socket socket = new Socket(host, port);
            OutputStream outputStream = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            //sessionToken = mock.loginRequest(user, pass);
            dataOutputStream.writeUTF("login");
            dataOutputStream.flush();

            ArrayList<String> dataToSend = new ArrayList<>();
            dataToSend.add(user);
            dataToSend.add(String.valueOf(pass.hashCode()));
            ObjectOutputStream send = new ObjectOutputStream(socket.getOutputStream());
            send.writeObject(dataToSend);
            dataOutputStream.flush();

            InputStream inputStream = socket.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            String response = dataInputStream.readUTF();

            if (response.equals("Username not found") || response.equals("Wrong password")) {
                return false;
            } else {
                sessionToken = response;
            }
            System.out.println("Logged in");
            socket.close();
            return true;
        } catch (Exception e){
            return false;
        }
    }

    /**
     * Attempts to send a request to the server to create
     * a new billboard with the given Billboard details.
     * @param billboard the billboard to create
     * @return a boolean of whether it was created or not
     */
    public boolean createBillboard(Billboard billboard){
        try (Socket socket = new Socket(host, port)) {
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF("create billboard");
            dataOutputStream.flush();
            dataOutputStream.writeUTF(sessionToken);
            dataOutputStream.flush();

            try {
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                String response = dataInputStream.readUTF();

                if (response.equals("User does not have correct permission")) {
                    System.out.println(response);
                    socket.close();
                    return false;
                } else if (response.equals("User does have correct permission")) {
                    ArrayList<String> dataToSend = new ArrayList<>();
                    dataToSend.add(billboard.name);
                    dataToSend.add(billboard.backgroundColour);
                    dataToSend.add(billboard.message);
                    dataToSend.add(billboard.messageColour);
                    if (billboard.pictureData.length() > 0) {
                        dataToSend.add(billboard.pictureData);
                    } else {
                        dataToSend.add(billboard.pictureURL);
                    }
                    dataToSend.add(billboard.infoMessage);
                    dataToSend.add(billboard.infoColour);
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    objectOutputStream.writeObject(dataToSend);
                    dataOutputStream.flush();
                }

                response = dataInputStream.readUTF();
                System.out.println(response);
                if (response.contains("name already in use")) {
                    return false;
                }
            } catch (Exception error) {
                error.printStackTrace();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Attempts to send a request to the server
     * to edit an existing billboard.
     * @param billboard the given billboard details
     * @return a boolean on whether it worked or not
     */
    public boolean editBillboard(Billboard billboard){
        Socket socket = null;
        try {
            socket = new Socket(host, port);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF("edit billboard");
            dataOutputStream.flush();
            dataOutputStream.writeUTF(sessionToken);
            dataOutputStream.flush();

            ArrayList<String> dataToSend = new ArrayList<>();
            dataToSend.add(billboard.name);
            dataToSend.add(billboard.backgroundColour);
            dataToSend.add(billboard.message);
            dataToSend.add(billboard.messageColour);
            if (billboard.pictureData != null) {
                if (billboard.pictureData.length() > 0) {
                    dataToSend.add(billboard.pictureData);
                } else {
                    dataToSend.add(billboard.pictureURL);
                }
            } else {
                dataToSend.add(billboard.pictureURL);
            }
            dataToSend.add(billboard.infoMessage);
            dataToSend.add(billboard.infoColour);
            System.out.println(dataToSend);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(dataToSend);
            dataOutputStream.flush();

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            String response = dataInputStream.readUTF();
            System.out.println(response);
            return true;
        } catch (IOException error){
            error.printStackTrace();
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException ignore){}
        }
    }

    /**
     * Sends a request to the server to delete the
     * billboard with given name.
     * @param name the name of the billboard to delete
     * @return whether it was successful or not
     */
    public boolean deleteBillboard(String name){
        Socket socket = null;
        try {
            socket = new Socket(host, port);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF("delete billboard");
            dataOutputStream.flush();
            dataOutputStream.writeUTF(sessionToken);
            dataOutputStream.flush();
            dataOutputStream.writeUTF(name);
            dataOutputStream.flush();

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            String response = dataInputStream.readUTF();
            if (response.equals("can delete")){
                return true;
            } else {
                return false;
            }
        } catch (IOException error){
            error.printStackTrace();
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException ignore){}
        }
    }

    /**
     * Sends a request to the server to get the list
     * of all known billboards.
     * @return an arraylist of billboards
     */
    public ArrayList<Billboard> getBillboards() {
        Socket socket = null;
        try {
            socket = new Socket(host, port);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF("list billboards");
            dataOutputStream.flush();
            dataOutputStream.writeUTF(sessionToken);
            dataOutputStream.flush();

            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            ArrayList<ArrayList<String>> response = (ArrayList<ArrayList<String>>) objectInputStream.readObject();
            ArrayList<Billboard> billboards = new ArrayList<Billboard>();
            for (var billboard : response){
                Billboard tempBillboard = new Billboard();
                tempBillboard.fromStrings(billboard.get(0), // Name
                        billboard.get(2), // BG colour
                        billboard.get(3), // Message
                        billboard.get(4), // Message colour
                        billboard.get(5), // Picture URL
                        billboard.get(5), // Picture Data
                        billboard.get(6), // Info
                        billboard.get(7), // Info colour
                        billboard.get(1));// Creator
                billboards.add(tempBillboard);
            }
            return billboards;
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<Billboard>();
        } finally {
            try {
                socket.close();
            } catch (IOException ignore){}
        }
    }

    /**
     * Sends a request to the server to get
     * the details of an individual billboard.
     * @param name the name of the billboard
     * @return a billboard
     */
    public Billboard getBillboard(String name) {
        Socket socket = null;
        try {
            socket = new Socket(host, port);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF("billboard info");
            dataOutputStream.flush();
            dataOutputStream.writeUTF(sessionToken);
            dataOutputStream.flush();
            dataOutputStream.writeUTF(name);
            dataOutputStream.flush();

            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            ArrayList<String> response = (ArrayList<String>)objectInputStream.readObject();
            String[] vals = response.toString().substring(1, response.toString().length()-1).split(",");
            Billboard tempBillboard = new Billboard();
            tempBillboard.fromStrings(vals[0].substring(1), // Name
                    vals[2].substring(1), // BG colour
                    vals[3].substring(1), // Message
                    vals[4].substring(1), // Message colour
                    vals[5].substring(1), // Picture URL
                    vals[5].substring(1), // Picture Data
                    vals[6].substring(1), // Info
                    vals[7].substring(1, vals[7].length()-1), // Info colour
                    vals[1]);// Creator
            return tempBillboard;
        } catch (Exception error){
            return new Billboard();
        } finally {
            try {
                socket.close();
            } catch (IOException ignore){}
        }

    }

    public Schedule getSchedule() {
        try (Socket socket = new Socket(host, port)){
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF("view schedule");
            dataOutputStream.flush();
            dataOutputStream.writeUTF(sessionToken);
            dataOutputStream.flush();

            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            Object schedule = objectInputStream.readObject();

            Schedule schedule1 = new Schedule();
            ArrayList<ArrayList<Object>> scheduleArray = (ArrayList<ArrayList<Object>>) schedule;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (ArrayList<Object> entry : scheduleArray){
                String date = (String)entry.get(3);
                int duration = Integer.parseInt((String)entry.get(2));
                schedule1.add(getBillboard((String)entry.get(0)),
                        LocalDateTime.parse(date.substring(0, 19), formatter),
                        duration,
                        (String)entry.get(4));
            }

            return schedule1;
        } catch (Exception e){
            System.out.println(e);
            return new Schedule();
        }
    }

    public boolean scheduleBillboard(ArrayList<Object> billboardSchedule){
        try (Socket socket = new Socket(host, port)){
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF("schedule billboard");
            dataOutputStream.flush();
            dataOutputStream.writeUTF(sessionToken);
            dataOutputStream.flush();

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(billboardSchedule);

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            return dataInputStream.readUTF().equals("billboard scheduled successfully");
        } catch (Exception error){
            return false;
        }
    }

    public boolean unscheduleBillboard(ArrayList<Object> billboardToUnschedule) {
        try (Socket socket = new Socket(host, port)){
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF("remove billboard from schedule");
            dataOutputStream.flush();
            dataOutputStream.writeUTF(sessionToken);
            dataOutputStream.flush();

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(billboardToUnschedule);

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            return dataInputStream.readUTF().equals("billboard successfully deleted");
        } catch (Exception error){
            return false;
        }
    }

    public ArrayList<String> getUsers() {
        try (Socket socket = new Socket(host, port)){
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF("list users");
            dataOutputStream.flush();
            dataOutputStream.writeUTF(sessionToken);
            dataOutputStream.flush();

            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            return (ArrayList<String>) objectInputStream.readObject();
        } catch (Exception e){
            return new ArrayList<>();
        }
    }

    public boolean createUser(String username, ArrayList<String> perms, String pass){
        try (Socket socket = new Socket(host, port)){
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF("register user");
            dataOutputStream.flush();
            dataOutputStream.writeUTF(sessionToken);
            dataOutputStream.flush();

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(perms);
            //objectOutputStream.flush();
            ArrayList<String> tempArray = new ArrayList<>();
            tempArray.add(username);
            tempArray.add(pass);
            //tempArray.addAll(perms);
            objectOutputStream.writeObject(tempArray);
            objectOutputStream.flush();

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            return dataInputStream.readUTF().contains("User registered");
        } catch (Exception error){
            return false;
        }
    }

    public boolean deleteUser(String userToDelete) {
        try (Socket socket = new Socket(host, port)){
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF("delete user");
            dataOutputStream.flush();
            dataOutputStream.writeUTF(sessionToken);
            dataOutputStream.flush();

            dataOutputStream.writeUTF(userToDelete);
            dataOutputStream.flush();

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            return dataInputStream.readUTF().contains("user delete successful");
        } catch (Exception error){
            return false;
        }
    }

    enum Perms {
        CREATE_BILLBOARD,
        EDIT_BILLBOARD,
        SCHEDULE_BILLBOARD,
        EDIT_USER
    }
    public ArrayList<String> getPermissions(String user) {
        try (Socket socket = new Socket(host, port)){
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF("get user permission");
            dataOutputStream.flush();
            dataOutputStream.writeUTF(sessionToken);
            dataOutputStream.flush();
            dataOutputStream.writeUTF(user);
            dataOutputStream.flush();

            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            ArrayList<String> response = (ArrayList<String>) objectInputStream.readObject();
            ArrayList<String> perms = new ArrayList<>();
            for (int i=0; i<response.size(); i++){
                if (response.get(i).equals("1")) {
                    switch (i){
                        case 1:
                            perms.add("Create Billboards");
                            break;
                        case 2:
                            perms.add("Edit All Billboards");
                            break;
                        case 3:
                            perms.add("Schedule Billboards");
                            break;
                        case 4:
                            perms.add("Edit Users");
                            break;
                    }
                }
            }
            return perms;
        } catch (Exception e){
            return new ArrayList<String>();
        }
    }

    public boolean setPermissions(String user, ArrayList<String> perms){
        try (Socket socket = new Socket(host, port)){
            System.out.println(perms.toString());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF("set user permission");
            dataOutputStream.flush();
            dataOutputStream.writeUTF(sessionToken);
            dataOutputStream.flush();
            dataOutputStream.writeUTF(user);
            dataOutputStream.flush();

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(perms);
            objectOutputStream.flush();

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            return dataInputStream.readUTF().contains("Permission changed, user: ");
        } catch (Exception error){
            return false;
        }
    }

    public boolean setPassword(String user, String pass){
        try (Socket socket = new Socket(host, port)){
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF("edit user password");
            dataOutputStream.flush();
            dataOutputStream.writeUTF(sessionToken);
            dataOutputStream.flush();

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ArrayList<String> temp = new ArrayList<>();
            temp.add(user);
            temp.add(pass);
            objectOutputStream.writeObject(temp);
            objectOutputStream.flush();

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            String response = dataInputStream.readUTF();
            return response.equals("password change successful!");
        } catch (Exception error){
            return false;
        }
    }

    /**
     * Sends a request to the server to log out
     * the user with given username.
     * @return a boolean of whether they were successfully logged out or not
     */
    public boolean logout(String username) {
        try {
            if (username.length() > 0) {
                Socket socket = new Socket(host, port);
                OutputStream outputStream = socket.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

                dataOutputStream.writeUTF("logout");
                dataOutputStream.flush();

                dataOutputStream.writeUTF(sessionToken);
                dataOutputStream.flush();
                sessionToken = "";
                System.out.println("Logged out");
                socket.close();
            }
            return true;
        } catch (Exception e){
            return false;
        }
    }
}
