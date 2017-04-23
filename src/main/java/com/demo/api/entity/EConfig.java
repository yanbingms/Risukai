package com.demo.api.entity;

import javax.persistence.*;
import java.util.Date;

/**
 *
 * Created by mhh on 2017/3/26.
 */
@Entity
@Table(name = "e_config")
public class EConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long time;            // 时间间隔 单位(分钟)

    @Column(length = 32)
    private String kid;

    @Column(length = 64)
    private String secret;

    @Column(length = 30)
    private String uname;

    @Column(length = 30)
    private String password;

    @Column(columnDefinition = "char(1)")
    private String model;

    private Long loadTime;          // 更新数据时间

    private Long reTime;            // 失败重试时间

    private int reNum = 0;              // 失败重试次数

    private int interva = 0;           // 重试时间间隔 单位(分)

    @Column(length = 20, name = "good_supplier_id")
    private String goodSupplierId;      //

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getKid() {
        return kid;
    }

    public void setKid(String kid) {
        this.kid = kid;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Long getReTime() {
        return reTime;
    }

    public void setReTime(Long reTime) {
        this.reTime = reTime;
    }

    public Long getLoadTime() {
        return loadTime;
    }

    public void setLoadTime(Long loadTime) {
        this.loadTime = loadTime;
    }

    public int getReNum() {
        return reNum;
    }

    public void setReNum(int reNum) {
        this.reNum = reNum;
    }

    public int getInterva() {
        return interva;
    }

    public void setInterva(int interva) {
        this.interva = interva;
    }

    public String getGoodSupplierId() {
        return goodSupplierId;
    }

    public void setGoodSupplierId(String goodSupplierId) {
        this.goodSupplierId = goodSupplierId;
    }
}
