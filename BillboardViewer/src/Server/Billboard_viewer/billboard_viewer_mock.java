package Server.Billboard_viewer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import Server.Database.mock_db;


public class billboard_viewer_mock implements billboard_viewer_interface {
    mock_db billbord_viewer;
    mock_db schedule;

    public billboard_viewer_mock() { //inisiates new mock database
        billbord_viewer = new mock_db();
        schedule = new mock_db();

        billbord_viewer.database("billboard_name", "bg_colour", "mssg", "mssg_colour", "picture", "info_mssg", "info_colour");
        schedule.database("billboard_name", "user_name", "duration", "time");

        initiate_mock_DB();

    }

    public void initiate_mock_DB() {//adds values to mock db
        billbord_viewer.database("billboard one", "red", "this is a message", "red", "picutre", "this is info", "black");
        billbord_viewer.database("billboard two", "green", "this is a message", "white", "picutre", "this is info", "white");
        billbord_viewer.database("billboard three", "yellow", "this is a message", "purple", "picutre", "this is info", "silver");

        schedule.database("billboard one", "user one", "15", "2020-05-05 19:36:30", "daily");
        schedule.database("billboard two", "user two", "15", "2020-05-05 19:38:30", "hourly");
        schedule.database("billboard three", "user three", "15", "2020-05-05 20:13:30", "13");
    }


    @Override
    public String current_time() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); //datetime format
        LocalDateTime now = LocalDateTime.now();
        String current_time = dtf.format(now);
        String test_current_time = "2020-05-05 19:36:46"; // for testing only
        return test_current_time;
    }


    @Override
    public String recurrence(String reassurance, String time) {
        String test_current_time = time; // for testing only
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime end_time = LocalDateTime.parse(test_current_time, dtf);
        if (reassurance.equals("daily")) {
            end_time = end_time.plusDays(1);//gets time after 24 hours
        } else if (reassurance.equals("hourly")) {
            end_time = end_time.plusHours(1);//gets current time plust an hour
        } else {
            try {
                Integer minute = Integer.parseInt(reassurance); //gets time plus minutes
                end_time = end_time.plusMinutes(minute);
            } catch (Exception e) {
                return "Wrong recurrence";
            }
        }
        String new_time_format = dtf.format(end_time);
        return new_time_format; //return expire time from current time
    }


    @Override
    public String duration(String Start_time, int duration) {
        String test_current_time = Start_time; // for testing only
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime end_time = LocalDateTime.parse(test_current_time, dtf);
        end_time = end_time.plusSeconds(duration);
        String tommorow_format = dtf.format(end_time);
        return tommorow_format;
    }

    @Override
    public List current_billboard() {
        //get billboard with current time form schedule
        String current_time = current_time();
        ArrayList<String> billboard_name = new ArrayList<>(); //store found billboard id in current time
        for (int i = 1; i < schedule.get_info().size(); i++) { //skip the column names
            //check the duration
            String duration_str = schedule.get_info().get(i).get(2);
            int duration = Integer.parseInt(schedule.get_info().get(i).get(2));
            //get the start time
            String start_time = schedule.get_info().get(i).get(3);
            //get when it stop time
            String stop_time = duration(start_time, duration);
            //get the current time
            //check if current time is grater than the start and less than the stop
            if ((current_time().compareTo(start_time) >= 0) & (current_time().compareTo(stop_time) <= 0)) {
                return schedule.get_info().get(i);
            }
        }
        //check fore reccuarance
        return billboard_name;
    }


    @Override
    public List return_billboards() {
        List billboard_id = current_billboard();
        ArrayList<List<String>> billboard_info = new ArrayList<List<String>>(); //store found billboard id in current time
        for (int i = 0; i < billbord_viewer.get_info().size(); i++) {
            for (int j = 0; j < billboard_id.size(); j++) {
                if (billbord_viewer.get_info().get(i).get(0).equals(billboard_id.get(j))) {
                    billboard_info.add(billbord_viewer.get_info().get(i));
                }
            }
        }
        if (billboard_info.size() != 0) {
            return billboard_info;
        } else {//if there is not billboard to display: send a custom one
            mock_db custom_billboard = new mock_db();
            custom_billboard.database("custom billboard", "custom colour", "custom message", "custom message color", "picture", "custome info", "custome info colour");
            return custom_billboard.get_info();
        }
    }


}
