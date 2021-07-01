package Server.Billboard_viewer;

import Server.user_authentication_package.User_authentication_exception;

import java.util.List;


public interface billboard_viewer_interface {
    /**
     * gets current time
     * @return returns current time in "yyyy-MM-dd HH:mm:ss" format
     */
     String current_time();

    /**
     * gets current billboard to be displayed in current time
     * @return return teh name of the billboard to be discplayed
     * @throws User_authentication_exception
     */
     List current_billboard() throws User_authentication_exception;

    /**
     *returns billboard that's to be displayed on current time
     * @return arraylist of the billboard contents to be displayed
     * @throws User_authentication_exception
     */
     List return_billboards() throws User_authentication_exception;

    /** finds when the next recurrence date/time
     * @param recurrence daily,hourly or number in string for minutes
     * @param time time thats need to be changed
     * @return gives new time depending on ressurance
     */
     String recurrence(String recurrence, String time);

    /**
     * Finds the duration of a time in "yyyy-MM-dd HH:mm:ss" format
     * @param Start_time
     * @param duration
     * @return returns new time
     */
     String duration(String Start_time, int duration);
}
