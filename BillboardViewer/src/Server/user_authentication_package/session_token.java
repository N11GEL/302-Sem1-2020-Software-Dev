package Server.user_authentication_package;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Generates token, stores and delete them
 */
public class session_token {
    private static HashMap<String, String> user_token;
    private static HashMap<String, String> user_token_timer;

    /**
     * creates hashmap to store the the token, the user and when the token expires
     */
    public session_token(){
        user_token = new HashMap<>();
        user_token_timer = new HashMap<>();
    }


    public static String random_string(int n){ //generates random string
        String random_string = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(random_string.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(random_string
                    .charAt(index));
        }
        return sb.toString();
    }

    /**
     * creates new token for the user
     * @param user_name
     */
   public static void new_token(String user_name){
        String generate_token = random_string(5); //genrates random token
       user_token.put(user_name,generate_token);//username is the key and the token is the value
       user_token_timer.put(user_name,expire());//the expire time of the token
    }

    /**
     * deletes the user token
     * @param user_name
     */
    public void delete_token(String user_name){//delets the token
        user_token.remove(user_name);
        user_token_timer.remove(user_name);
    }

    /**
     * sets expire time for a token
     */
    public static String expire(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); //datetime format
        LocalDateTime tommorow = LocalDateTime.now().plusDays(1);//gets time after 24 hours
        String tommorow_format = dtf.format(tommorow);
        return tommorow_format; //return expire time from current time
    }

    /**
     * Gets current time
     * @return string
     */
    public static String current_time(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); //datetime format
        LocalDateTime now = LocalDateTime.now();
        String tommorow_format = dtf.format(now);
        return tommorow_format; //return expire time from current time
    }

    /**
     * compare user token current time and expire time
     * @param username
     * @return
     */
    public static int compare(String username) {//positve if equal or grateer, negaitve if lower
        return user_token_timer.get(username).compareTo(current_time());
    }

    /**
     * gets the key using the value
     * @param map hasmaap
     * @param value value
     * @return key
     */
    public static String getKeyByValue(Map<String, String> map, String value) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }


    public HashMap<String, String> getUser_token(){ //gets user time
        return user_token;
    }
    public HashMap<String, String> getUser_token_timer(){ //gets token expire time
        return user_token_timer;
    }


    /**
     * tests
     */
    @Test
    public void test(){
        new_token("adam lee");
        new_token("second  usr");

        System.out.println(getUser_token()); //gets all the user token
        System.out.println(getUser_token_timer());
        System.out.println(current_time() + " :current time");
        System.out.println(expire() + " expire date");
        System.out.println(compare("adam lee") + " : compare");
        System.out.println(getUser_token_timer().keySet());
        System.out.println("\n");

//        if (getUser_token().get("adam leee") == null){
//            System.out.println("no user found");
//        }
        System.out.println(getKeyByValue(getUser_token(),getUser_token().get("adam lee") ));



    }


}
