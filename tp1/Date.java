import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

class Date {
    int day;
    int month;
    int year;

    public Date() {
        this.day = this.month = this.year = 0;
    }

    public void setDate(int day, int month, int year){
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public String getDate(){
        return Integer.toString(day) + "/" + Integer.toString(month) + "/" + Integer.toString(year);
    }
}