package Server.Database;

import java.sql.ResultSet;

/**
 * returns sql commands as string
 */
public class Database_commands {
    private static String insert_into = "INSERT INTO ";
    private static String delete_row = "DELETE FROM " + "table_name" + "condition";
    private static String edit_row = "UPDATE" + "table_name" + "SET" + "coliumn1 = value..." + "WHERE" + "conditon";
    private static String view_db = "SELECT" + "*" + "FROM" + "table";

    public static String delete(String talbe_name, String condition){
        //delete a row;
        String delete_row = "DELETE FROM "+ talbe_name + " WHERE "+ condition;
        return delete_row;
    }


    public static String insert_into(String talbe_name, String column_name) {
        String insert = insert_into + talbe_name + " VALUES" + " (" + column_name + " )";
        return insert;
    }


    public static String select_all(String table_name){
            String select = "SELECT * FROM " + table_name;
            return select;
    }


    public static String select_row(String table, String constraint){
            String select = "SELECT * FROM "+ table + " WHERE " + constraint;

            return select;
    }


    public static String update(String table, String set , String where){
            String update = "UPDATE " + table + " SET " + set + " WHERE " + where;
            return update;
    }





}











