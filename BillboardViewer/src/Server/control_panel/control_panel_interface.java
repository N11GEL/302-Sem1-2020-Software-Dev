package Server.control_panel;

import Server.user_authentication_package.User_authentication_exception;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface control_panel_interface {

    /**
     * create new billboard
     * @param billboard_name billboard name
     * @param username user that created the billboard
     * @param bg_colour background colour
     * @param mssg  message
     * @param mssg_colour message colour
     * @param picture pictureURL\pictureDATA
     * @param info_mssg information message
     * @param info_colour information colour
     * @return returns a string conforming if successful or not on creating a new billboard
     * @throws User_authentication_exception
     */
     String create_bilboard(String billboard_name, String username, String bg_colour, String mssg,
                            String mssg_colour, String picture, String info_mssg, String info_colour) throws User_authentication_exception;



    /**
     *  Edits current billboard on the database
     * @param billboard_name new billboard name
     * @param username user that edited the billboard
     * @param bg_colour new background colour
     * @param mssg new message
     * @param mssg_colour new message colour
     * @param picture new pictureURL\pictureDATA
     * @param info_mssg new information message
     * @param info_colour new information colour
     * @return
     * @throws User_authentication_exception
     * @throws SQLException
     */
     String edit_billboard(String billboard_name, String username, String bg_colour, String mssg,
                           String mssg_colour, String picture, String info_mssg, String info_colour) throws User_authentication_exception, SQLException;

    /**
     * Delets the billboard and any scheduled showings of it
     * @param username the user who is deleting the billboard
     * @param billboard_name the name of the billboard
     * @return "can delete" or "cannot delete"
     * @throws User_authentication_exception
     * @throws SQLException
     */
     String delete_billboard(String username, String billboard_name) throws User_authentication_exception, SQLException;


    /**
     * delete user and replace the user name of the user if the user has createa/edited a billboard and scheduled
     * with another user with appropriate permission
     * @param username the user trying to delete
     * @param delete_user the user who is getting deleted
     * @throws User_authentication_exception
     * @throws SQLException
     */
     void delete_user(String username, String delete_user) throws User_authentication_exception, SQLException;


    /**
     * changes users password
     * @param user user who is currently logged in
     * @param user_edit user who's password is getting changed
     * @param newpassword new password
     * @return acknowledgement
     * @throws User_authentication_exception
     */
     String edit_password(String user, String user_edit, String newpassword) throws User_authentication_exception;


    /**
     * Register user
     * @param user user who is currently logged in
     * @param user_name name of the new user
     * @param permission list of permission the new user will have
     * @param hashed_password password
     * @throws User_authentication_exception
     */
     void create_user(String user, String user_name, List permission, String hashed_password) throws User_authentication_exception;


    /**
     * Returns list of users
     * @param user user who is currently logged in
     * @return 2d array list of users and their info
     * @throws User_authentication_exception
     */
     ArrayList<String> list_users(String user) throws User_authentication_exception;


    /**
     * gets the permission of a user
     * @param user user who is currently logged in
     * @param user_name user who's permissions is to be displayed
     * @return Array list of permissions in 0 1 or false or true
     * @throws User_authentication_exception
     */
     List<List<String>> get_user_permission(String user, String user_name) throws  User_authentication_exception;


    /**
     * changes users permission
     * @param user user who is currenly logged in
     * @param username user who's permission is to be changed
     * @param permissions new list of permissions
     * @throws User_authentication_exception
     */
     void set_user_permission(String user, String username, List permissions) throws User_authentication_exception;


    /**
     * Adds billboard to scheduling table in database
     * @param user user who is currently logged in
     * @param billboardname
     * @param duration
     * @param time datetime format
     * @param reccurance sets reccurance for a billboar scheduled as "daily", "hourly" or any number for minutes
     * @return acknowledgement
     * @throws User_authentication_exception
     * @throws SQLException
     */
     String schedule_billboard(String user, String billboardname, String duration, String time, String reccurance) throws User_authentication_exception, SQLException;


    /**
     * removes billboard from schedule
     * @param user user who is currenly logged in
     * @param billboardname name of the billboard to be remobed
     * @param time the scheduled time of the billboard
     * @return acknowledgement
     * @throws User_authentication_exception
     * @throws SQLException
     */
     String remove_billboard_schedule(String user, String billboardname, String time) throws User_authentication_exception, SQLException;



    }
