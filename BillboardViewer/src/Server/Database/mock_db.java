package Server.Database;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * creates a 2d array list depending on number of index. from 3 - 8 countion from one.
 * User for creating mock data and storing data from database
 */
class database{
    List<List<String>> database;
    public database(){
        //creates an array
        database = new ArrayList<List<String>>();
    }
    //stores values in 2d array list
    public void database(String first_entry, String second_entry, String third_entry){

        int curr_size = this.database.size();
        for(int i = curr_size; i < curr_size+1; i++)  {
            database.add(new ArrayList<String>());
        }
        int new_size = this.database.size() - 1;
        database.get(new_size).add(first_entry);
        database.get(new_size).add(second_entry);
        database.get(new_size).add(third_entry);
        return ;
    }

    //overloading for 4 values
    public void database(String first_entry, String second_entry, String third_entry, String fourth_entry){
        int curr_size = this.database.size();
        for(int i = curr_size; i < curr_size+1; i++)  {
            database.add(new ArrayList<String>());
        }
        int new_size = this.database.size() - 1;
        database.get(new_size).add(first_entry);
        database.get(new_size).add(second_entry);
        database.get(new_size).add(third_entry);
        database.get(new_size).add(fourth_entry);
        return ;
    }

    //overloading for 5 values
    public void database(String first_entry, String second_entry, String third_entry, String fourth_entry, String fifth_entry){

        int curr_size = this.database.size();
        for(int i = curr_size; i < curr_size+1; i++)  {
            database.add(new ArrayList<String>());
        }
        int new_size = this.database.size() - 1;
        database.get(new_size).add(first_entry);
        database.get(new_size).add(second_entry);
        database.get(new_size).add(third_entry);
        database.get(new_size).add(fourth_entry);
        database.get(new_size).add(fifth_entry);

        return;
    }
    //overloading for 6 values
    public void database(String first_entry, String second_entry, String third_entry, String fourth_entry, String fifth_entry, String  sixth_entry){
        int curr_size = this.database.size();
        for(int i = curr_size; i < curr_size+1; i++)  {
            database.add(new ArrayList<String>());
        }
        int new_size = this.database.size() - 1;
        database.get(new_size).add(first_entry);
        database.get(new_size).add(second_entry);
        database.get(new_size).add(third_entry);
        database.get(new_size).add(fourth_entry);
        database.get(new_size).add(fifth_entry);
        database.get(new_size).add(sixth_entry);
        return;
    }

    //overloading for 7 values
    public void database(String first_entry, String second_entry, String third_entry, String fourth_entry, String fifth_entry, String  sixth_entry, String seventh_entry){
        int curr_size = this.database.size();
        for(int i = curr_size; i < curr_size+1; i++)  {
            database.add(new ArrayList<String>());
        }
        int new_size = this.database.size() - 1;
        database.get(new_size).add(first_entry);
        database.get(new_size).add(second_entry);
        database.get(new_size).add(third_entry);
        database.get(new_size).add(fourth_entry);
        database.get(new_size).add(fifth_entry);
        database.get(new_size).add(sixth_entry);
        database.get(new_size).add(seventh_entry);
        return;
    }
    //overloading for 8 values
    public void database(String first_entry, String second_entry, String third_entry, String fourth_entry, String fifth_entry, String  sixth_entry, String seventh_entry, String eight_entry){
        int curr_size = this.database.size();
        for(int i = curr_size; i < curr_size+1; i++)  {
            database.add(new ArrayList<String>());
        }
        int new_size = this.database.size() - 1;
        database.get(new_size).add(first_entry);
        database.get(new_size).add(second_entry);
        database.get(new_size).add(third_entry);
        database.get(new_size).add(fourth_entry);
        database.get(new_size).add(fifth_entry);
        database.get(new_size).add(sixth_entry);
        database.get(new_size).add(seventh_entry);
        database.get(new_size).add(eight_entry);
        return;
    }

    /**
     * returns the database
     * @return 2d array list
     */
    public List<List<String>> get_info(){
        return database; //returns the database in arraylist
    }


}

/**
 * extnds database
 */
public class mock_db extends database {
    public static database user_mock() {
        database user = new database();
        user.database("username", "password", "salt");
        return user;
    }

    public static database scheduling_mock() {
        database scheduling = new database();
        scheduling.database("billboard_name", "user_name", "duration", "time");
        return scheduling;
    }

    public static database billboard_mock() {
        database billboard = new database();
        billboard.database("billboard_name", "bg_colour", "mssg", "mssg_colour", "picture", "info_mssg", "info_colour");
        return billboard;
    }


    public static database permission() {
        database billboard = new database();
        billboard.database("user_name", "create_billboard", "edit_billboard", "schedule_billboard", "edit_user");
        return billboard;
    }

    //tests
    public static void main(String[] args) {
        System.out.println(billboard_mock().get_info());
        System.out.println(scheduling_mock().get_info());
        System.out.println(user_mock().get_info());
        System.out.println(permission().get_info());

    }
}


















