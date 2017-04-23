package com.demo.api.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * Created by mhh on 2017/3/16.
 */
@Configuration
@EnableAutoConfiguration
@PropertySource(value= "classpath:/conf.properties")
public class ConfProperties {

    @Value("${client.id}")
    private String clientId;

    @Value("${client.secret}")
    private String clientSecret;

    @Value("${file.csv.path}")
    private String fileCsvPath;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getFileCsvPath() {
        return fileCsvPath+new SimpleDateFormat("yyyyMMdd").format(new Date())+ File.separator;
    }

    public void setFileCsvPath(String fileCsvPath) {
        this.fileCsvPath = fileCsvPath;
    }
}
