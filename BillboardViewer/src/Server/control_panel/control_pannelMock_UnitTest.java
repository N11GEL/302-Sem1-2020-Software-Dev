package Server.control_panel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Server.user_authentication_package.User_authentication_exception;

import java.sql.SQLException;

public class control_pannelMock_UnitTest {
    control_pannelMock contol_pannel;
    @BeforeEach
    @Test
    public void viewer_test() throws User_authentication_exception {  contol_pannel = new control_pannelMock();}

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

        contol_pannel.create_bilboard("billboard one", username, bg_colour, mssg, mssg_colour, picture, into_mssg, info_colour);
    }
    @Test
    public void Test_editbillboard_edit() throws  User_authentication_exception{

        String a = contol_pannel.edit_billboard("billboard two", username, bg_colour, mssg, mssg_colour, picture, into_mssg, info_colour);
        System.out.println(contol_pannel.billboard_mock.get_info());
        System.out.println(a);
    }
    @Test
    public void Test_editbillboard_editor() throws  User_authentication_exception{

        String a = contol_pannel.edit_billboard("billboard one", username, bg_colour, mssg, mssg_colour, picture, into_mssg, info_colour);
        System.out.println(contol_pannel.billboard_mock.get_info());
        System.out.println(a);
    }

    @Test
    public void Test_editbillboard_invalidepermission() throws  User_authentication_exception{

        String a = contol_pannel.edit_billboard("billboard one", "test one", bg_colour, mssg, mssg_colour, picture, into_mssg, info_colour);
        System.out.println(contol_pannel.billboard_mock.get_info());
        System.out.println(a);
    }

    @Test
    public void Test_delete_Billboard() throws User_authentication_exception{
        System.out.println(contol_pannel.billboard_mock.get_info());
        System.out.println(contol_pannel.schedule.get_info());
        String a = contol_pannel.delete_billboard(username, "billboard two");
        System.out.println(a);
        System.out.println(contol_pannel.billboard_mock.get_info());
        System.out.println(contol_pannel.schedule.get_info());
    }

    @Test
    public void Test_delete_user() throws User_authentication_exception{

       contol_pannel.delete_user("test one", "adam lee");
        System.out.println(contol_pannel.billboard_mock.get_info());
        System.out.println(contol_pannel.schedule.get_info());
        System.out.println(contol_pannel.show_permission());
        System.out.println(contol_pannel.show_user());

    }
    @Test
    public void Test_edit_paassword() throws User_authentication_exception, SQLException {
        System.out.println(contol_pannel.show_user());
       System.out.println(contol_pannel.edit_password("test one", "adam lee", "newpasswozrd"));
        System.out.println(contol_pannel.show_user());
    }

    @Test
    public void Test_remove_billboard_schedule() throws User_authentication_exception, SQLException {
        System.out.println(contol_pannel.show_permission());
        contol_pannel.remove_billboard_schedule("test one", "billboard two", "2020-05-05 19:38:30" );
        System.out.println();
    }

    @Test
    public void Test_schedule_billboard() throws User_authentication_exception, SQLException {
        contol_pannel.schedule_billboard("test one", "billboard two", "30","2020-05-05 19:38:30" , "hourly");
        System.out.println();
    }









}
