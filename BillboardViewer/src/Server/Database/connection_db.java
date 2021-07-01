package Server.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import Server.user_authentication_package.User_authentication;
import Server.user_authentication_package.User_authentication_exception;

/**
 * Sends and receives data from the database.
 * Any data received from the database is stored in a 2D array list
 */

public class connection_db  extends  Database_commands{
    private static String request; //type of request
    private  static String table ; //name of the table
    private static String cond_one; //the first condition
    private static String cond_two; //second condition

    /**
     * creates tables if its not in the database
     * @throws SQLException
     */
    public static void check_table() throws SQLException { // creates tables if it doesnot not exist
        Connection connection = DBConnection.getInstance();
        ResultSet resultSet;
        Statement statement =  connection.createStatement();
        resultSet = statement.executeQuery("SET sql_notes = 0");// Temporarily disable the "Table already exists" warning
        resultSet.next();
        String CREATE_TABLE_USER = "create table if not exists user (user_name varchar(255) NOT NULL, password varchar(255), salt varchar(255), PRIMARY KEY (user_name), UNIQUE KEY (user_name))";
        String CREATE_TABLE_PERMISSION = "create table if not exists permission(user_name varchar(255) NOT NULL , create_billboard bool, edit_billboard bool, schedule_billboard bool, edit_user bool,  UNIQUE KEY (user_name), FOREIGN KEY (user_name) REFERENCES user(user_name))";
        String CREATE_BILLBOARD = "Create table if not exists billboard  (billboard_name varchar(255) NOT NULL, user_name varchar(255) , bg_colour varchar(255), mssg varchar(255), mssg_colour varchar(255), picture LONGTEXT, info_msg varchar(255), info_colour varchar(255), PRIMARY KEY(billboard_name), FOREIGN KEY (user_name) references user(user_name))";
        String CREATE_SCHEDULING = " create table if not exists scheduling (billboard_name varchar(255) NOT NULL, user_name varchar(255) , duration int, time DATETIME, recurrence varchar(255), FOREIGN KEY (billboard_name) REFERENCES billboard(billboard_name), FOREIGN KEY (user_name) REFERENCES user(user_name))";
        resultSet = statement.executeQuery(CREATE_TABLE_USER);
        resultSet.next();
        resultSet = statement.executeQuery(CREATE_TABLE_PERMISSION);
        resultSet.next();
        resultSet = statement.executeQuery(CREATE_BILLBOARD);
        resultSet.next();
        resultSet = statement.executeQuery(CREATE_SCHEDULING);
        resultSet.next();
        resultSet = statement.executeQuery("SET sql_notes = 1"); // And then re-enable the warning again
        resultSet.next();
    }

    /**
     * connects to the database, sends and receives information depending on what the request is
     * @return 2d array list
     * @throws SQLException
     */
    public static List<List<String>> DB_Connect() throws SQLException {
        Connection connection = DBConnection.getInstance();
        try {
            Statement statement =  connection.createStatement();
            ResultSet resultSet;
            check_table(); // check the table exist
            switch (request){
                case "insert_into": //Insert into command
                    resultSet = statement.executeQuery(insert_into(table, cond_one));
                    resultSet.next();
                    break;

                case "delete": //Delete command
                    resultSet = statement.executeQuery(delete(table,cond_one));
                    resultSet.next();
                    break;

                case "view_all": //view all command, the data is saved in 2d array
                    resultSet = statement.executeQuery("SELECT COUNT(*) AS rowcount FROM " + table);
                    resultSet.next();
                    int count = resultSet.getInt("rowcount"); //gets the row count
                    resultSet = statement.executeQuery(select_all(table));
                    resultSet.next();
                    database store = new database();
                    for (int i = 1; i < count + 1; i ++) {//count is the number of column in the table
                        String first_entry = resultSet.getString(resultSet.getMetaData().getColumnName(1));
                        String second_entry = resultSet.getString(resultSet.getMetaData().getColumnName(2));
                        String third = resultSet.getString(resultSet.getMetaData().getColumnName(3));
                        if (resultSet.getMetaData().getColumnCount() >= 4) { //if the column if 4
                            String fourth_entry = resultSet.getString(resultSet.getMetaData().getColumnName(4));
                            if (resultSet.getMetaData().getColumnCount() >= 5) {//if the column if 5
                                String fifth_entry = resultSet.getString(resultSet.getMetaData().getColumnName(5));
                                if (resultSet.getMetaData().getColumnCount() >= 6) {//if the column if 6
                                    String sixth_entry = resultSet.getString(resultSet.getMetaData().getColumnName(6));
                                    if (resultSet.getMetaData().getColumnCount() >= 7) {//if the column if 7
                                        String seventh_entry = resultSet.getString(resultSet.getMetaData().getColumnName(7));
                                        if (resultSet.getMetaData().getColumnCount() >= 8) {//if the column if 8
                                            String eight_entry = resultSet.getString(resultSet.getMetaData().getColumnName(8));
                                            store.database(first_entry, second_entry, third, fourth_entry, fifth_entry, sixth_entry, seventh_entry, eight_entry);
                                        }else {
                                            store.database(first_entry, second_entry, third, fourth_entry, fifth_entry, sixth_entry, seventh_entry);
                                        }
                                    } else {
                                        store.database(first_entry, second_entry, third, sixth_entry);
                                    }
                                } else {
                                    store.database(first_entry, second_entry, third, fourth_entry, fifth_entry);
                                }
                            } else {
                                store.database(first_entry, second_entry, third, fourth_entry);
                            }
                        } else {
                            store.database(first_entry, second_entry, third);
                        }
                        resultSet.next();
                    }
                    resultSet.next();
                    return store.get_info();

                case "select_where": //select where command for sql
                    resultSet = statement.executeQuery(String.format("SELECT COUNT(*) AS rowcount FROM %s WHERE %s ", table, cond_one));
                    resultSet.next();
                     count = resultSet.getInt("rowcount"); //gets the row count
                    resultSet = statement.executeQuery(select_row(table, cond_one));
                    resultSet.next();
                     store = new database();
                    for (int i = 1; i < count + 1; i ++) {//count is the number of column in the table{
                        String first_entry = resultSet.getString(resultSet.getMetaData().getColumnName(1));
                        String second_entry = resultSet.getString(resultSet.getMetaData().getColumnName(2));
                        String third = resultSet.getString(resultSet.getMetaData().getColumnName(3));
                        if (resultSet.getMetaData().getColumnCount() >= 4) { // because only user have 5 cloumn (permission)
                            String fourth_entry = resultSet.getString(resultSet.getMetaData().getColumnName(4));
                            if (resultSet.getMetaData().getColumnCount() >= 5) {
                                String fifth_entry = resultSet.getString(resultSet.getMetaData().getColumnName(5));
                                if (resultSet.getMetaData().getColumnCount() >= 6) {
                                    String sixth_entry = resultSet.getString(resultSet.getMetaData().getColumnName(6));
                                    if (resultSet.getMetaData().getColumnCount() >= 7) {
                                        String seventh_entry = resultSet.getString(resultSet.getMetaData().getColumnName(7));
                                        if (resultSet.getMetaData().getColumnCount() >= 8) {
                                            String eight_entry = resultSet.getString(resultSet.getMetaData().getColumnName(8));
                                            store.database(first_entry, second_entry, third, fourth_entry, fifth_entry, sixth_entry, seventh_entry, eight_entry);
                                        }else {
                                            store.database(first_entry, second_entry, third, fourth_entry, fifth_entry, sixth_entry, seventh_entry);
                                        }
                                    } else {
                                        store.database(first_entry, second_entry, third, sixth_entry);
                                    }
                                } else {
                                    store.database(first_entry, second_entry, third, fourth_entry, fifth_entry);
                                }
                            } else {
                                store.database(first_entry, second_entry, third, fourth_entry);
                            }
                        } else {
                            store.database(first_entry, second_entry, third);
                        }
                        resultSet.next();
                    }
                    resultSet.next();
                    return store.get_info();

                case "update": //update command
                    String command = String.format("UPDATE %s SET %s Where %s", table,cond_one, cond_two);
                    resultSet = statement.executeQuery(command);
                    resultSet.next();
                    break;

            }
//            statement.close();
//            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        List<List<String>> list_return =  new ArrayList<List<String>>();
        return list_return;
    }


    //get and setters
    public  void setRequest(String request) { connection_db.request = request; }
    public void table(String table){ this.table = table; }
    public  void setCond_one(String cond_one) { connection_db.cond_one = cond_one; }
    public  void setCond_two(String cond_two) { connection_db.cond_two = cond_two; }


}
