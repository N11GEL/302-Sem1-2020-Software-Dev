package Server.user_authentication_package;

import Server.Database.connection_db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class User_authentication  implements user_authenticaion_interface {


    ArrayList<String> possible_permission = new ArrayList<String>(Arrays.asList( "Create Billboards", "Edit All Billboards", "Schedule Billboards", "Edit Users")); //list of all permission

    public static HashMap hash_passowrd(String hashed_password, String salt) { //gets the hashed passwrod and the salt
        String password = hashed_password + salt;   //adds salt
        Integer with_salt = password.hashCode();    //hash it again with salt
        String password_store = with_salt.toString(); //convert into string
        HashMap<String, String> password_hash = new HashMap<>();
        password_hash.put(salt, password_store); //salt is the lable
        return password_hash;
    }

    private String random_string(int n){ //generates random string
        String random_string = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(random_string.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(random_string
                    .charAt(index));
        }
        return sb.toString();
    }

    //TO DO:  make the salt generation random
    @Override
    public void register_user(String user_name, List permission, String hashed_password )throws User_authentication_exception {
        ArrayList<List<String>> test = (ArrayList<List<String>>) show_all_user();
        for (int i = 0; i < show_all_user().size(); i++){ //check if the user name already exit beacuse user name is unique
            if (user_name.equals(show_all_user().get(i).get(0))){
                throw new User_authentication_exception(String.format("User name '%s' already exist", user_name));
            }
        }
        connection_db connect = new connection_db();
        connect.setRequest("insert_into");
        connect.table("user");
        String random_salt = random_string(5);
        HashMap passowrd_has = hash_passowrd(hashed_password, random_salt);
        String command = String.format("'%s', '%s', '%s'", user_name, passowrd_has.get(random_salt), random_salt);
        connect.setCond_one(command);
        try {
            connect.DB_Connect();
        }catch (Exception e){
            throw new User_authentication_exception("User not logged in");

        }
        //setting up another connection for adding permission
        //finding whic permssion this user has
        ArrayList<String> permission_save = new ArrayList<String>(); //1 if permission is true 0 if false
        for (int i = 0; i < 5; i++) { //sets every index to 0/false at start; 5 -> number of permission
            permission_save.add("0");
        }
        if (permission.size() > possible_permission.size()){ //checks if the list of permission is too big
            throw new User_authentication_exception("permission size too big (limit = 4), permission size: " + permission.size());}

        int count = 0;
        for (int j = 0; j < permission.size(); j++) { //sets index to 1 if permission is given
            for (int i = 0; i < possible_permission.size() ; i++) {
                if (permission.get(j).equals(possible_permission.get(i))) {
                    permission_save.set(i, "1");
                    count = 0;
                    break;
                }
                else{
                    count++;
                }
            }
            if (count == 4){
                // return (permission.get(j) + " :permission not found!") ;
                throw new User_authentication_exception(permission.get(j) + " :permission not found!") ;
            }
        }
        connection_db connecttwo = new connection_db();
        connecttwo.setRequest("insert_into");
        connecttwo.table("permission");
        connecttwo.setCond_one(String.format("'%s', %s, %s, %s, %s", user_name, permission_save.get(0), permission_save.get(1), permission_save.get(2), permission_save.get(3)));
       try {
           connecttwo.DB_Connect();
       }
       catch (Exception e){
           throw new User_authentication_exception("Register error: permission");
       }

    }

    @Override
    public void delete_user(String user_name)throws User_authentication_exception{
        //need to delete the permission first because cannot delete user cause its foreign key to permmsion
        connection_db connect = new connection_db();
        connect.setRequest("select_where");
        connect.table("permission");
        String command = String.format("user_name = '%s'", user_name);
        connect.setCond_one(command);
        List<List<String>> data;
        try {
            data = connect.DB_Connect(); //retreves the data from database
        }
        catch (Exception e){
            throw new User_authentication_exception("delete user, username not found");
        }


        connect = new connection_db();
        connect.setRequest("delete");
        connect.table("permission");
        command = String.format("user_name = '%s'", user_name);
        connect.setCond_one(command);
        try {
            connect.DB_Connect();
        }
        catch (Exception e) {
            e.printStackTrace();
        }


        connect = new connection_db();
        connect.setRequest("delete");
        connect.table("user");
        command = String.format("user_name = '%s'", user_name);
        connect.setCond_one(command);
        try {
            connect.DB_Connect();
        }
         catch (Exception e){
            throw new User_authentication_exception("delete user, usernaame not found");
        }
    }

    //TO DO: CHECK IF THERE ARE MULTIPLE USER WITH SAME USERNAME
    @Override
    public String log_in(String user_name, String password)throws User_authentication_exception { //need salt, username, password
        //find corrent salt and username


        connection_db connect = new connection_db();
        connect.setRequest("select_where");
        connect.table("user");
        String user_name_format = String.format("'%s'", user_name);
        String command = String.format("user_name = %s", user_name_format);
        connect.setCond_one(command);
        List<List<String>> data;
        try {
            data = connect.DB_Connect(); //retreves the data from database
        }
        catch (Exception e){
            throw new User_authentication_exception("login user, usernaame not found");
        }

       if (data.size() == 0){
           return "Username not found";
       }

        //if there is more than one user with same name
        for (int i = 0; i < data.size(); i++) {
            String username_retrived = data.get(i).get(0);
            String password_retreved = data.get(i).get(1);
            String salt_retrieved = data.get(i).get(2);

            //hash the password gotten from the parameter to check again the database
            HashMap passowrd_has = hash_passowrd(password, salt_retrieved);
            String password_got = (String) passowrd_has.get(salt_retrieved);

            //check if the username is same or not
            if ((user_name.equals(username_retrived)) && (password_retreved.equals(password_got))) {
               // return "Log in sucessful\nUserID: " + userID_retrived;
                return "Log in sucessful";
            }
        }
        return "Wrong password";
    }

    @Override
    public String edit_user_password(String user_name, String new_password) throws User_authentication_exception {

        //check if user exists

        connection_db connect;
        connect = new connection_db();
        connect.setRequest("select_where");
        connect.table("user");
        connect.setCond_one(String.format("user_name = '%s'", user_name));

        try {
            if (connect.DB_Connect().size() == 0) {
                throw new User_authentication_exception("User does not exists for editing password");
            }
        } catch (SQLException e) {
            throw new User_authentication_exception("User does not exists for editing password");
        }
        String random_salt = random_string(5);
        HashMap password_has = hash_passowrd(new_password,random_salt);
         connect = new connection_db();
        connect.setRequest("update");
        connect.table("user");
        String command = String.format("password = '%s', salt = '%s'", password_has.get(random_salt), random_salt);
        connect.setCond_two(String.format("user_name = '%s'", user_name));
        connect.setCond_one(command);
        try {
            connect.DB_Connect(); //retreves the data from database
        } catch (Exception e){
            throw new User_authentication_exception("edit user, user name not found");
        }
        return "password change successful!";
    }

    @Override
    public void change_permission(String user_name, List permission) throws User_authentication_exception {

        //finding whic permssion this user has
        ArrayList<String> permission_save = new ArrayList<String>(); //1 if permission is true 0 if false
        for (int i = 0; i < 4; i++) { //sets every index to 0/false at start; 5 -> number of permission
            permission_save.add("0");
        }
        if (permission.size() > possible_permission.size()){ //checks if the list of permission is too big
            throw new User_authentication_exception("permission size too big (limit = 4), permission size: " + permission.size());
        }
        if (permission.size() == 0){throw new User_authentication_exception ("Permission not recieved" );}
        int count = 0;
        for (int j = 0; j < permission.size(); j++) { //sets index to 1 if permission is given
            for (int i = 0; i < possible_permission.size() ; i++) {
                if (permission.get(j).equals(possible_permission.get(i))) {
                    permission_save.set(i, "1");
                    count = 0;
                    break;
                }
                else{
                    count++;
                }
            }
            if (count == 4){
                // return (permission.get(j) + " :permission not found!") ;
                throw new User_authentication_exception(permission.get(j) + " :permission not found!") ;
            }
        }
        connection_db connect = new connection_db();
        connect.setRequest("update");
        connect.table("permission");
        String command = String.format("create_billboard = '%s',  edit_billboard = '%s',  schedule_billboard = '%s', edit_user = '%s'", permission_save.get(0), permission_save.get(1), permission_save.get(2), permission_save.get(3));
        connect.setCond_one(command);
        connect.setCond_two(String.format("user_name = '%s'", user_name));
        try {
            connect.DB_Connect();
        }
        catch (Exception e){
          e.printStackTrace();
    }
    }

    @Override //shows all user from the database
    public List<List<String>> show_all_user() throws User_authentication_exception{
        connection_db connect = new connection_db();
        connect = new connection_db();
        connect.setRequest("view_all");
        connect.table("user");
        try{
            return connect.DB_Connect();
        }
        catch (Exception e){
            throw new User_authentication_exception("show_all_user");
        }

    }


    @Override
    public ArrayList<String> show_username() throws User_authentication_exception {
        ArrayList<String> usernames = new ArrayList<>();
        for ( List<String> o : show_all_user()){
            usernames.add(o.get(0));
        }
        return usernames;
    }


    @Override //shows all permissions from the database
    public List show_all_permissions()  throws User_authentication_exception{

        connection_db connect = new connection_db();
        connect.setRequest("view_all");
        connect.table("permission");
        try {
            return connect.DB_Connect();
        }
        catch (Exception e){
            throw new User_authentication_exception("show_all_permissions");
        }

    }

    @Override
    public List get_user_permission(String username) throws User_authentication_exception{
        //get the list of user permission
        connection_db connect = new connection_db();
        connect.setRequest("select_where");
        connect.table("permission");
        connect.setCond_one(String.format("user_name = '%s'", username));
        try {
            return connect.DB_Connect().get(0);
        } catch (Exception e) {
            throw new User_authentication_exception("User not found in permission");
        }

    }

    @Override
    public Boolean check_user_permission(String username, String permission) throws User_authentication_exception{
        get_user_permission(username);

        switch (permission){
            case("Create Billboards"):
                if (get_user_permission(username).get(1).equals("1")){
                    return true;
                }
                break;
            case("Edit All Billboards"):
                if (get_user_permission(username).get(2).equals("1")){
                    return true;
                }
                break;
            case("Schedule Billboards"):
                if (get_user_permission(username).get(3).equals("1")){
                    return true;
                }
                break;
            case("Edit Users"):
                if (get_user_permission(username).get(4).equals("1")){
                    return true;
                }
                break;
        }
        return false;
    }






}


