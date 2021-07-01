package Viewer;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;

/**
 * The purpose of this class is to connect to the billboard server,
 * and retrieve the current billboard data
 */
public class Connect {
    /**
     * This function returns the data in the form of an ArrayList.
     * This data is then added to the billboardData[] array for use in the rest of the application.
     * If a successful connection is made and no null values are present in the object,
     * the billboard is then generated from the retrieved data.
     *
     * @return = Return bool value based on the outcome of connecting to the server
     * @throws IOException
     */
    public static boolean serverConnect() throws IOException {
        boolean connected;
        try {
            Properties props = new Properties();
            FileInputStream in = new FileInputStream("db.props");
            props.load(in);
            in.close();
            // Specify the data source
            String hostName = props.getProperty("server.hostname");
            String portName = props.getProperty("server.port");

            Socket socket = new Socket(hostName, Integer.parseInt(portName));
            // Get the output stream from the socket.
            OutputStream outputStream = socket.getOutputStream();

            // Create a data output stream
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            // Send request to server asking for billboard
            dataOutputStream.writeUTF("billboard viewer request");
            dataOutputStream.flush();

            // Transfers object received from server to an array list
            ObjectInputStream out = new ObjectInputStream(socket.getInputStream());
            ArrayList<String> message = (ArrayList<String>) out.readObject();
            //System.out.println((Object) message);

            // Checks if received value is null, if so connection is deemed unsuccessful
            if (message.get(0) == null) {
                connected = false;
            } else {
                // Add each string from message object into the billboard data array
                for (int i = 0; i < message.size(); i++) {
                    Viewer.billboardData[i] = message.get(i);
                }
                connected = true;
                System.out.println("Server connection successful.");
                System.out.println("Data received: " + message);
            }
            // End connection
            dataOutputStream.close();
            socket.close();

        } catch (Exception e) {
            System.out.println("Error: " + e);
            connected = false;
            System.out.println("Server connection failed.");
        }
        return connected;
    }
}
