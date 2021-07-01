package Server.server_package;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import Server.Billboard_viewer.billboard_viewer;
import Server.Database.connection_db;
import Server.user_authentication_package.User_authentication_exception;
import Server.user_authentication_package.session_token;
import Server.user_authentication_package.User_authentication;
import Server.control_panel.control_pannel_server;
public class Server {

    public static int  port_number() throws IOException {
        Properties props = new Properties();
        FileInputStream in = null;

        in = new FileInputStream("db.props");
        props.load(in);
        in.close();

        // specify the data source, username and password
        String host_name = props.getProperty("server.hostname");
        String port_name = props.getProperty("server.port");

        return Integer.parseInt(port_name);

    }


        public static  void main(String[] args) throws IOException, User_authentication_exception, SQLException {
        billboard_viewer billboard_viewer = new billboard_viewer();
        connection_db.check_table(); //creates table if it does not exist
        connection_db connect = new connection_db(); //connect to db
        //create a admin if their is no user
        User_authentication creaate_user = new User_authentication();
        ArrayList<String> possible_permission = new ArrayList<String>(Arrays.asList( "Create Billboards", "Edit All Billboards", "Schedule Billboards", "Edit Users")); //list of all permission
        try { //creates a default user with all the permissions on start up
            if(  creaate_user.show_all_user().size() == 0) {
                String hashed_password = Integer.toString("password".hashCode()); //uses same hash as control pannel to send password over the network
                creaate_user.register_user("admin", possible_permission, hashed_password);
            }
        } catch (User_authentication_exception user_authentication_exception) {
            user_authentication_exception.printStackTrace();
        }


        //read file from the db.props to get the port

        ServerSocket serverSocket = new ServerSocket(port_number()); //receives connection on this port
        session_token token = new session_token(); //stores token

        //initiating some variables that's are used throughout the class
        String user;
        List<List<String>> billboard_info;
        User_authentication permission = new User_authentication();
        String permission_required;
        control_pannel_server control_pannel_serve_obj = new control_pannel_server();
        String billboardname;
        String username;
        String bg_colour;
        String mssg ;
        String mssg_colour ;
        String picture ;
        String into_mssg ;
        String info_colour;

        for(;;) {
            Socket socket = serverSocket.accept(); //accepts the connection
            InputStream inputStream = socket.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            String message = dataInputStream.readUTF();
            System.out.println("The message sent from the socket was: " + message);

            String request = message; //the message the server gets
            //sends array
            switch (request){//
                case "billboard viewer request":
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    List<String> obj = (List<String>) billboard_viewer.return_billboards();
                    System.out.println(obj);
                    out.writeObject(obj);
                    out.flush();
                    break;

                case "login":
                    //recieve user name and password as list
                    //mock_user_authentication login = new mock_user_authentication();//
                    User_authentication loging_db =  new User_authentication();

                    try {
                        ObjectInputStream recieve = new ObjectInputStream(socket.getInputStream());
                        ArrayList<String> data_recieved = (ArrayList<String>) recieve.readObject();//recieves username and password
                        username = data_recieved.get(0);
                        String password = data_recieved.get(1);
                        System.out.println(loging_db.log_in(username,password));

                        if (loging_db.log_in(username,password).equals("Log in sucessful")){//send the token and successful
                            token.new_token(username);//creates a new token for this user
                            String generate_token = "random token";
                            //send the token back
                            OutputStream outputStream = socket.getOutputStream();
                            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                            dataOutputStream.writeUTF( token.getUser_token().get(username));
                            dataOutputStream.flush(); // send the token
                        }
                        else{
                            OutputStream outputStream = socket.getOutputStream();
                            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                            dataOutputStream.writeUTF(loging_db.log_in(username,password) );
                            dataOutputStream.flush(); //send the message if the user can not log in

                        }
                    } catch (Exception e) { // if there is error
                        e.printStackTrace();
                    }
                    break;

                case "list billboards"://sends the schedule db
                    inputStream = socket.getInputStream();
                    dataInputStream = new DataInputStream(inputStream);
                    message = dataInputStream.readUTF();
                    try {
                        user = token.getKeyByValue(token.getUser_token(),message);//gets the user using the token
                    if (user == null ){
                        System.out.println("list billboard: user not logged in"); //checks if the token is valid or not
                        throw new User_authentication_exception("list billboard: user not logged in");
                    }
                    System.out.println("Username: " + user + " token: " + message);
                    //get
                        connect = new connection_db();
                        connect.setRequest("view_all");
                        connect.table("billboard");
                        //send
                        out = new ObjectOutputStream(socket.getOutputStream());
                        List<List<String>> list_billboards = connect.DB_Connect();
                        System.out.println(list_billboards);
                        out.writeObject(list_billboards);
                        out.flush();
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        OutputStream outputStream = socket.getOutputStream();
                        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                        dataOutputStream.writeUTF(e.toString());
                        dataOutputStream.flush();
                    }
                    break;

                case "billboard info":
                    inputStream = socket.getInputStream();
                    dataInputStream = new DataInputStream(inputStream);
                    message = dataInputStream.readUTF();
                     user = token.getKeyByValue(token.getUser_token(),message);//gets the user using the token
                    try {
                        if (user == null ){
                            System.out.println("billboard info: user not logged in"); //checks if the token is valid or not
                            throw new User_authentication_exception("billboard info user not logged in");
                        }
                        System.out.println("Username: " + user + " token: " + message);
                        inputStream = socket.getInputStream();
                        dataInputStream = new DataInputStream(inputStream);
                        String billboard_name = dataInputStream.readUTF();//recieve the name to send from client
                        //get
                        connect = new connection_db();
                        connect.setRequest("select_where");
                        connect.table("billboard");
                        connect.setCond_one(String.format("billboard_name = '%s'", billboard_name));
                        //send
                        out = new ObjectOutputStream(socket.getOutputStream());
                        billboard_info = connect.DB_Connect();
                        System.out.println(billboard_info);
                        out.writeObject(billboard_info);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        OutputStream outputStream = socket.getOutputStream();
                        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                        dataOutputStream.writeUTF(e.toString());
                        dataOutputStream.flush();
                    }
                    break;

                case "create billboard":
                    permission_required = "Create Billboards";
                    //recieve billboard name and token
                    inputStream = socket.getInputStream();
                    dataInputStream = new DataInputStream(inputStream);
                    message = dataInputStream.readUTF();
                    user = token.getKeyByValue(token.getUser_token(),message);//gets the user using the token
                    try{

                    if (user == null ){
                        System.out.println("create billboard: user not logged in"); //checks if the token is valid or not
                        throw new User_authentication_exception("create billboard: user not logged in");
                    }
                    System.out.println("Username: " + user + " token: " + message);
                    //check for the user permission
                    if(!permission.check_user_permission(user,permission_required)){
                        throw new User_authentication_exception("Permission error");
                    }}catch (Exception e){
                        e.printStackTrace();
                        //send the error to the client
                        OutputStream outputStream = socket.getOutputStream();
                        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                        dataOutputStream.writeUTF("User does not have correct permission");
                        dataOutputStream.flush();
                        break;
                    }
                    //send message if the user have correct permission
                    System.out.println("User does have correct permission");
                    OutputStream outputStream = socket.getOutputStream();
                    DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                    dataOutputStream.writeUTF("User does have correct permission");
                    dataOutputStream.flush();
                    //receive the billboard content from the client in form of a list
                    try {
                        ObjectInputStream recieve = new ObjectInputStream(socket.getInputStream());
                        ArrayList<String> data_recieved = (ArrayList<String>) recieve.readObject();//recieves username and password
                         billboardname =  data_recieved.get(0);
                         username  =  user; //the user who made the billboard
                         bg_colour =  data_recieved.get(1);
                         mssg =  data_recieved.get(2);
                         mssg_colour =  data_recieved.get(3);
                         picture =  data_recieved.get(4);
                         into_mssg = data_recieved.get(5);
                         info_colour =  data_recieved.get(6);
                        // control_pannel_serve_obj.create_bilboard(billboard_name);
                         control_pannel_serve_obj = new control_pannel_server();
                         String test =  control_pannel_serve_obj.create_bilboard(billboardname, username, bg_colour, mssg, mssg_colour, picture, into_mssg, info_colour);
                         //send message to the client
                         outputStream = socket.getOutputStream();
                         dataOutputStream = new DataOutputStream(outputStream);
                        dataOutputStream.writeUTF(test);
                        dataOutputStream.flush();

                        System.out.println(test);
                    }catch (Exception e){
                        e.printStackTrace();
                        break;
                    }
                    break;
                case "edit billboard":
                    inputStream = socket.getInputStream();
                    dataInputStream = new DataInputStream(inputStream);
                    message = dataInputStream.readUTF();
                    user = token.getKeyByValue(token.getUser_token(),message);//gets the user using the token
                    //receive the billboard content from the client in form of a list
                    try {
                        if (user == null ){
                            System.out.println("list billboard: user not logged in"); //checks if the token is valid or not
                            throw new User_authentication_exception("list billboard: user not logged in");
                        }
                        System.out.println("Username: " + user + " token: " + message);
                        ObjectInputStream recieve = new ObjectInputStream(socket.getInputStream());
                        ArrayList<String> data_recieved = (ArrayList<String>) recieve.readObject();//recieves username and password
                        billboardname =  data_recieved.get(0);
                        username  =  user; //the user who made the billboard
                        bg_colour =  data_recieved.get(1);
                        mssg =  data_recieved.get(2);
                        mssg_colour =  data_recieved.get(3);
                        picture =  data_recieved.get(4);
                        into_mssg = data_recieved.get(5);
                        info_colour =  data_recieved.get(6);
                        // control_pannel_serve_obj.create_bilboard(billboard_name);
                        control_pannel_serve_obj = new control_pannel_server();
                        String test =  control_pannel_serve_obj.edit_billboard(billboardname, username, bg_colour, mssg, mssg_colour, picture, into_mssg, info_colour);
                          System.out.println(test);
                        //sending the message to the client conforming that the process was complete
                        outputStream = socket.getOutputStream();
                        dataOutputStream = new DataOutputStream(outputStream);
                        dataOutputStream.writeUTF("Billboard edited");
                        dataOutputStream.flush();
                        //sends the error to the client
                    }catch (Exception e){
                        e.printStackTrace();
                        outputStream = socket.getOutputStream();
                        dataOutputStream = new DataOutputStream(outputStream);
                        dataOutputStream.writeUTF(e.toString());
                        dataOutputStream.flush();
                    }
                    break;

                case "delete billboard":
                    inputStream = socket.getInputStream();
                    dataInputStream = new DataInputStream(inputStream);
                    message = dataInputStream.readUTF();
                    user = token.getKeyByValue(token.getUser_token(),message);//gets the user using the token
                    try{
                        if (user == null ){
                            System.out.println("delete billboard: user not logged in"); //checks if the token is valid or not
                            throw new User_authentication_exception("delete billboard: user not logged in");
                        }
                        System.out.println("Username: " + user + " token: " + message);
                        inputStream = socket.getInputStream();
                        dataInputStream = new DataInputStream(inputStream);
                        billboardname = dataInputStream.readUTF();//recieve the name to send from client
                        username  =  user; //the user who made the billboard
                        String test =  control_pannel_serve_obj.delete_billboard(username, billboardname);
                        //send message to client
                        outputStream = socket.getOutputStream();
                        dataOutputStream = new DataOutputStream(outputStream);
                        dataOutputStream.writeUTF(test);
                        dataOutputStream.flush();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        outputStream = socket.getOutputStream();
                        dataOutputStream = new DataOutputStream(outputStream);
                        dataOutputStream.writeUTF(e.toString());
                        dataOutputStream.flush();
                    }
                    break;


                case "view schedule":
                    inputStream = socket.getInputStream();
                    dataInputStream = new DataInputStream(inputStream);
                    message = dataInputStream.readUTF();
                    user = token.getKeyByValue(token.getUser_token(),message);

                    System.out.println(user);

                    try {
                        if (user == null ){
                            System.out.println("list user : user not logged in");
                            throw new User_authentication_exception("list user : user not logged in");
                        }
                        System.out.println("Username: " + user + " token: " + message);

                        out = new ObjectOutputStream(socket.getOutputStream());
                        out.writeObject(control_pannel_serve_obj.view_schedule(user));
                        out.flush();
                        System.out.println(control_pannel_serve_obj.view_schedule(user));
                    }catch (Exception e){
                        e.printStackTrace();
                        outputStream = socket.getOutputStream();
                        dataOutputStream = new DataOutputStream(outputStream);
                        dataOutputStream.writeUTF(e.toString());
                        dataOutputStream.flush();
                    }
                    break;

                case "schedule billboard":
                    inputStream = socket.getInputStream();
                    dataInputStream = new DataInputStream(inputStream);
                    message = dataInputStream.readUTF();
                    user = token.getKeyByValue(token.getUser_token(),message);//gets the user using the token
                    //recieve billboard and time in array
                    System.out.println(user);
                    try {
                        if (user == null ){
                            System.out.println("schedule billboard : user not logged in"); //checks if the token is valid or not
                            throw new User_authentication_exception("schedule billboard : user not logged in");
                        }
                        System.out.println("Username: " + user + " token: " + message);

                        //recieve from client
                        ObjectInputStream recieve = new ObjectInputStream(socket.getInputStream());
                        ArrayList<String> data_recieved = (ArrayList<String>) recieve.readObject();//recieves username and password
                        billboardname = data_recieved.get(0);
                        String duration = data_recieved.get(1);
                        String time = data_recieved.get(2);
                        String reccurance = data_recieved.get(3);
                        //send acknowledgement
                        outputStream = socket.getOutputStream();
                        dataOutputStream = new DataOutputStream(outputStream);
                        dataOutputStream.writeUTF((control_pannel_serve_obj.schedule_billboard(user,billboardname,duration,time,reccurance)));
                        dataOutputStream.flush();
                    }catch (Exception e){
                        e.printStackTrace();
                        outputStream = socket.getOutputStream();
                        dataOutputStream = new DataOutputStream(outputStream);
                        dataOutputStream.writeUTF(e.toString());
                        dataOutputStream.flush();
                    }
                    break;

                case "remove billboard from schedule":
                    inputStream = socket.getInputStream();
                    dataInputStream = new DataInputStream(inputStream);
                    message = dataInputStream.readUTF();
                    user = token.getKeyByValue(token.getUser_token(),message);//gets the user using the token
                    //recieve billboard and time in array
                    System.out.println(user);
                    try {
                        if (user == null ){
                            System.out.println("remove billboard from schedule : user not logged in"); //checks if the token is valid or not
                            throw new User_authentication_exception("remove billboard from schedule : user not logged in");
                        }
                        System.out.println("Username: " + user + " token: " + message);
                        ObjectInputStream recieve = new ObjectInputStream(socket.getInputStream());
                        ArrayList<String> data_recieved = (ArrayList<String>) recieve.readObject();//recieves username and password
                        billboardname = data_recieved.get(0);
                        String time = data_recieved.get(1);
                        //send acknowledgement
                        outputStream = socket.getOutputStream();
                        dataOutputStream = new DataOutputStream(outputStream);
                        dataOutputStream.writeUTF(control_pannel_serve_obj.remove_billboard_schedule(user,billboardname,time));
                        dataOutputStream.flush();
                    }catch (Exception e){
                        e.printStackTrace();
                        outputStream = socket.getOutputStream();
                        dataOutputStream = new DataOutputStream(outputStream);
                        dataOutputStream.writeUTF(e.toString());
                        dataOutputStream.flush();
                    }
                    break;

                case "list users":
                    inputStream = socket.getInputStream();
                    dataInputStream = new DataInputStream(inputStream);
                    message = dataInputStream.readUTF();
                    user = token.getKeyByValue(token.getUser_token(),message);//gets the user using the token
                    System.out.println(user);
                    try {
                        if (user == null ){
                            System.out.println("list user : user not logged in"); //checks if the token is valid or not
                            throw new User_authentication_exception("list user : user not logged in");
                        }
                        System.out.println("Username: " + user + " token: " + message);

                         out = new ObjectOutputStream(socket.getOutputStream());
                        out.writeObject(control_pannel_serve_obj.list_users(user)); //send to client
                        out.flush();
                        System.out.println(control_pannel_serve_obj.list_users(user));
                    }catch (Exception e){
                        e.printStackTrace();
                        outputStream = socket.getOutputStream();
                        dataOutputStream = new DataOutputStream(outputStream);
                        dataOutputStream.writeUTF(e.toString());
                        dataOutputStream.flush();
                    }
                    break;

                case "register user":
                    inputStream = socket.getInputStream();
                    dataInputStream = new DataInputStream(inputStream);
                    message = dataInputStream.readUTF();
                    user = token.getKeyByValue(token.getUser_token(),message);//gets the user using the token
                    System.out.println(user);
                    //recieve permissions first
                    try {
                        if (user == null ){
                            System.out.println("regester user : user not logged in"); //checks if the token is valid or not
                            throw new User_authentication_exception("regester user : user not logged in");
                        }
                        System.out.println("Username: " + user + " token: " + message);
                        ObjectInputStream recieve = new ObjectInputStream(socket.getInputStream());
                        ArrayList<String> permission_recieved = (ArrayList<String>) recieve.readObject();//recieves username and password
                     //   ObjectInputStream recieve_sec_arraay = new ObjectInputStream(socket.getInputStream());
                        ArrayList<String> username_password = (ArrayList<String>) recieve.readObject();//recieves username and password
                        username = username_password.get(0);
                        String password = username_password.get(1);
                        control_pannel_serve_obj.create_user(user, username, permission_recieved, password);
                        outputStream = socket.getOutputStream();
                        dataOutputStream = new DataOutputStream(outputStream);
                        dataOutputStream.writeUTF("User registered");
                        dataOutputStream.flush();
                    }catch (Exception e){
                        e.printStackTrace();
                        outputStream = socket.getOutputStream();
                        dataOutputStream = new DataOutputStream(outputStream);
                        dataOutputStream.writeUTF(e.toString());
                        dataOutputStream.flush();
                    }
                    break;

                case "get user permission":
                    inputStream = socket.getInputStream();
                    dataInputStream = new DataInputStream(inputStream);
                    message = dataInputStream.readUTF();
                    user = token.getKeyByValue(token.getUser_token(),message);//gets the user using the token
                    System.out.println(user);
                    try {
                        if (user == null ){
                            System.out.println("get  user permission : user not logged in"); //checks if the token is valid or not
                            throw new User_authentication_exception("get  user permission : user not logged in");
                        }
                        System.out.println("Username: " + user + " token: " + message);
                        //get username
                        inputStream = socket.getInputStream();
                        dataInputStream = new DataInputStream(inputStream);
                        String user_name = dataInputStream.readUTF();//recieve the name to send from client

                        out = new ObjectOutputStream(socket.getOutputStream());
                        out.writeObject(control_pannel_serve_obj.get_user_permission(user, user_name));//send to client
                        out.flush();
                    }catch (Exception e){
                        e.printStackTrace();
                        outputStream = socket.getOutputStream();
                        dataOutputStream = new DataOutputStream(outputStream);
                        dataOutputStream.writeUTF(e.toString());
                        dataOutputStream.flush();
                    }
                    break;

                case "set user permission":
                    inputStream = socket.getInputStream();
                    dataInputStream = new DataInputStream(inputStream);
                    message = dataInputStream.readUTF();
                    user = token.getKeyByValue(token.getUser_token(),message);//gets the user using the token
                    System.out.println(user);
                    System.out.println("Username: " + user + " token: " + message);
                    try {
                        if (user == null ){
                            System.out.println("set user permission : user not logged in"); //checks if the token is valid or not
                            throw new User_authentication_exception("set user permission : user not logged in");
                        }
                        //get username
                        inputStream = socket.getInputStream();
                        dataInputStream = new DataInputStream(inputStream);
                        username = dataInputStream.readUTF();//recieve the name to send from client
                        //get permission list
                        ObjectInputStream recieve = new ObjectInputStream(socket.getInputStream());
                        ArrayList<String> permission_recieved = (ArrayList<String>) recieve.readObject();//recieves username and password
                        control_pannel_serve_obj.set_user_permission(user,username,permission_recieved);
                        outputStream = socket.getOutputStream();
                        dataOutputStream = new DataOutputStream(outputStream);
                        dataOutputStream.writeUTF("Permission changed, user: " + username);
                        dataOutputStream.flush();
                    }catch (Exception e){
                        e.printStackTrace();
                        outputStream = socket.getOutputStream();
                        dataOutputStream = new DataOutputStream(outputStream);
                        dataOutputStream.writeUTF(e.toString());
                        dataOutputStream.flush();
                    }
                    break;

                case "edit user password":
                    inputStream = socket.getInputStream();
                    dataInputStream = new DataInputStream(inputStream);
                    message = dataInputStream.readUTF();
                    user = token.getKeyByValue(token.getUser_token(),message);//gets the user using the token
                    System.out.println(user);
                    System.out.println("Username: " + user + " token: " + message);
                    try{
                        if (user == null ){
                            System.out.println("edit user password : user not logged in"); //checks if the token is valid or not
                            throw new User_authentication_exception("edit user password : user not logged in");
                        }
                        ObjectInputStream recieve = new ObjectInputStream(socket.getInputStream());
                        ArrayList<String> data_recieved = (ArrayList<String>) recieve.readObject();//recieves username and password
                        String change_user_password = data_recieved.get(0);
                        String new_password = data_recieved.get(1);
                        String change_password = control_pannel_serve_obj.edit_password(user, change_user_password,new_password);
                        outputStream = socket.getOutputStream();
                        dataOutputStream = new DataOutputStream(outputStream);
                        dataOutputStream.writeUTF(change_password);
                        dataOutputStream.flush();
                    }catch (Exception e){
                        e.printStackTrace();
                        outputStream = socket.getOutputStream();
                        dataOutputStream = new DataOutputStream(outputStream);
                        dataOutputStream.writeUTF(e.toString());
                        dataOutputStream.flush();
                    }
                    break;

                case "delete user":
                    inputStream = socket.getInputStream();
                    dataInputStream = new DataInputStream(inputStream);
                    message = dataInputStream.readUTF();
                    user = token.getKeyByValue(token.getUser_token(),message);//gets the user using the token
                    System.out.println(user);
                    try{
                        if (user == null ){
                            System.out.println("delete user : user not logged in"); //checks if the token is valid or not
                            throw new User_authentication_exception("delete user : user not logged in");
                        }
                        System.out.println("Username: " + user + " token: " + message);
                        inputStream = socket.getInputStream();
                        dataInputStream = new DataInputStream(inputStream);
                        String delete_user = dataInputStream.readUTF();//recieve the name to send from client
                        control_pannel_serve_obj.delete_user(user, delete_user);

                        outputStream = socket.getOutputStream();
                        dataOutputStream = new DataOutputStream(outputStream);
                        dataOutputStream.writeUTF("user delete successful");
                        dataOutputStream.flush();
                    }catch (Exception e){
                        e.printStackTrace();
                        outputStream = socket.getOutputStream();
                        dataOutputStream = new DataOutputStream(outputStream);
                        dataOutputStream.writeUTF(e.toString());
                        dataOutputStream.flush();
                    }
                    break;

                case "logout":
                    //check if the user is loged in first
                    inputStream = socket.getInputStream();
                    dataInputStream = new DataInputStream(inputStream);
                    message = dataInputStream.readUTF();
                    user = token.getKeyByValue(token.getUser_token(),message);//gets the user using the token
                    System.out.println("The message sent from the socket was: " + user + " token: " + message);
                    try{
                        if (token.getUser_token().get(user) == null){
                            System.out.println("user log in not found");
                            throw new User_authentication_exception(("user log in not found"));
                        }
                        else {
                            System.out.println(token.getUser_token().get(user) + "  user found");
                            token.delete_token(user);
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        outputStream = socket.getOutputStream();
                        dataOutputStream = new DataOutputStream(outputStream);
                        dataOutputStream.writeUTF(e.toString());
                        dataOutputStream.flush();
                    }
                    outputStream = socket.getOutputStream();
                    dataOutputStream = new DataOutputStream(outputStream);
                    dataOutputStream.writeUTF("User log out successful");
                    dataOutputStream.flush();
                    break;
            }

            //deletes the token after 24 hours
            try {
               // System.out.println(token.getUser_token());
                System.out.println("\n\n");
                System.out.println(token.current_time() + " current time");
                System.out.println(token.getUser_token_timer() + " expire time");

                for (String o : token.getUser_token_timer().keySet()) {
                    if (token.compare(o) < 0) {
                        System.out.println("deleted: " + o);
                        token.getUser_token().remove(o);
                        token.getUser_token_timer().remove(o);
                    }
                }
            }catch (Exception e){
                System.out.println("token error");
                 e.printStackTrace();
            }
            System.out.println(token.getUser_token() + " token remaining ");
            System.out.println("\n\n");
            socket.close();

        }

    }

}
