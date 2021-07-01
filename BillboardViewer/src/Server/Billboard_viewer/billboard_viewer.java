package Server.Billboard_viewer;

import Server.Database.connection_db;
import Server.Database.mock_db;
import Server.user_authentication_package.User_authentication_exception;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class billboard_viewer implements billboard_viewer_interface{
    @Override
    public String current_time(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); //datetime format
        LocalDateTime now = LocalDateTime.now();
        String current_time = dtf.format(now);
        return current_time;
    }


    @Override
    public String recurrence(String reassurance, String time ){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime end_time = LocalDateTime.parse(time, dtf);
        if (reassurance.equals("daily")){
            end_time = end_time.plusDays(1);//gets time after 24 hours
        }
        else if (reassurance.equals("hourly") ) {
            end_time = end_time.plusHours(1);//gets current time plust an hour
        }
        else{
            try {
                Integer minute = Integer.parseInt(reassurance); //gets time plus minutes
                end_time = end_time.plusMinutes(minute);
            } catch (Exception e){
                return "Wrong recurrence";
            }
        }
        String new_time_format = dtf.format(end_time);
        return new_time_format; //return expire time from current time
    }


    @Override
    public String duration(String Start_time, int duration){
        String test_current_time = Start_time;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime end_time = LocalDateTime.parse(test_current_time, dtf);
        end_time = end_time.plusMinutes(duration); //adds duration to the start time
        String tommorow_format = dtf.format(end_time);
        return tommorow_format;
    }


    @Override
    public List current_billboard() throws User_authentication_exception {
        connection_db connect = new connection_db();
        connect = new connection_db();
        connect.setRequest("view_all");
        connect.table("scheduling");
        ArrayList<List<String>> data = null;//
        ArrayList<String> datatwo = new ArrayList<>(); //stores the current schedule time
        try{
            data = (ArrayList<List<String>>) connect.DB_Connect(); //retreves the data from database

            for (int i = 0 ; i < data.size(); i++){
                int duration_bill = Integer.parseInt(data.get(i).get(2)); //converting to int
                String start_time = data.get(i).get(3);
                start_time = start_time.substring(0, start_time.indexOf('.')); //removing '.0' from the end
                String stop_time = duration(start_time, duration_bill); //the time when the billboard stops displaying
                if ((current_time().compareTo(start_time) >= 0) & (current_time().compareTo(stop_time) <= 0)){//display the billbord if the currentime is between start and end time
                    datatwo = (ArrayList<String>) data.get(i);
                }

                //update scheduling if the duration+billboard schedule time is less than current time
                if (current_time().compareTo(stop_time) > 0){//check if a billboard end time is less than curerent time;
                    String reccurance = data.get(i).get(4);
                    //new schedule
                    String new_date = recurrence(reccurance, start_time); //gives new date
                    connect = new connection_db();
                    connect.setRequest("update");
                    connect.table("scheduling");
                    String command = String.format("time = '%s'", new_date);
                    connect.setCond_one(command);
                    connect.setCond_two(String.format("time = '%s' and billboard_name = '%s'", start_time, data.get(i).get(0)));
                    connect.DB_Connect(); //update the database with new time for the billboard name
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return datatwo;
    }


    @Override
    public List return_billboards() throws User_authentication_exception {
        connection_db connect = new connection_db();
        connect.setRequest("view_all"); //get all the row and column
        connect.table("billboard"); //from billboard table
        ArrayList<List<String>> billbord_viewer = null;
        try {
            billbord_viewer = (ArrayList<List<String>>) connect.DB_Connect();//stores data from database as 2d list
        }
        catch (Exception e){
            throw new User_authentication_exception("return billbaord");
        }
        ArrayList<List<String>> billboard_name = (ArrayList<List<String>>) current_billboard(); // billboard to be showed
        if (billboard_name.size() == 0){    //if its none send null
            mock_db custom_billboard = new mock_db();
            custom_billboard.database(null, null, null, null, null, null,null);
            return custom_billboard.get_info().get(0);
        }
        ArrayList<List<String>> billboard_info = new ArrayList<List<String>>(); //store found billboard name that's need to be displayed
        for (int i = 0 ; i < billbord_viewer.size(); i++){
                if (billbord_viewer.get(i).get(0).equals(billboard_name.get(0))) {
                    billboard_info.add(billbord_viewer.get(i)); //gets the billboard to be displayed using billboard name
            }
        }
        if (billboard_info.size() != 0){
            return billboard_info.get(0); // return it
        }
        else {//if there is no on current time billboard to display: send a custom one
            mock_db custom_billboard = new mock_db();
            custom_billboard.database(null, null, null, null, null, null,null);
            return custom_billboard.get_info().get(0);
        }

    }


}
