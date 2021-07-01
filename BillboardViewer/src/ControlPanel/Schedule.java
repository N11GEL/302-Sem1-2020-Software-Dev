package ControlPanel;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Schedule {
    public ArrayList<ArrayList<Object>> entries;

    public Schedule(){
        entries = new ArrayList<>();
    }

    public void add(Billboard b, LocalDateTime t, int d, String r) {
        ArrayList<Object> temp = new ArrayList<>();
        temp.add(b);
        temp.add(t);
        temp.add(d);
        temp.add(r);
        entries.add(temp);
    }

    public void remove(String name, LocalDateTime time){
        for (var entry : entries){
            Billboard tempBillboard = (Billboard)entry.get(0);
            LocalDateTime tempTime = (LocalDateTime)entry.get(1);
            if (tempBillboard.name.equals(name) && tempTime.equals(time)){
                entries.remove(entry);
            }
        }
    }
}
