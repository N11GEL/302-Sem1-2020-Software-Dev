package Server.control_panel;
import Server.Database.mock_db;
import Server.user_authentication_package.User_authentication_exception;
import Server.user_authentication_package.mock_user_authentication;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class control_pannelMock implements  control_panel_interface{
    mock_db billboard_mock;
    mock_db schedule;
    mock_user_authentication check_permission;
    private ArrayList<String> permission = new ArrayList<String>(Arrays.asList("Create Billboards",  "Schedule Billboards", "Edit All Billboards"));

    public control_pannelMock() throws User_authentication_exception {
        billboard_mock = new mock_db();
        schedule = new mock_db();
        initiate_mock_DB();
        check_permission = new mock_user_authentication();
        String username = "adam lee";
        check_permission.register_user(username, permission, "test");
        permission = new ArrayList<String>(Arrays.asList("Create Billboards", "Edit Users","Schedule Billboards"));
        check_permission.register_user("test one", permission, "test");
    }

    public void initiate_mock_DB(){//adds values to mock db
        billboard_mock.database("billboard one", "adam lee", "red", "this is a message", "red", "picutre","this is info", "black" );
        billboard_mock.database("billboard two", "user two ", "green", "this is a message", "white", "picutre","this is info", "white" );
        billboard_mock.database("billboard three", "user three","yellow", "this is a message", "purple", "picutre","this is info", "silver" );


        schedule.database("billboard one", "user one", "15","2020-05-05 19:36:30" );
        schedule.database("billboard two", "adam lee", "15","2020-05-05 19:38:30" );
        schedule.database("billboard three", "user three", "15","2020-05-05 20:13:30" );

    }

    //username is the currently logged in user
    @Override
    public String create_bilboard(String billboard_name, String username, String bg_colour, String mssg,
                                String mssg_colour, String picture, String info_mssg, String info_colour) throws User_authentication_exception{
        //check if the name is not already in use
        for (int i = 0; i < billboard_mock.get_info().size(); i++){
            if (billboard_mock.get_info().get(i).contains(billboard_name)){
                throw new User_authentication_exception("billboard name already in use");//thros if the name aredly in use
            }
        }
        //add the billboard
        try{
        billboard_mock.database(billboard_name,username,bg_colour,mssg, mssg_colour, picture, info_mssg, info_colour);
        }catch (Exception e){
            throw  new User_authentication_exception("creating new billboard uncessfull");
        }
        return "billboard created";
    }

    @Override
    public String edit_billboard(String billboard_name, String username, String bg_colour, String mssg,
                               String mssg_colour, String picture, String info_mssg, String info_colour) throws User_authentication_exception{
        //create billboard -- to edit own billboard as long as it is not in schedule
        //edit all billboard -- to edit other user billboard or edit billboard currently scheduled
        //find if the user has correct permission
        Boolean edit_user = false; //true: reuires edit user; false: create user can change too
        //check if the billboard is already in schedule
        for (int i = 0; i < schedule.get_info().size(); i++){
            if (schedule.get_info().get(i).contains(billboard_name)){
             //if it is than only edit user can edit it
                edit_user = true;
                break;
            }
        }
        if (!edit_user) { //false because its already on schedule only edit user can change it
            for (int i = 0; i < billboard_mock.get_info().size(); i++) {
                if (billboard_mock.get_info().get(i).contains(billboard_name)) {
                    if(billboard_mock.get_info().get(i).contains(username)){//check if its created by same user
                        edit_user = false;
                    }
                    else{
                        edit_user = true; // need edit user permission
                    }
                    break;
                }
            }

        }
        //check if the user hass correct permission
        String Permission_create = "Create Billboards";
        String Permission_edit = "Edit All Billboards";
        Boolean permission_granted = false;
        if(!edit_user){
            //permission required: create billboard OR edit all billbaord
            if ((check_permission.check_user_permission(username,Permission_create)) ||  (check_permission.check_user_permission(username,Permission_edit))){
                permission_granted = true;
            }
        }
        if (edit_user){
            //permission required: only edit all billboard
            if (check_permission.check_user_permission(username,Permission_edit)){
                permission_granted = true;
            }
        }
        if (permission_granted){
            //
        //repalce the billboard: billbaord_name
        int billboard_count = -10;
        for (int i = 0; i < billboard_mock.get_info().size(); i++){
            if (billboard_mock.get_info().get(i).contains(billboard_name)){
                billboard_count = i; //gets the index of the billboard
            }
        }
        if (billboard_count == - 10){
            throw new User_authentication_exception("billboard not found");
        }
        ArrayList<String> content = new ArrayList<String>(Arrays.asList(billboard_name,username,bg_colour,mssg, mssg_colour, picture, info_mssg, info_colour));
        billboard_mock.get_info().set(billboard_count, content); //sents new value
            return "can edit";
        }
        else {
            return "cannot edit";
        }

    }


    @Override
    public String delete_billboard(String username,String billboard_name) throws User_authentication_exception {
        //check permission
        String Permission_create = "Create Billboards";
        String Permission_edit = "Edit All Billboards";
        Boolean edit_billboarrd = false; //true: reuires edit user; false: create user can change too
        //check if the billboard is already in schedule
        for (int i = 0; i < schedule.get_info().size(); i++) {
            if (schedule.get_info().get(i).contains(billboard_name)) {
                //if it is than only edit user can edit it
                edit_billboarrd = true;
                break;
            }
        }
        if (!edit_billboarrd) { //false because its already on schedule only edit user can change it
            for (int i = 0; i < billboard_mock.get_info().size(); i++) {
                if (billboard_mock.get_info().get(i).contains(billboard_name)) {
                    if (billboard_mock.get_info().get(i).contains(username)) {//check if its created by same user
                        edit_billboarrd = false;
                    } else {
                        edit_billboarrd = true; // need edit user permission
                    }
                    break;
                }
            }
        }
        //check if the user hass correct permission
        Boolean permission_granted = false;
        if (!edit_billboarrd) {
            //permission required: create billboard OR edit all billbaord
            if ((check_permission.check_user_permission(username, Permission_create)) || (check_permission.check_user_permission(username, Permission_edit))) {
                permission_granted = true;
            }
        }
        if (edit_billboarrd) {
            //permission required: only edit all billboard
            if (check_permission.check_user_permission(username, Permission_edit)) {
                permission_granted = true;
            }
        }
        if (permission_granted) {
            for (int i = 0; i < schedule.get_info().size(); i++) {
                if (schedule.get_info().get(i).contains(billboard_name)) {
                    schedule.get_info().remove(i); //remove the billboard from schedule
                }
            }
            for (int i = 0; i < billboard_mock.get_info().size(); i++) {
                if (billboard_mock.get_info().get(i).contains(billboard_name)) {
                    billboard_mock.get_info().remove(i); //sents new value
                }
            }
            return "can delete";
        } else {
            return "cannot delete";
        }
    }

    /**
     * reutrns all user and their info
     * @return returns 2d list
     * @throws User_authentication_exception
     */

    public List<List<String>> show_user () throws User_authentication_exception{
         return check_permission.show_all_user();
    }


    /**
     * reutrns all user and their permission
     * @return returns 2d list
     * @throws User_authentication_exception
     */
    public List<List<String>> show_permission () throws User_authentication_exception{
        return check_permission.show_all_permissions();
    }

    @Override
    public  void delete_user(String username, String delete_user) throws User_authentication_exception {
        //delete from all the tables and also replace the user name in billboard/schedule table to admin(the startup user)
        String replace_username = "admin";
        //cehck for permission
        String Permission_edit = "Edit Users";
        if (!check_permission.check_user_permission(username,Permission_edit)){
            throw new User_authentication_exception("user does not have permission");
        }

        //user cannot delete themselves
        if (username == delete_user){
            throw new User_authentication_exception("user cannot delete themselves");
        }

        //replace from scheduling first because of foreign key constraint
        for (int i = 0; i < schedule.get_info().size(); i++) {
            if (schedule.get_info().get(i).contains(delete_user)) {
                schedule.get_info().get(i).set(1, replace_username); //replace
            }
        }
        for (int i = 0; i < billboard_mock.get_info().size(); i++) {
            if (billboard_mock.get_info().get(i).contains(delete_user)) {
                billboard_mock.get_info().get(i).set(1, replace_username); //replce

            }
        }
        check_permission.delete_user(delete_user); //deletes from the billboard and permission

    }

    @Override
    public String  edit_password(String user, String user_edit, String newpassword) throws User_authentication_exception {

        String Permission_edit = "Edit Users";
        //check if user is editing their own password
        if (!user.equals(user_edit)){
            if (!check_permission.check_user_permission(user,Permission_edit)){
                throw new User_authentication_exception("user does not have permission");
            }
        }
       return check_permission.edit_user_password(user_edit, newpassword);
    }

    @Override
    public String remove_billboard_schedule(String user, String billboardname, String time) throws User_authentication_exception {
        //
        String Permission_edit = "Schedule Billboards";
        if (!check_permission.check_user_permission(user, Permission_edit)) {
            throw new User_authentication_exception("user does not have permission");
        }
        //delete from schedule useing billboard name and time
        for (int i = 0; i < schedule.get_info().size(); i++) {
            if (schedule.get_info().get(i).contains(billboardname) & schedule.get_info().get(i).contains(time)) {
                schedule.get_info().remove(schedule.get_info().get(i));
                return "billboard removed from schedule";
            }
        }
        throw new User_authentication_exception("billboard was not deleted from schedule");
    }


    @Override
    public String schedule_billboard(String user, String billboardname, String duration, String time, String reccurance) throws User_authentication_exception {

        String Permission_edit = "Schedule Billboards";
        if (!check_permission.check_user_permission(user, Permission_edit)) {
            throw new User_authentication_exception("user does not have permission");
        }
        //add to db
        //check reccuarace- can only be daily, hourly, or any number for minutes
        if (reccurance != "daily" & reccurance != "hourly"){
            try {
                int check_if_int = Integer.parseInt(reccurance);
            }catch (Exception e){
                throw new User_authentication_exception("reccuarance can only be 'daily', 'hourly' or any number for minute");
            }
        }
        //create new array:
        ArrayList<String> new_schedule = new ArrayList<String>(Arrays.asList( billboardname, user, duration, time, reccurance)); //list of all permission
        schedule.get_info().add(new_schedule);
        return "billboard scheduled";
    }

    public List<List<String>> view_schedule() throws User_authentication_exception {
        String Permission_bill = "Schedule Billboards";
        return schedule.get_info();

    }

    //these methods are tested at user_authentication_package
    @Override
    public void create_user(String user, String user_name, List permission, String hashed_password) throws User_authentication_exception {
    }

    @Override
    public ArrayList<String> list_users(String user) throws User_authentication_exception {
        return null;
    }

    @Override
    public List<List<String>> get_user_permission(String user, String user_name) throws User_authentication_exception {
        return null;
    }

    @Override
    public void set_user_permission(String user, String username, List permissions) throws User_authentication_exception {

    }



}
