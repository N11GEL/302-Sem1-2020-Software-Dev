package Server.server_package;


import java.io.IOException;
import java.io.OutputStream;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Billboard_viewer_client_mock {
    //this is client for database viewer
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 12345);
        // get the output stream from the socket.
        OutputStream outputStream = socket.getOutputStream();

        // create a data output stream from the output stream so we can send data through it
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        // sends this to the server asking for billbaord
        dataOutputStream.writeUTF("billboard viewer request");
        dataOutputStream.flush(); // send the message

        // bos.close();//also flush the data but that will also close the underline stream for example it will also close output stream
        //if wanna keep communicating use flush


        //recieves array from the server
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




    }}