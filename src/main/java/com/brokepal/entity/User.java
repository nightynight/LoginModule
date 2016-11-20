package com.brokepal.entity;

import java.util.Date;

/**
 * Created by Administrator on 2016/11/3.
 */
public class User {
    private long uuid;
    private String nickname;
    private String username;
    private String password;
    private String salt;
    private String email;
    private String phone;
    private int status;//激活状态
    private String activateCode;//激活码
    private String validateCode;//验证码
    private Date registerTime;//注册时间
    private Date sendActivateCodeTime;//发送激活码时间
    private Date sendValidateCodeTime;//发送验证码时间

    public User() {
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getActivateCode() {
        return activateCode;
    }

    public void setActivateCode(String activateCode) {
        this.activateCode = activateCode;
    }

    public Date getSendActivateCodeTime() {
        return sendActivateCodeTime;
    }

    public void setSendActivateCodeTime(Date sendActivateCodeTime) {
        this.sendActivateCodeTime = sendActivateCodeTime;
    }

    public Date getSendValidateCodeTime() {
        return sendValidateCodeTime;
    }

    public void setSendValidateCodeTime(Date sendValidateCodeTime) {
        this.sendValidateCodeTime = sendValidateCodeTime;
    }

    public User(String nickname, String username, String password, String salt, String email, String phone, int status, String activateCode, String validateCode, Date registerTime, Date sendActivateCodeTime, Date sendValidateCodeTime) {
        this.nickname = nickname;
        this.username = username;
        this.password = password;
        this.salt = salt;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.activateCode = activateCode;
        this.validateCode = validateCode;
        this.registerTime = registerTime;
        this.sendActivateCodeTime = sendActivateCodeTime;
        this.sendValidateCodeTime = sendValidateCodeTime;
    }

    public User(String nickname, String username, String password, String email, String phone) {
        this.nickname = nickname;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
    }

    public User(String nickname, String username, String password, String salt, String email, String phone) {
        this.nickname = nickname;
        this.username = username;
        this.password = password;
        this.salt = salt;
        this.email = email;
        this.phone = phone;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getValidateCode() {
        return validateCode;
    }

    public void setValidateCode(String validateCode) {
        this.validateCode = validateCode;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    public long getUuid() {
        return uuid;
    }

    public void setUuid(long uuid) {
        this.uuid = uuid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "User{" +
                "uuid=" + uuid +
                ", nickname='" + nickname + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", status=" + status +
                ", activateCode='" + activateCode + '\'' +
                ", validateCode='" + validateCode + '\'' +
                ", registerTime=" + registerTime +
                ", sendActivateCodeTime=" + sendActivateCodeTime +
                ", sendValidateCodeTime=" + sendValidateCodeTime +
                '}';
    }
}
