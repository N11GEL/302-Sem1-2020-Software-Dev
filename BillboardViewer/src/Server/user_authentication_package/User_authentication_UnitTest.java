package Server.user_authentication_package;

import Server.Database.connection_db;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/*
                            TO: MAKE IT ONLY PEOPLE WITH PERMISSION USE CERTAIN METHODS
*/
public class User_authentication_UnitTest {
    User_authentication user_test;

    @BeforeEach @Test

    public void setUser_test() {
        user_test = new User_authentication() ;
    }

    @Test
    public void test_regester_user() throws User_authentication_exception {
        ArrayList<String> permission = new ArrayList<String>(Arrays.asList("Create Billboards"));
        //Adam Lee password = password_one ; Bowemen passsword: Password.1; (password_two Adam lee user 3)
        user_test.register_user("new user", permission,"password");

    }
    @Test

    public void test_delete_user() throws User_authentication_exception{
        user_test.delete_user("new user");//also deltes permission
    }

    @Test
    public void test_hash_password() throws User_authentication_exception{
        String has_password = "password";
        HashMap a = user_test.hash_passowrd(has_password.toString(), "salt");
        System.out.println(a.get("salt"));
    }
    @Test
    /*
        retruns username not found if there is no given username in the database
        returns log in failed if the hash password is different to the password in database
        returs successful if the match is sucess

     */
    public void test_log_in() throws User_authentication_exception {
        String has_password = "test_password";
       // user_test.log_in("Otto Beil", has_password);
        System.out.println(user_test.log_in("Zishan Fry", has_password));

    }
    @Test
    public void test_show_all_user_n_permission() throws User_authentication_exception {

        user_test.show_all_user();
        System.out.println(user_test.show_all_user());
        System.out.println(user_test.show_all_permissions());

    }


    @Test
    public void test_edit_password() throws User_authentication_exception{//
        user_test.edit_user_password("Zishan Fry", "password");
    }
    @Test
    public void test_edit_permission() throws User_authentication_exception{
        ArrayList<String> permission = new ArrayList<String>(Arrays.asList("Edit Users","Schedule Billboards", "Create Billboards", "Edit All Billboards"));
        user_test.change_permission("new user", permission);
    }

    @Test
    public void test_new_table() throws  User_authentication_exception{
        try {
            connection_db.check_table();
        }
        catch (Exception e){
            System.out.println("asdasd");
        }
    }


    @Test
    public void Test_get_user_permission() throws User_authentication_exception {

        String username = "adam lee";
        System.out.println(user_test.get_user_permission(username));

    }

    @Test
    public void Test_check_user_permission() throws User_authentication_exception {
        String username = "Zishan Fry";
        System.out.println(user_test.get_user_permission(username));
        if (user_test.check_user_permission(username, "Edit All Users")){
            System.out.println("user has permission");
        }else{
            System.out.println("user does not haave permissiion: Edit All Users");
        }

        if (user_test.check_user_permission(username, "Schedule Billboards")){
            System.out.println("user has permission");
        }else{
            System.out.println("user does not  permissiion: Schedule Billboards ");
        }

        if (user_test.check_user_permission(username, "Create Billboards")){
            System.out.println("user has permission");
        }else{
            System.out.println("user does not  permissiion: Create Billboards ");
        }
        if (user_test.check_user_permission(username, "Edit All Billboards")){
            System.out.println("user has permission");
        }else{
            System.out.println("user does not  permissiion:Edit All Billboards ");
        }
    }



}
