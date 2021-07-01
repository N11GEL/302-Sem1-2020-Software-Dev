package Server.user_authentication_package;

import java.util.*;

import Server.Database.mock_db;


public class mock_user_authentication extends mock_db implements user_authenticaion_interface {
    mock_db user;
    mock_db permission;
    ArrayList<String> possible_permission = new ArrayList<String>(Arrays.asList( "Create Billboards", "Edit All Billboards", "Schedule Billboards", "Edit Users"));
    public mock_user_authentication(){
        user = new mock_db(); //initiates a 2d array as mock database
        user.database("user_name", "hashed_password", "salt");
        permission = new mock_db();
        permission.database("user_name",  "create_billboard", "edit_billbaord","schedule_billboard","edit_user");
    }


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


    @Override
    public void change_permission(String user_name,List permission) throws User_authentication_exception{
        //no permission on list
        //user id not found
        int count_user = 0;
        int user_int = 0;
        for (int i = 0; i < this.permission.get_info().size(); i++){
            if (this.permission.get_info().get(i).get(0).equals(user_name)){
                user_int = i;
                break;
            }
            count_user++;
            if (count_user == this.permission.get_info().size()){
                throw new User_authentication_exception("user not found");
            }
        }
        ArrayList<String> permission_save = new ArrayList<String>(); //1 if permission is true 0 if false
        for (int i = 0; i < 5; i++) { //sets every index to 0/false at start; 5 -> number of permission
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
            if (count == 5){
                // return (permission.get(j) + " :permission not found!") ;
                throw new User_authentication_exception(permission.get(j) + " :permission not found!") ;
            }
        }
        mock_db db = new mock_db();
        db.database( user_name, permission_save.get(0), permission_save.get(1), permission_save.get(2),
                permission_save.get(3));
        this.permission.get_info().set(user_int, db.get_info().get(0));
    }


    @Override
    public void register_user(String user_name, List permission, String hashed_password) throws User_authentication_exception {

        String salt = "salt"; //using constant so its easier for testing
        ArrayList<String> permission_save = new ArrayList<String>(); //1 if permission is true 0 if false
        for (int i = 0; i < 5; i++) { //sets every index to 0/false at start; 5 -> number of permission
            permission_save.add("0");
        }
        if (permission.size() > possible_permission.size()){ //checks if the list of permission is too big
            throw new User_authentication_exception("permission size too big (limit = 4), permission size: " + permission.size());
        }
        if (permission.size() == 0){throw new User_authentication_exception("Permission not recieved");}
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
        HashMap password_has = hash_passowrd(hashed_password,salt ); //hash the password again and save with salt
        user.database(user_name,(String) password_has.get(salt), salt);//add user
        this.permission.database(user_name, permission_save.get(0), permission_save.get(1), permission_save.get(2),
                                    permission_save.get(3)); //adds permission
        return;
    }


    @Override
    public  List<List<String>> show_all_user(){
       return  user.get_info();
    }


    public ArrayList<String> show_username(){
        ArrayList<String> usernames = new ArrayList<>();
        for ( List<String> o : show_all_user()){
            usernames.add(o.get(0));
        }

        return usernames;

    }


    @Override
    public List<List<String>> show_all_permissions(){
        return  permission.get_info();
    }


    @Override
    public void delete_user(String username) throws User_authentication_exception {

        for (int i = 0; i < user.get_info().size(); i++){
            if (username.equals(user.get_info().get(i).get(0))) {
                user.get_info().remove(user.get_info().get(i));
            }
        }
        for (int i = 0; i < permission.get_info().size(); i++){
            if (username.equals(permission.get_info().get(i).get(0))){
                permission.get_info().remove(permission.get_info().get(i));
                return; //because only can delete one user at a time
            }
        }
        throw new User_authentication_exception("user name not found!");
    }


    @Override
    public String log_in(String user_name, String password) throws User_authentication_exception {
        //search if the username exit in the database
        ArrayList<List<String>> data = new ArrayList<List<String>>() {
        };//store the user data
        for (int i = 0; i < user.get_info().size(); i++){
            if (user.get_info().get(i).get(0).equals(user_name)){
                data.add(user.get_info().get(i));
            }
        }
        ArrayList<List<String>> test = data;
        if (test.size() == 0){
            throw new User_authentication_exception("User not found!");
        }
        for (int i = 0; i < data.size(); i++) {
            String username_retrived = data.get(i).get(0);
            String password_retreved = data.get(i).get(1);
            String salt_retrieved = data.get(i).get(2);
            //hash the password gotten from the parameter to check again the database
            HashMap passowrd_has = hash_passowrd(password, salt_retrieved);
            String password_got = (String) passowrd_has.get(salt_retrieved);
            //check if the username is same or not
            if ((user_name.equals(username_retrived)) && (password_retreved.equals(password_got))) {
                //return "Log in sucessful\nUserID: " + userID_retrived;
                return "Log in sucessful";
            }
        }
        return "Wrong password";
    }


    @Override
    public String edit_user_password(String user_name, String new_password) throws User_authentication_exception{
        String current_password = "";
        String salt = "";
        String new_password_test = "";
        for (int i = 0; i < user.get_info().size(); i++){
            String user_id = user.get_info().get(i).get(0);
            if (user_name.equals(user.get_info().get(i).get(0))){
                current_password =  user.get_info().get(i).get(1);
                salt = user.get_info().get(i).get(2);
                HashMap password_has = hash_passowrd(new_password,salt);
                new_password_test = (String) password_has.get(salt);
                user.get_info().get(i).set(1, (String) password_has.get(salt));//new password
                return "password change successful!";
               // return String.format("old password: %s\nnew password: %s", current_password, new_password_test);
            }
        }
        throw new User_authentication_exception("passwrod change unsuccessful!");
    }


    @Override
    public ArrayList get_user_permission(String username) throws User_authentication_exception{
        //get the list of user permission
        for (int i = 0; i < show_all_permissions().size(); i++){
            if (show_all_permissions().get(i).contains(username)){
                return (ArrayList) show_all_permissions().get(i);
            }
        }
        throw new User_authentication_exception("user permission not found");
    }


    @Override
    public Boolean check_user_permission(String username, String permission) throws User_authentication_exception{
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