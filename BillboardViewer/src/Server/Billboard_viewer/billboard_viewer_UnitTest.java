package Server.Billboard_viewer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Server.user_authentication_package.User_authentication_exception;

/**
 * User test test for billboard_viewer. It is used to test billboard_viewer which uses data from the database
 * So CAREFUL when running these tests as it changes the actual database data
 */
public class billboard_viewer_UnitTest {

    billboard_viewer viewer_test;
    @BeforeEach @Test
    public void viewer_test(){ viewer_test = new billboard_viewer();}


    @Test
    public void Test_currenttime(){
        String currenttime = viewer_test.current_time();
        System.out.println(currenttime);
    }


    @Test
    public void Test_currentbillboard() throws User_authentication_exception {
        System.out.println(viewer_test.current_billboard());
    }


    @Test
    public void Test_return_billboard() throws User_authentication_exception{
        System.out.println(viewer_test.return_billboards());
    }


    @Test
    public void Test_currentbillboard_new() throws User_authentication_exception {
        System.out.println(viewer_test.current_billboard());
    }

}
