package com.schedule.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
    public String get(String uri) {
        try {
            // 创建 URL 对象
            URL url = new URL(uri);

            // 打开连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置请求方法为 GET
            connection.setRequestMethod("GET");

            // 设置连接超时（单位：毫秒）
            int connectTimeout = 5000; // 5秒
            connection.setConnectTimeout(connectTimeout);

            // 设置读取超时（单位：毫秒）
            int readTimeout = 5000; // 5秒
            connection.setReadTimeout(readTimeout);

            // 获取响应码
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 读取响应内容
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                System.out.println("response Info: " + response.toString());
                return response.toString();// 以格式化的方式打印 JSON
            } else {
                System.out.println("Failed to fetch user info. Response Code: " + responseCode);
            }

            // 关闭连接
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
