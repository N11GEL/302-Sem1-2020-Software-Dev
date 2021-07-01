package Server.control_panel;

import Server.user_authentication_package.User_authentication;
import Server.user_authentication_package.User_authentication_exception;
import Server.Database.connection_db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class control_pannel_server implements control_panel_interface {

    @Override
    public String create_bilboard(String billboard_name, String username, String bg_colour, String mssg,
                                  String mssg_colour, String picture, String info_mssg, String info_colour) throws User_authentication_exception {
        //check if the name is not already in use
        connection_db connect;
        connect = new connection_db();
        connect.setRequest("select_where");
        connect.table("billboard");
        connect.setCond_one(String.format("billboard_name = '%s'", billboard_name));
        try {
            connect.DB_Connect();
            if (connect.DB_Connect().size() != 0) {
                return "name already in use: " + billboard_name;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //add it to database
        connect = new connection_db();
        connect.setRequest("insert_into");
        connect.table("billboard");
        connect.setCond_one(String.format("'%s','%s','%s','%s','%s','%s','%s','%s'", billboard_name, username, bg_colour, mssg, mssg_colour, picture, info_mssg, info_colour));
        try {
            connect.DB_Connect();
            return "new billboard  created";
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "new failed to create";
    }


    @Override
    public String edit_billboard(String billboard_name, String username, String bg_colour, String mssg,
                                 String mssg_colour, String picture, String info_mssg, String info_colour) throws User_authentication_exception, SQLException {
        //check if the billboard exits
        connection_db connect;
        connect = new connection_db();
        connect.setRequest("select_where");
        connect.table("billboard");
        connect.setCond_one(String.format("billboard_name = '%s'", billboard_name));
        //errror if the billboard name is already not found
        if (connect.DB_Connect().size() == 0) {
            throw new User_authentication_exception("Billboard does not exists for editing");
        }
        Boolean edit_user = false; //if true only user with edit user permission can edit the billboard
        //check if the billboard is in schedule
        connect = new connection_db();
        connect.setRequest("select_where");
        connect.table("scheduling");
        connect.setCond_one(String.format("billboard_name = '%s'", billboard_name));
        if (connect.DB_Connect().size() > 0) {
            edit_user = true; //true if billboard is in schedule
        }
        if (!edit_user) {
            connect = new connection_db();
            connect.setRequest("select_where");
            connect.table("billboard");
            connect.setCond_one(String.format("billboard_name = '%s'", billboard_name));
            if (connect.DB_Connect().size() == 0) {
                throw new User_authentication_exception("Billboard does not exists for editing");
            }
            if (connect.DB_Connect().get(0).contains(username)) { //checks if the user is editing their own billboard thats not scheduled
                edit_user = false;
            } else {
                edit_user = true;
            }
        }
        String Permission_create = "Create Billboards";
        String Permission_edit = "Edit All Billboards";
        Boolean permission_granted = false;
        User_authentication check_permission = new User_authentication();
        if (!edit_user) {
            //permission required: create billboard OR edit all billbaord
            if ((check_permission.check_user_permission(username, Permission_create)) || (check_permission.check_user_permission(username, Permission_edit))) {
                permission_granted = true;
            }
        }
        if (edit_user) {
            //permission required: only edit all billboard
            if (check_permission.check_user_permission(username, Permission_edit)) {
                permission_granted = true;
            }
        }
        if (permission_granted) { //if permission requirement is met
            //edit the billboard
            connect = new connection_db();
            connect.setRequest("update");
            connect.table("billboard");
            connect.setCond_one(String.format("billboard_name = '%s', bg_colour = '%s', mssg = '%s', mssg_colour = '%s'," +
                    "picture = '%s', info_msg = '%s', info_colour = '%s'", billboard_name, bg_colour, mssg, mssg_colour, picture, info_mssg, info_colour));
            connect.setCond_two(String.format("billboard_name = '%s'", billboard_name));
            connect.DB_Connect();
            return "can edit";
        } else {
            return "cannot edit";
        }
    }


    @Override
    public String delete_billboard(String username, String billboard_name) throws User_authentication_exception, SQLException {
        //check permission
        String Permission_create = "Create Billboards";
        String Permission_edit = "Edit All Billboards";
        Boolean edit_billboarrd = false; //true: requires edit user; false: create user can change too
        connection_db connect;
        connect = new connection_db();
        connect.setRequest("select_where");
        connect.table("billboard");
        connect.setCond_one(String.format("billboard_name = '%s'", billboard_name));
        if (connect.DB_Connect().size() == 0) {
            throw new User_authentication_exception("Billboard does not exists for editing");
        }
        //check if the billboard is already in schedule
        connect = new connection_db();
        connect.setRequest("select_where");
        connect.table("scheduling");
        connect.setCond_one(String.format("billboard_name = '%s'", billboard_name));
        //check if the billboard is already in schedule
        if (connect.DB_Connect().size() > 0) {
            edit_billboarrd = true;
        }
        if (!edit_billboarrd) {
            connect = new connection_db();
            connect.setRequest("select_where");
            connect.table("billboard");
            connect.setCond_one(String.format("billboard_name = '%s'", billboard_name));

            if (connect.DB_Connect().size() == 0) {
                throw new User_authentication_exception("Billboard does not exists for editing");
            }
            if (connect.DB_Connect().get(0).contains(username)) {
                edit_billboarrd = false;
            } else {
                edit_billboarrd = true;
            }
        }
        //check if the user has correct permission
        User_authentication check_permission = new User_authentication();
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
            //delete from the billboard from scheduling if found
            connect = new connection_db();
            connect.setRequest("delete");
            connect.table("scheduling");
            connect.setCond_one(String.format("billboard_name = '%s'", billboard_name));
            connect.DB_Connect();

            //delete the billboard from billboard table
            connect = new connection_db();
            connect.setRequest("delete");
            connect.table("billboard");
            connect.setCond_one(String.format("billboard_name = '%s'", billboard_name));
            connect.DB_Connect();
            return "can delete";
        } else {
            return "cannot delete";
        }
    }


    @Override
    public void delete_user(String username, String delete_user) throws User_authentication_exception, SQLException {
        //check if user exist first
        connection_db connect;
        connect = new connection_db();
        connect.setRequest("select_where");
        connect.table("user");
        connect.setCond_one(String.format("user_name = '%s'", delete_user));
        if (connect.DB_Connect().size() == 0) {
            throw new User_authentication_exception("User does not exists for deleting");
        }
        String replace_username = "admin"; //replace the user name if the user has name on billboard and/or schedule table
        String Permission_edit = "Edit Users";
        //check permission
        User_authentication check_permission = new User_authentication();
        if (!check_permission.check_user_permission(username, Permission_edit)) {
            throw new User_authentication_exception("user does not have permission");
        }
        //user cannot delete themselves
        if (username == delete_user) {
            throw new User_authentication_exception("user cannot delete themselves");
        }

        //replace in scheduling with new user with permission schedule user
        connect = new connection_db();
        connect.setRequest("update");
        connect.table("scheduling");
        connect.setCond_one(String.format("user_name = null"));
        connect.setCond_two(String.format("user_name = '%s'", delete_user));
        connect.DB_Connect();
        //replace in billboard  with new user with permission create billboard
        connect = new connection_db();
        connect.setRequest("update");
        connect.table("billboard");
        connect.setCond_one(String.format("user_name = null"));
        connect.setCond_two(String.format("user_name = '%s'", delete_user));
        connect.DB_Connect();
        //delete
        User_authentication user_delete = new User_authentication();
        user_delete.delete_user(delete_user);
    }


    @Override
    public String edit_password(String user, String user_edit, String newpassword) throws User_authentication_exception {
        String Permission_edit = "Edit Users";
        //check if user is editing their own password
        User_authentication check_permission = new User_authentication();
        if (!user.equals(user_edit)) {
            if (!check_permission.check_user_permission(user, Permission_edit)) {
                throw new User_authentication_exception("user does not have permission");
            }
        }
        return check_permission.edit_user_password(user_edit, newpassword);    //edit password and return acknowledgment
    }


    @Override
    public void create_user(String user, String user_name, List permission, String hashed_password) throws User_authentication_exception {
        String Permission_edit = "Edit Users";
        //check if user is editing their own password
        User_authentication check_permission = new User_authentication();
        if (!check_permission.check_user_permission(user, Permission_edit)) {
            throw new User_authentication_exception("user does not have permission");
        }
        check_permission.register_user(user_name, permission, hashed_password); //register user

    }


    @Override
    public ArrayList<String> list_users(String user) throws User_authentication_exception {
        String Permission_edit = "Edit Users";
        //check if user is editing their own password
        User_authentication check_permission = new User_authentication();
        if (!check_permission.check_user_permission(user, Permission_edit)) {
            throw new User_authentication_exception("user does not have permission");
        }

        return check_permission.show_username(); //return list of users and their info
    }


    @Override
    public List<List<String>> get_user_permission(String user, String user_name) throws User_authentication_exception {
        String Permission_edit = "Edit Users";
        //check if user is editing their own password
        User_authentication check_permission = new User_authentication();
        if (!user.equals(user_name)) {
            if (!check_permission.check_user_permission(user, Permission_edit)) {
                throw new User_authentication_exception("user does not have permission");
            }
        }
        return check_permission.get_user_permission(user_name); //return list of user permissions
    }


    @Override
    public void set_user_permission(String user, String username, List permissions) throws User_authentication_exception {
        String Permission_edit = "Edit Users";
        //user cannot change their own edit permission
        if (user.equals(username)) {
            if (!permissions.contains("Edit Users"))
            throw new User_authentication_exception("user cannot change their own 'Edit Users' permission ");
        }

        User_authentication check_permission = new User_authentication();
        if (!check_permission.check_user_permission(user, Permission_edit)) {
            throw new User_authentication_exception("user does not have permission");
        }
        check_permission.change_permission(username, permissions);
    }


    @Override
    public String schedule_billboard(String user, String billboardname, String duration, String time, String reccurance) throws User_authentication_exception, SQLException {
        User_authentication check_permission = new User_authentication();
        //check if the billboard name exist
        connection_db connect;
        connect = new connection_db();
        connect.setRequest("select_where");
        connect.table("billboard");
        connect.setCond_one(String.format("billboard_name = '%s'", billboardname));
        if (connect.DB_Connect().size() == 0) {
            throw new User_authentication_exception("Billboard does not exists for scheduling");
        }
        //check permission
        String Permission_edit = "Schedule Billboards";
        if (!check_permission.check_user_permission(user, Permission_edit)) {
            throw new User_authentication_exception("user does not have permission");
        }
        //check recurrence- can only be daily, hourly, or any number for minutes
        if (!reccurance.equals("daily") & !reccurance.equals("hourly")) {
            try {
                int check_if_int = Integer.parseInt(reccurance);
            } catch (Exception e) {
                throw new User_authentication_exception("recurrence can only be 'daily', 'hourly' or any number for minute");
            }
        }
        //add it to the database
        connect = new connection_db();
        connect.setRequest("insert_into");
        connect.table("scheduling");
        connect.setCond_one(String.format("'%s','%s','%s','%s','%s'", billboardname, user, duration, time, reccurance));
        try {
            connect.DB_Connect();
            return "billboard scheduled successfully";
        } catch (SQLException e) {
            e.printStackTrace();
            throw new User_authentication_exception(e.toString());
        }
    }


    @Override
    public String remove_billboard_schedule(String user, String billboardname, String time) throws User_authentication_exception, SQLException {
        User_authentication check_permission = new User_authentication();
        String Permission_edit = "Schedule Billboards";
        if (!check_permission.check_user_permission(user, Permission_edit)) {
            throw new User_authentication_exception("user does not have permission");
        }
        //check if it exists
        connection_db connect;
        connect = new connection_db();
        connect.setRequest("select_where");
        connect.table("scheduling");
        connect.setCond_one(String.format("billboard_name = '%s' and time = '%s'", billboardname, time));

        if (connect.DB_Connect().size() == 0) {
            throw new User_authentication_exception("this billboard does not exists in scheduling to delete");
        }
        //delete
        connect = new connection_db();
        connect.setRequest("delete");
        connect.table("scheduling");
        connect.setCond_one(String.format("billboard_name = '%s' and time = '%s'", billboardname, time));
        connect.DB_Connect();
        return "billboard successfully deleted";
    }

    public List<List<String>> view_schedule(String user) throws User_authentication_exception {
        String Permission_bill = "Schedule Billboards";

        User_authentication check_permission = new User_authentication();
        if (!check_permission.check_user_permission(user,Permission_bill)){
            throw new User_authentication_exception("user does not have permission");
        }

        connection_db connect;
        //connection_db connect = new connection_db();
        connect = new connection_db();
        connect.setRequest("view_all");
        connect.table("scheduling");
        try{
            return connect.DB_Connect();
        }
        catch (Exception e){
            throw new User_authentication_exception(e.toString());
        }


    }





}









