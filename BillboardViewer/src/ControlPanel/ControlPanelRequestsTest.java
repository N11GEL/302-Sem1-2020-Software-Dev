package ControlPanel;

import static org.junit.jupiter.api.Assertions.*;

import ControlPanel.Billboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;


class ControlPanelRequestsTest {
    ControlPanelRequests cp;

    @BeforeEach
    void ConstructControlPanel(){
        try {
            cp = new ControlPanelRequests(System.getProperty("user.dir"));
            assertFalse(cp.login("WrongUser", "WrongPass"));
            assertTrue(cp.login("admin", "Password"));
        } catch (Exception error){
            error.printStackTrace();
        }
    }

    @AfterEach
    void destroyControlPanel(){
        assert cp != null;
        cp.logout("admin");
    }

    @Test
    void listBillboardsTest(){
        Billboard billboard = new Billboard();
        billboard.fromStrings("Test", "", "A", "", "", "", "", "", "admin");
        cp.createBillboard(billboard);
        assertDoesNotThrow(() -> {
            cp.getBillboards();
        });
        assertTrue(cp.getBillboards().size() > 0);
        System.out.println(cp.getBillboards());
    }

    @Test
    void getBillboardTest(){
        listBillboardsTest();
        assertDoesNotThrow(() -> {cp.getBillboard("Test1");});
        assertEquals("Test1", cp.getBillboard("Test1").name);
    }

    @Test
    void getScheduleTest(){
        assertDoesNotThrow(() -> {cp.getSchedule();});
        getBillboardTest();
        ArrayList<Object> tempArray = new ArrayList<>();
        Billboard billboard = new Billboard();
        billboard.fromStrings("Test", "", "A", "", "", "", "", "", "admin");
        tempArray.add(billboard.name);
        tempArray.add("10");
        tempArray.add(LocalDateTime.of(2020, 05, 28, 20, 41).toString());
        tempArray.add("daily");
        assertTrue(cp.scheduleBillboard(tempArray));
        System.out.println(cp.getSchedule().entries.toString());
        assertTrue(cp.getSchedule().entries.size() > 0);
    }

    @Test
    void getUsersTest(){
        assertDoesNotThrow(() -> {cp.getUsers();});
        assertTrue(cp.getUsers().size() > 0);
    }

    @Test
    void getPermissions(){
        assertDoesNotThrow(() -> {cp.getPermissions("admin");});
        assertEquals(0, cp.getPermissions("NonExistentUser").size());
        assertTrue(cp.getPermissions("admin").size() > 0);
    }
}