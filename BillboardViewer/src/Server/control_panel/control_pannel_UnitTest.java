package Server.control_panel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Server.user_authentication_package.User_authentication_exception;

import java.sql.SQLException;

public class control_pannel_UnitTest {

    control_pannel_server contol_pannel;
    @BeforeEach
    @Test
    public void viewer_test(){  contol_pannel = new control_pannel_server();}

    String billboardname = "bill the first";
    String username  = "adam lee";
    String bg_colour = "titanum white";
    String mssg = "heh";
    String mssg_colour = "Shinny silver";
    String picture = ": )";
    String into_mssg = "what is even info";
    String info_colour = "red";



    @Test
    public void Test_createbillboard() throws User_authentication_exception {

       String a =  contol_pannel.create_bilboard("billboard the last", username, bg_colour, mssg, mssg_colour, picture, into_mssg, info_colour);
        System.out.println(a);
    }

    @Test
    public void Test_editbillboard() throws User_authentication_exception, SQLException {

      String a =   contol_pannel.edit_billboard("billboard the second", username, bg_colour, mssg, mssg_colour, picture, into_mssg, info_colour);
      System.out.println(a);
    }


    @Test
    public void Test_deletebillboard() throws User_authentication_exception, SQLException {

        String a =   contol_pannel.delete_billboard(username, "billboard the fifth");
        System.out.println(a);
    }

    @Test
    public void Test_delete_user() throws User_authentication_exception, SQLException {
        contol_pannel.delete_user("admin", "new user");
    }

    @Test
    public void Test_edit_user_password() throws User_authentication_exception, SQLException {
        System.out.println(contol_pannel.edit_password("admin", "new user", "new password"));
    }


    @Test
    public void Test_set_user_permission() throws User_authentication_exception, SQLException {
        System.out.println(contol_pannel.schedule_billboard("admin", "billboard the tenth", "30", "2020-05-31 21:20:10", "12"));
    }


    @Test
    public void Test_remove_billboard_schedule() throws User_authentication_exception, SQLException {
        System.out.println(contol_pannel.remove_billboard_schedule("admin", "billboard the tenth",  "2020-05-31 21:20:10" ));
    }

    @Test
    public void Test_viewschedule() throws User_authentication_exception, SQLException {


        System.out.println(contol_pannel.view_schedule(username));
    }












}
