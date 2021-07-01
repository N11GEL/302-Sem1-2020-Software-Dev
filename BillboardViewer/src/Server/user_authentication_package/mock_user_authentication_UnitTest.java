package Server.user_authentication_package;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Arrays;

public class mock_user_authentication_UnitTest {
    mock_user_authentication user_test;
    @BeforeEach @Test
    public void setUser_test(){ user_test = new mock_user_authentication();}

    private String passowrd = "password";
    private Integer hashed_password_int = passowrd.hashCode();
    private String hashed_password_str = hashed_password_int.toString();
    private ArrayList<String> permission = new ArrayList<String>(Arrays.asList("Create Billboards",  "Schedule Billboards", "Edit Users"));
    private String token = "thisisatoken";


    @Test
    public void Test_regester_user() throws User_authentication_exception {
        user_test.register_user("Keiron Graves", permission,hashed_password_str );
        String new_password = "new_passwrod";
        user_test.register_user(" Jess Philip", permission,new_password );


    }
    @Test
    public void Test_showusernames() throws User_authentication_exception{
        Test_regester_user();
        System.out.println(user_test.show_username());

    }
    @Test
    public void Test_show_all_user() throws User_authentication_exception{
        Test_regester_user();
        //changing into string so its easier to compare
        String expected = "[[user_name, hashed_password, salt], [Keiron Graves, -1035850563, salt], [ Jess Philip, -1671085674, salt]]";
        String result = user_test.show_all_user().toString();
        assertEquals(expected,result, "show all user registration unsuccessful!!");//get(1) because 0 is the column heading
    }
    @Test
    public void Test_show_all_permission() throws User_authentication_exception{
        Test_regester_user();
        //changing into string so its easier to compare
        String expected = "[[user_name, create_billboard, edit_billbaord, schedule_billboard, edit_user], [Keiron Graves, 1, 0, 1, 1], [ Jess Philip, 1, 0, 1, 1]]";
        String result = user_test.show_all_permissions().toString();
        assertEquals(expected,result, "show all user registration unsuccessful!!");//get(1) because 0 is the column heading
    }

    @Test
    public void Test_regester_perrmission_nolist() throws User_authentication_exception{//no permission
        //tested if incorrect permission, list size grater than  5//if not permsion recieced --> exception
        permission = new ArrayList<String>(Arrays.asList());
       // assertEquals("Permission not recieved",  user_test.register_user("Keiron Graves", permission,hashed_password_str,token ), "User regesteration failed!");//get(1) because 0 is the column heading
        try {
            user_test.register_user("Keiron Graves", permission,hashed_password_str );
        }catch (User_authentication_exception e){
            assertEquals("user_authentication_package.User_authentication_exception: Permission not recieved",  e.toString() , "User regesteration failed!");//get(1) because 0 is the column heading
        }
    }


    @Test
    public void Test_regester_perrmission_greater() throws User_authentication_exception{//if there is multiple permission
        //tested if incorrect permission, --> exception
        permission = new ArrayList<String>(Arrays.asList("Admin","Edit Users","Schedule Billboards", "Admin","Edit Users","Schedule Billboards", "asdasdasd"));
        ArrayList<String> expected_show_all_permissions = new ArrayList<String>(Arrays.asList("1", "1", "0", "0", "1", "1"));
        try {
            user_test.register_user("Keiron Graves", permission, hashed_password_str);
        }catch (User_authentication_exception E){
            String expected_error = "user_authentication_package.User_authentication_exception: permission size too big (limit = 4), permission size: 7";
            assertEquals(expected_error, E.toString(), "wrong error!");
        }
    }
    @Test
    public void Test_regester_perrmission_wrong() throws User_authentication_exception{//incorrect permission
        //tested if incorrect permission --> exception
        permission = new ArrayList<String>(Arrays.asList("Admin"));
        try {
            user_test.register_user("Keiron Graves", permission,hashed_password_str );
        }catch (User_authentication_exception e){
            String expected_error = "user_authentication_package.User_authentication_exception: Admin :permission not found!";
            assertEquals(expected_error, e.toString(), "wrong error");
        }
        System.out.println(user_test.show_all_permissions());
    }

    @Test
    public void test_login() throws User_authentication_exception{
        Test_regester_user();//reger the user first
        user_test.register_user("Dixie Lott", permission,hashed_password_str );
        assertEquals("Log in sucessful",user_test.log_in("Dixie Lott", hashed_password_str), "Login unsuccessful");
    }

    @Test
    public void test_login_password_fail() throws User_authentication_exception { //if wrong password
        Test_regester_user();//reger the user first
        user_test.register_user("Dixie Lott", permission, "test");
        assertEquals("Wrong password", user_test.log_in("Dixie Lott", "test_new_password"), "Login unsuccessful");
    }

    @Test
    public void test_delete_user() throws User_authentication_exception{
        Test_regester_user();
        user_test.register_user("Dixie Lott", permission, "test");
        user_test.delete_user("Dixie Lott");
        String expected_user = "[[user_name, hashed_password, salt], [Keiron Graves, -1035850563, salt], [ Jess Philip, -1671085674, salt]]";
        String expecteed_permission = "[[user_name, create_billboard, edit_billbaord, schedule_billboard, edit_user], [Keiron Graves, 1, 0, 1, 1], [ Jess Philip, 1, 0, 1, 1]]";
        String result = user_test.show_all_user().toString();
        String result_permission = user_test.show_all_permissions().toString();
        assertEquals(expected_user,result, "user delete failed, user");

        assertEquals(expecteed_permission,result_permission, "user delete failed, permission");
    }
    @Test
    public void test_delete_nouser() throws User_authentication_exception{//test for no invalid user
        Test_regester_user();
        String expected = "user_authentication_package.User_authentication_exception: user name not found!";
        String result = user_test.show_all_user().toString();
        try {
            user_test.delete_user("Dixie Lott");
        }catch (User_authentication_exception e){
            assertEquals(expected,e.toString());
        }
    }
    @Test
    public void test_login_no_user() throws User_authentication_exception{
        Test_regester_user();//reger the user first
    try {
        user_test.log_in("Dixie Lott", hashed_password_str);
    }catch (Exception e){
        assertEquals("user_authentication_package.User_authentication_exception: user not found!",e.toString() );
    }
    }

    @Test
    public void test_edit_user() throws User_authentication_exception{
        Test_regester_user();

         user_test.register_user("Dixie Lott", permission, "test");
        System.out.println( user_test.show_all_user());
        assertEquals("password change successful!", user_test.edit_user_password("Dixie Lott", "diffrentpassword"));
        System.out.println( user_test.show_all_user());
    }
    @Test
    public void test_new_edit_password() throws User_authentication_exception{
        user_test.register_user("Dixie Lott", permission, "test");
        user_test.edit_user_password("Dixie Lott", "diffrentpassword");
        assertEquals("Log in sucessful",user_test.log_in("Dixie Lott", "diffrentpassword"), "Login unsuccessful");
    }

    @Test
    public void test_change_permission() throws User_authentication_exception{
        Test_regester_user();
        permission = new ArrayList<String>(Arrays.asList("Edit Users","Schedule Billboards"));
        user_test.register_user("Dixie Lott", permission, "test");
       // assertEquals("password change successful!", user_test.show_all_permissions());
        permission = new ArrayList<String>(Arrays.asList("Edit Users","Schedule Billboards", "Edit All Billboards"));
        user_test.change_permission("Dixie Lott", permission);
        assertEquals("[[user_name, create_billboard, edit_billbaord, schedule_billboard, edit_user], [Keiron Graves, 1, 0, 1, 1], [ Jess Philip, 1, 0, 1, 1], [Dixie Lott, 0, 1, 1, 1]]", user_test.show_all_permissions().toString());
    }


    @Test
    public void Test_get_user_permission() throws User_authentication_exception {
        Test_regester_user();
        String username = "Dixie Lotte";
        user_test.register_user("Dixie Lotte", permission, "test");

        System.out.println(user_test.get_user_permission(username));

    }


    @Test
    public void Test_check_user_permission() throws User_authentication_exception {
        Test_regester_user();
        String username = "Dixie Lotte";
        user_test.register_user(username, permission, "test");


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
