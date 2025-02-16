package com.schedule.supervisory.service.impl;

import com.schedule.supervisory.service.IConfigService;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

@Service
public class IConfigServiceImpl implements IConfigService {
    @Override
    public String getTenantId() {
        String configFilePath = Paths.get(System.getProperty("user.dir"), "external-config.properties").toString();
        try {
            if (!Files.exists(Paths.get(configFilePath))) {
                new FileNotFoundException("Configuration file not found: " + configFilePath);
            } else {
                InputStream input = new FileInputStream(configFilePath);
                Properties prop = new Properties();
                prop.load(input);
                return prop.getProperty("tenant.id", "default-tenant-id");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void setTenantId(String tenantId) {

    }
}
