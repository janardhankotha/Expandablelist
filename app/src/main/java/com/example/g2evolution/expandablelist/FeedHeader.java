package com.example.g2evolution.expandablelist;

import java.util.Date;

/**
 * Created by brajabasi on 17-03-2016.
 */
public class FeedHeader {
    String headerName;
    String rowid;
    String time;
    Date date;
    String Upload;

    String Description;
    public FeedHeader(){}
    public FeedHeader(String rid, String header, String desc, String upload, Date dt, String time){
        date = dt;
        headerName = header;
        rowid = rid;
        time = time;

        Description = desc;
        Upload = upload;

    }

    public String getRowid() {
        return rowid;
    }

    public void setRowid(String rowid) {
        this.rowid = rowid;
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }



    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }


    public String getUpload() {
        return Upload;
    }

    public void setUpload(String upload) {
        Upload = upload;
    }




}
