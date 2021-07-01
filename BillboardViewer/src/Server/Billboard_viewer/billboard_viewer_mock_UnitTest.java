package Server.Billboard_viewer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * User test test for billboard_viewer_mock
 */

public class billboard_viewer_mock_UnitTest {
    billboard_viewer_mock viewer_test;

    @BeforeEach @Test
    public void viewer_test() {
        viewer_test = new billboard_viewer_mock();
    }

    @Test
    public void Test_currenttime() {
        String currenttime = viewer_test.current_time();
        System.out.println(currenttime);
    }

    @Test
    public void Test_currentbillboard() {
        System.out.println(viewer_test.current_billboard());
    }

    @Test
    public void Test_return_billboard() {
        System.out.println(viewer_test.return_billboards());
    }


    @Test
    public void Test_current_billboard_new() {
        System.out.println(viewer_test.current_billboard());
    }


}



