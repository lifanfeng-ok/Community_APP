package com.example.community;

public class ClockStates {
    private String date;// 日期 年-月-日
    private String dateType;// 1表示正常,2.异常

    public ClockStates(String date, String dateType) {
        this.date = date;
        this.dateType = dateType;
    }

    public ClockStates() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDateType() {
        return dateType;
    }

    public void setDateType(String dateType) {
        this.dateType = dateType;
    }
}
