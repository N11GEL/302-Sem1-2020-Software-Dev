package ControlPanel;

import ControlPanel.Billboard;

import java.time.LocalDateTime;

import java.util.ArrayList;

/***
 * NOTE: I have created mocks for those which return meaningful data. Not just acknowledgements.
 * This is as it is not meaningful to create a mock of just returning "True".
 */
public class Mock {
    String currentSessionToken = "";
    public String loginRequest(String user, String pass) throws Exception{
        if (user.equals("CorrectUser") && pass.equals("CorrectPass")){
            currentSessionToken = "1234";
            return "1234";
        } else {
            throw new Exception("Invalid login credentials!");
        }
    }

    public ArrayList<Billboard> listBillboards(String sessionToken) throws Exception{
        ArrayList<Billboard> billboards = new ArrayList<>();
        if (sessionToken.equals(currentSessionToken) && sessionToken.length() > 0) {
            Billboard b1 = new Billboard();
            b1.fromStrings("Test1", "", "TestMessage", "", "https://www.example.image", "", "TestInfo", "", "User1");
            billboards.add(b1);

            Billboard b2 = new Billboard();
            b2.fromStrings("Test2", "", "TestMessage2", "", "", "asdasdafaftwef412413", "TestInfo2", "", "CorrectUser");
            billboards.add(b2);

            return billboards;
        } else {
            throw new Exception("Invalid session token!");
        }
    }

    public Billboard getBillboardInfo(String billboardName, String sessionToken) throws Exception{
        if (sessionToken.equals(currentSessionToken)) {
            for (Billboard b : listBillboards(sessionToken)){
                System.out.println(b.name);
                if (b.name.equals(billboardName)){
                    return b;
                }
            }
            throw new Exception("No billboard with that name found!");
        } else {
            throw new Exception("Invalid session token!");
        }
    }

    public Schedule viewSchedule(String sessionToken) throws Exception{
            Billboard b1 = new Billboard();
            b1.fromStrings("Test1", "", "TestMessage", "", "https://www.example.image", "", "TestInfo", "", "User1");

            Billboard b2 = new Billboard();
            b2.fromStrings("Test2", "", "TestMessage2", "", "https://www.example.image2", "", "TestInfo2", "", "CorrectUser");

            Billboard b3 = new Billboard();
            b3.fromStrings("Test3", "", "TestMessage3", "", "https://www.example.image3", "", "TestInfo3", "", "User3");

            Billboard b4 = new Billboard();
            b4.fromStrings("Test4", "", "TestMessage4", "", "https://image.4", "", "TestInfo4", "", "User4");
            Schedule s = new Schedule();

            s.add(b1, LocalDateTime.of(2020, 5, 28, 3, 59), 65, "daily");
            //s.add(b2, LocalDateTime.of(2020, 5, 20, 4, 30), 15);
            //s.add(b3, LocalDateTime.of(2020, 5, 20, 19, 50), 65);
            //s.add(b4, LocalDateTime.of(2020, 5, 15, 5, 50), 4);

            return s;
    }

    public ArrayList<ArrayList<String>> listUsers(String sessionToken) throws Exception{
        //if (sessionToken.equals(currentSessionToken)){
            ArrayList<ArrayList<String>> userList = new ArrayList<>();
            ArrayList<String> user1 = new ArrayList<>();
            ArrayList<String> user2 = new ArrayList<>();
            ArrayList<String> user3 = new ArrayList<>();
            ArrayList<String> user4 = new ArrayList<>();

            user1.add("User1");
            user1.add("Edit users");
            user2.add("User2");
            user2.add("Schedule billboards");
            user3.add("User3");
            user3.add("Create billboards");
            user4.add("User4");
            user4.add("Edit users");
            user4.add("Schedule billboards");

            userList.add(user1);
            userList.add(user2);
            userList.add(user3);
            userList.add(user4);

            return userList;
        //} else {
        //    throw new Exception("Invalid session token!");
        //}
    }

    public ArrayList<String> listUserPermissions(String name, String sessionToken) throws Exception {
            if (name.equals("User1")) {
                ArrayList<String> userPermissions = new ArrayList<>();
                userPermissions.add("Create Billboards");
                userPermissions.add("Edit users");
                userPermissions.add("Schedule billboards");
                userPermissions.add("Edit All Billboards");
                return userPermissions;
            } else {
                throw new Exception("User doesn't exist!");
            }
    }

    public boolean logoutRequest(String sessionToken){
        currentSessionToken = "";
        return true;
    }
}
