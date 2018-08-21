package com.ef;

public class Params {

    private String startDate;

    private String duration;

    private String threshold;

    private String pathToFile;

    public Params(String startDate, String duration, String threshold, String pathToFile) {
        this.startDate = startDate;
        this.duration = duration;
        this.threshold = threshold;
        this.pathToFile = pathToFile;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getThreshold() {
        return threshold;
    }

    public void setThreshold(String threshold) {
        this.threshold = threshold;
    }

    public String getPathToFile() {
        return pathToFile;
    }

    public void setPathToFile(String pathToFile) {
        this.pathToFile = pathToFile;
    }

    @Override
    public String toString() {
        return "Params{" +
                "startDate='" + startDate + '\'' +
                ", duration='" + duration + '\'' +
                ", threshold='" + threshold + '\'' +
                ", pathToFile='" + pathToFile + '\'' +
                '}';
    }
}
