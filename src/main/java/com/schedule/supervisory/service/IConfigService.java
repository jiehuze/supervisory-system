package com.schedule.supervisory.service;

public interface IConfigService {
    String getExternConfig(String name);

    void setExternConfig(String name, String value);

    boolean getConfig(String tenantId);
}
