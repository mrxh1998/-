package com.ourProject.StudyAnalysis.entity;

import java.util.Date;

public class LoginTicket {
    private String username;
    private String ticket;
    private int status;
    private Date expired;
    private int userId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "LoginTicket{" +
                "username='" + username + '\'' +
                ", ticket='" + ticket + '\'' +
                ", status=" + status +
                ", expired=" + expired +
                ", userId=" + userId +
                '}';
    }

    public Date getExpired() {
        return expired;
    }

    public void setExpired(Date expired) {
        this.expired = expired;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
