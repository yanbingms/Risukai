package com.demo.api.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by mhh on 2017/3/25.
 */
@Entity
@Table(name = "e_account")
public class EAccount {

    public final static long ONE_DAY = 24 * 60 * 60 * 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String uid;

    private String state;

    private String refreshToken;

    @Temporal(TemporalType.TIMESTAMP)
    private Date invalidRefresh;            // 失效时间

    private String accessToken;

    @Temporal(TemporalType.TIMESTAMP)
    private Date invalidAccess;

    public boolean isRefreshValid() {
        return invalidRefresh != null && System.currentTimeMillis() <= invalidRefresh.getTime();
    }

    public boolean isAccessValid() {
        return invalidAccess != null && System.currentTimeMillis() <= invalidAccess.getTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Date getInvalidRefresh() {
        return invalidRefresh;
    }

    public void setInvalidRefresh(Date invalidRefresh) {
        this.invalidRefresh = invalidRefresh;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Date getInvalidAccess() {
        return invalidAccess;
    }

    public void setInvalidAccess(Date invalidAccess) {
        this.invalidAccess = invalidAccess;
    }
}
