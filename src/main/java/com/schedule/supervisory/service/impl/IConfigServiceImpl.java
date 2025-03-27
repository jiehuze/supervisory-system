package com.schedule.supervisory.service.impl;

import com.schedule.common.BaseResponse;
import com.schedule.common.Licence;
import com.schedule.supervisory.service.IConfigService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

@Service
public class IConfigServiceImpl implements IConfigService {
    @Override
    public String getExternConfig(String name) {
        String configFilePath = Paths.get(System.getProperty("user.dir"), "external-config.properties").toString();
        try {
            if (!Files.exists(Paths.get(configFilePath))) {
                new FileNotFoundException("Configuration file not found: " + configFilePath);
            } else {
                InputStream input = new FileInputStream(configFilePath);
                Properties prop = new Properties();
                prop.load(input);
                return prop.getProperty(name, "");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void setExternConfig(String name, String value) {
        String configFilePath = Paths.get(System.getProperty("user.dir"), "external-config.properties").toString();
        try {
            // 检查文件是否存在
            if (!Files.exists(Paths.get(configFilePath))) {
                // 如果文件不存在，则创建新文件
                Files.createFile(Paths.get(configFilePath));
            }

            // 加载现有的配置
            Properties prop = new Properties();
            try (InputStream input = new FileInputStream(configFilePath)) {
                prop.load(input);
            } catch (FileNotFoundException e) {
                // 如果文件是新创建的，这里会抛出 FileNotFoundException，可以忽略
            }

            // 设置新的配置值（这会更新已有的键或添加新的键）
            prop.setProperty(name, value);

            // 将更新后的属性存储回文件
            try (OutputStream output = new FileOutputStream(configFilePath)) {
                prop.store(output, null);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to update configuration in file: " + configFilePath, e);
        }
    }

    @Override
    public boolean getConfig(String tenantId) {
        if (!Licence.getLicence()) {
            String tenantIdex = getExternConfig("tenant.id");
            if (!tenantId.equals(tenantIdex)) {
                return false;
            } else {
                Licence.addLicenceNum();
                if (Licence.getLicenceNum() > 100) {
                    return false;
                }
            }
        }
        return true;
    }
}
