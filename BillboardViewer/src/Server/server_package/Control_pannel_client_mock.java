package Server.server_package;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Control_pannel_client_mock {
    public static String password = "password"; //this is suppose to be hashed
    public static String user_name = "admin";


    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 12345);
        String request;
       // request = "logout";




       request = "login";
      //request = "list billboards";
     //  request = "billboard info";
     //   request = "create billboard";
     //  request = "edit billboard";
      //   request = "delete user";
       // request = "edit user password";
      //  request = "register user";
        request = "list users";
     //   request = "get user permission";
       // request = "set user permission";
       // request = "schedule billboard";
      //  request = "remove billboard from schedule";
    //    String hashed_password = Integer.toString("password".hashCode());
    //    password = hashed_password;
        String newtoken = "jHGGT";
        String billboard_name;
        HashMap<String, String> token = new HashMap<>();
        OutputStream outputStream = socket.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        switch(request){

            case "login":

                outputStream = socket.getOutputStream();
                dataOutputStream = new DataOutputStream(outputStream);
                dataOutputStream.writeUTF("login");
                dataOutputStream.flush(); // send the message

                //sends user name and password as array list
                ArrayList<String> send_data = new ArrayList<>();
                send_data.add(user_name);
                send_data.add(password);
                ObjectOutputStream send = new ObjectOutputStream(socket.getOutputStream());
                send.writeObject(send_data);
                dataOutputStream.flush(); // send the message

                //recieves the token
                InputStream inputStream = socket.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(inputStream);
                String messages = dataInputStream.readUTF();
                System.out.println("The seasion token: " + messages);
                token.put(user_name,messages);



                dataOutputStream.close(); // close the output stream when we're done.
                //  dataInputStream.close(); // close the output stream when we're done.

                socket.close();

                break;

            case "logout":
                // create a data output stream from the output stream so we can send data through it
                 dataOutputStream = new DataOutputStream(outputStream);

                // sends this to the server asking for billbaord
                dataOutputStream.writeUTF("logout");
                dataOutputStream.flush(); // send the message

                dataOutputStream.writeUTF(newtoken);
                dataOutputStream.flush(); // send the message

                inputStream = socket.getInputStream();
                dataInputStream = new DataInputStream(inputStream);
                messages = dataInputStream.readUTF();
                System.out.println(messages);

                socket.close();
                break;

            case "list billboards":
                //send token
                //recieve list of billboard
                dataOutputStream = new DataOutputStream(outputStream);

                // sends this to the server asking for billbaord
                dataOutputStream.writeUTF("list billboards");
                dataOutputStream.flush(); // send the message


                String send_token = token.get(user_name);

                dataOutputStream.writeUTF(newtoken); //send the token
                dataOutputStream.flush(); // send the token

                //recieve the content
                try {
                    ObjectInputStream out = new ObjectInputStream(socket.getInputStream());

                    ArrayList<String> message = (ArrayList<String>) out.readObject();
                    System.out.println((Object) message);


                } catch (Exception e) { // if there is error
                    System.out.println(e);
                }

                dataOutputStream.close(); // close the output stream when we're done.
                //  dataInputStream.close(); // close the output stream when we're done.

                socket.close();


                break;

            case "billboard info":
                dataOutputStream = new DataOutputStream(outputStream);

                // sends this to the server asking for billbaord
                dataOutputStream.writeUTF("billboard info");
                dataOutputStream.flush(); // send the message

                dataOutputStream.writeUTF(newtoken); //send the token
                dataOutputStream.flush(); // send the token

                //send billboard name
                billboard_name = "billboard the second";
                dataOutputStream.writeUTF(billboard_name); //send the token
                dataOutputStream.flush(); // send the token

                //recieve
                try {
                    ObjectInputStream out = new ObjectInputStream(socket.getInputStream());

                    ArrayList<String> message = (ArrayList<String>) out.readObject();
                    System.out.println((Object) message);


                } catch (Exception e) { // if there is error
                    System.out.println(e);
                }

                dataOutputStream.close(); // close the output stream when we're done.

                socket.close();

                break;

            case "create billboard":
                dataOutputStream = new DataOutputStream(outputStream);

                // sends this to the server asking for billbaord
                dataOutputStream.writeUTF("create billboard");
                dataOutputStream.flush(); // send the message

                dataOutputStream.writeUTF(newtoken); //send the token
                dataOutputStream.flush(); // send the token

                try {
                    inputStream = socket.getInputStream();
                    dataInputStream = new DataInputStream(inputStream);
                    messages = dataInputStream.readUTF();
                    if (messages.equals("User does not have correct permission")) {
                        System.out.println("error" + messages);//prints the errro if the user does not have correct permission
                        socket.close();
                        break;
                    }
                    if (messages.equals("User does have correct permission")){
                        //send billboard content
                        //sends user name and password as array list
                        send_data = new ArrayList<>();
                        send_data.add("billboard the deletssse");
                        send_data.add("bg_colour");
                        send_data.add("mssg");
                        send_data.add("mssg_colour");
                        send_data.add("picture");
                        send_data.add("info_mssg");
                        send_data.add("info_colour");
                        send = new ObjectOutputStream(socket.getOutputStream());
                        send.writeObject(send_data);
                        dataOutputStream.flush(); // send the message
                    }
                    inputStream = socket.getInputStream();
                    dataInputStream = new DataInputStream(inputStream);
                    messages = dataInputStream.readUTF();
                    System.out.println(messages);

                }catch (Exception e){
                    System.out.println(e);
                }

                break;

            case "edit billboard":
                dataOutputStream = new DataOutputStream(outputStream);

                // sends this to the server asking for billbaord
                dataOutputStream.writeUTF("edit billboard");
                dataOutputStream.flush(); // send the message

                dataOutputStream.writeUTF(newtoken); //send the token
                dataOutputStream.flush(); // send the token


                //send billboard content
                //sends user name and password as array list
                send_data = new ArrayList<>();
                send_data.add("billboard the third");
                send_data.add("purple");
                send_data.add("mssg");
                send_data.add("mssg_colour");
                send_data.add("picture");
                send_data.add("info_mssg");
                send_data.add("info_colour");
                send = new ObjectOutputStream(socket.getOutputStream());
                send.writeObject(send_data);
                dataOutputStream.flush(); // send the message

                try {
                    inputStream = socket.getInputStream();
                    dataInputStream = new DataInputStream(inputStream);
                    messages = dataInputStream.readUTF();
                    System.out.println(messages);
                }catch (Exception e){}


                socket.close();
                break;

        }


        switch(request) { //previous switch was talking too many lines
            case "delete user":
                //send request
                dataOutputStream.writeUTF("delete user");
                dataOutputStream.flush(); // send the message
                //send token
                dataOutputStream.writeUTF(newtoken); //send the token
                dataOutputStream.flush(); // s
                //send user to delete
                String delete_user = "delete user";
                dataOutputStream.writeUTF(delete_user); ///send user that needs to be deleted
                dataOutputStream.flush(); // s
                //recieve message
                try {
                    InputStream inputStream = socket.getInputStream();
                    DataInputStream dataInputStream = new DataInputStream(inputStream);
                    String messages;
                    inputStream = socket.getInputStream();
                    dataInputStream = new DataInputStream(inputStream);
                    messages = dataInputStream.readUTF();
                    System.out.println(messages);
                }catch (Exception e){}
                break;

            case "edit user password":
                //send request
                dataOutputStream.writeUTF("edit user password");
                dataOutputStream.flush(); // send the message
                //send token
                dataOutputStream.writeUTF(newtoken); //send the token
                dataOutputStream.flush(); //
                //send user to delete
                String edit_user_password = "regester user";
                String new_password = "passwrd";

                ArrayList<String> send_data = new ArrayList<>();
                send_data.add(edit_user_password);
                send_data.add(new_password);
                ObjectOutputStream send = new ObjectOutputStream(socket.getOutputStream());
                send.writeObject(send_data);
                dataOutputStream.flush();
                try {
                    InputStream inputStream = socket.getInputStream();
                    DataInputStream dataInputStream = new DataInputStream(inputStream);
                    String messages;
                    inputStream = socket.getInputStream();
                    dataInputStream = new DataInputStream(inputStream);
                    messages = dataInputStream.readUTF();
                    System.out.println(messages);
                }catch (Exception e){}
                break;

            case "register user":
                dataOutputStream.writeUTF("register user");
                dataOutputStream.flush(); // send the message
                //send token
                dataOutputStream.writeUTF(newtoken); //send the token
                dataOutputStream.flush(); //

                //send permission
                ArrayList<String> possible_permission = new ArrayList<String>(Arrays.asList( "Create Billboards", "Schedule Billboards")); //list of all permission
                 send = new ObjectOutputStream(socket.getOutputStream());
                send.writeObject(possible_permission);
                dataOutputStream.flush();

                //send username and password
                String create_user = "regester new user";

                int passowrd_int =  ("new password".hashCode());

                String passworhas = Integer.toString(passowrd_int);


                 new_password = passworhas;
                send_data = new ArrayList<>();
                send_data.add(create_user);
                send_data.add(new_password);
                 send = new ObjectOutputStream(socket.getOutputStream());
                send.writeObject(send_data);
                dataOutputStream.flush();

                try {
                    InputStream inputStream = socket.getInputStream();
                    DataInputStream dataInputStream = new DataInputStream(inputStream);
                    String messages;
                    inputStream = socket.getInputStream();
                    dataInputStream = new DataInputStream(inputStream);
                    messages = dataInputStream.readUTF();
                    System.out.println(messages);
                }catch (Exception e){}
                break;
            case "list users":
                dataOutputStream = new DataOutputStream(outputStream);

                // sends this to the server asking for billbaord
                dataOutputStream.writeUTF("list users");
                dataOutputStream.flush(); // send the message


                String send_token = token.get(user_name);

                dataOutputStream.writeUTF(newtoken); //send the token
                dataOutputStream.flush(); // send the token

                //recieve the content
                try {
                    ObjectInputStream out = new ObjectInputStream(socket.getInputStream());
                    ArrayList<String> message = (ArrayList<String>) out.readObject();
                    System.out.println((Object) message);
                } catch (Exception e) { // if there is error
                    System.out.println(e);
                }

                dataOutputStream.close(); // close the output stream when we're done.
                //  dataInputStream.close(); // close the output stream when we're done.
                socket.close();
                break;

            case "get user permission":
                dataOutputStream = new DataOutputStream(outputStream);

                // sends this to the server asking for billbaord
                dataOutputStream.writeUTF("get user permission");
                dataOutputStream.flush(); // send the message

                 send_token = token.get(user_name);

                dataOutputStream.writeUTF(newtoken); //send the token
                dataOutputStream.flush(); // send the token

                String user_name_send = "admin";
                dataOutputStream.writeUTF(user_name_send); //send the token
                dataOutputStream.flush(); // send the toke

                //recieve the content
                try {
                    ObjectInputStream out = new ObjectInputStream(socket.getInputStream());
                    ArrayList<String> message = (ArrayList<String>) out.readObject();
                    System.out.println((Object) message);
                } catch (Exception e) { // if there is error
                    System.out.println(e);
                }
                break;

            case "set user permission":
                dataOutputStream = new DataOutputStream(outputStream);

                // sends this to the server asking for billbaord
                dataOutputStream.writeUTF("set user permission");
                dataOutputStream.flush(); // send the message



                dataOutputStream.writeUTF(newtoken); //send the token
                dataOutputStream.flush(); // send the token

                String user_name = "Zishan Fry";
                dataOutputStream.writeUTF(user_name); //send the token
                dataOutputStream.flush(); // send the toke

                //send permission list
                possible_permission = new ArrayList<String>(Arrays.asList("Create Billboards", "Edit All Billboards", "Schedule Billboards")); //list of all permission
                send = new ObjectOutputStream(socket.getOutputStream());
                send.writeObject(possible_permission);
                dataOutputStream.flush();

                //recieve the content
                try {
                    InputStream inputStream = socket.getInputStream();
                    DataInputStream dataInputStream = new DataInputStream(inputStream);
                    String messages;
                    inputStream = socket.getInputStream();
                    dataInputStream = new DataInputStream(inputStream);
                    messages = dataInputStream.readUTF();
                    System.out.println(messages);
                }catch (Exception e){}
                break;





        }

        switch (request){
            case "schedule billboard":
                dataOutputStream = new DataOutputStream(outputStream);

                // sends this to the server asking for billbaord
                dataOutputStream.writeUTF("schedule billboard");
                dataOutputStream.flush(); // send the message

                //send token
                dataOutputStream.writeUTF(newtoken); //send the token
                dataOutputStream.flush(); // send the token

                //send permission list
                billboard_name = "billboard the deletssse";
                String duration = "13";
                String time = " 2020-05-31 17:54:20";
                String reccurace = "50";



                ArrayList<String> possible_permission = new ArrayList<String>(Arrays.asList( billboard_name, duration, time, reccurace)); //list of all permission
                ObjectOutputStream send = new ObjectOutputStream(socket.getOutputStream());
                send.writeObject(possible_permission);
                dataOutputStream.flush();

                //recieve the content
                try {
                    InputStream inputStream = socket.getInputStream();
                    DataInputStream dataInputStream = new DataInputStream(inputStream);
                    String messages;
                    inputStream = socket.getInputStream();
                    dataInputStream = new DataInputStream(inputStream);
                    messages = dataInputStream.readUTF();
                    System.out.println(messages);
                }catch (Exception e){}
                break;

            case "remove billboard from schedule":
                dataOutputStream = new DataOutputStream(outputStream);

                // sends this to the server asking for billbaord
                dataOutputStream.writeUTF("remove billboard from schedule");
                dataOutputStream.flush(); // send the message

                //send token
                dataOutputStream.writeUTF(newtoken); //send the token
                dataOutputStream.flush(); // send the token

                //send permission list
                billboard_name = "billboard the last";
                 time = " 2020-05-31 17:54:20";


                possible_permission = new ArrayList<String>(Arrays.asList( billboard_name, time)); //list of all permission
                send = new ObjectOutputStream(socket.getOutputStream());
                send.writeObject(possible_permission);
                dataOutputStream.flush();

                //recieve the content
                try {
                    InputStream inputStream = socket.getInputStream();
                    DataInputStream dataInputStream = new DataInputStream(inputStream);
                    String messages;
                    inputStream = socket.getInputStream();
                    dataInputStream = new DataInputStream(inputStream);
                    messages = dataInputStream.readUTF();
                    System.out.println(messages);
                }catch (Exception e){}
                break;

        }


    }

}
