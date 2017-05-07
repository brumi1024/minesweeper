
package minesweeperSinglePlayer;



import java.text.DateFormat;

import java.text.SimpleDateFormat;

import java.util.Date;



class Timer {



    private long timeElapsed = 0; // time elapsed since first click

    private long firstClickTimestamp = 0; //



    String getTimeElapsed() {

        this.setTimeElapsed();

        return createTimeString(timeElapsed);

    }





    void setTimeFirstClick() {

        this.firstClickTimestamp = System.currentTimeMillis(); // call on launch

    }



    void setTimeElapsed() {

        this.timeElapsed = System.currentTimeMillis() - this.firstClickTimestamp;

    }





    private String createTimeString(long l_time) {

        Date date = new Date(l_time);

        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");



        return formatter.format(date);

    }

}
