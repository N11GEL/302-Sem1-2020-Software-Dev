package Server.user_authentication_package;

import java.util.ArrayList;
import java.util.List;

public interface user_authenticaion_interface {
    /**
     * Register the user and hashes the password with the salt and stores the data on the database including the salt
     * @param user_name the username
     * @param permission list of permission the user has
     * @param hashed_password the password for the user
     * @throws User_authentication_exception
     */
     void register_user(String user_name, List permission, String hashed_password) throws User_authentication_exception;

    /**
     *Delete a user
     * @param user_name  delete this user
     * @throws User_authentication_exception
     */
     void delete_user(String user_name) throws User_authentication_exception;

    /**
     * Verify and login user
     * @param user_name
     * @param password
     * @return returns if the user is loged in or not
     * @throws User_authentication_exception
     */
     String log_in(String user_name, String password) throws User_authentication_exception;

    /**
     * edit the user password
     * @param user_ID name of the user that needs password edited
     * @param new_password new password for the user
     * @return returns if sucessful or not on changing password
     * @throws User_authentication_exception
     */
     String edit_user_password(String user_ID, String new_password) throws User_authentication_exception;

    /**
     * change the user permission
     * @param user_ID change this user permission
     * @param permission list of new permission
     * @throws User_authentication_exception
     */
     void change_permission(String user_ID, List permission) throws User_authentication_exception;

    /**
     *
     * @return
     * @throws User_authentication_exception
     */
     List show_all_user() throws User_authentication_exception;

    /**
     * shows all user permissions form the database
     * @return 2d array list of all the permissions
     * @throws User_authentication_exception
     */
     List show_all_permissions()  throws User_authentication_exception;

    /**
     * gets permission of a user
     * @param username permission of the user to be returned
     * @return returns an array list of the user permission
     * @throws User_authentication_exception
     */
     List get_user_permission(String username) throws User_authentication_exception;

    /**
     *check if the user has a required permission or not
     * @param username user
     * @param permission required permission that the user must have
     * @return returns true if the user has the permission otherwise false
     * @throws User_authentication_exception
     */
     Boolean check_user_permission(String username, String permission) throws User_authentication_exception;

    /**
     * Returns list of user names
     * @return list
     * @throws User_authentication_exception
     */
    ArrayList<String> show_username() throws User_authentication_exception;
    }
