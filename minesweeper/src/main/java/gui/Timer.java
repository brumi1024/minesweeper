package gui;

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
        long time_s = 0; // sec
        long time_min = 0;  // minute
        long time_h = 0;    // hour
        String timeString = ""; // return string

        if (l_time > 60 && l_time < 3600) {
            time_min = l_time / 60;
            time_s = l_time % 60;
        } else if (l_time > 3600) {
            time_h = l_time / 3600;
            time_min = (l_time - time_h * 3600) / 60;
            time_s = (l_time - time_h * 3600) % 60;

        } else {
            time_s = l_time;
        }

        timeString = timeString.concat(String.valueOf(time_h)).concat(":").concat(String.format("%02d", time_min)).concat(":").concat(String.format("%02d", time_s));

        return timeString;
    }
}
