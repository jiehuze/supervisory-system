package com.schedule.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

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

    public String get(String url, Map<String, String> params, String token) {
        try {
            // 创建HttpClient实例
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2) // 可选，指定HTTP协议版本
                    .connectTimeout(Duration.ofSeconds(10)) // 设置连接超时时间
                    .build();

            // 创建HttpRequest，设置请求方法为GET，并在Headers中添加Authorization
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header("Authorization", "Bearer " + token) // 在这里添加token
                    .GET() // 明确指定这是一个GET请求
                    .build();

            // 发送请求并接收响应
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 打印响应状态码和响应体
            System.out.println("Response Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String uploadFile(String url, String token, String tenantId) {
//        String url = "http://113.207.111.33:48770/admin/sys-file/upload";
        File uploadFile = new File("/Users/jiehu/works/test/replacefile/templete.doc"); // 替换为你要上传的文件路径
//        String token = "your-token-value"; // 你的token值
//        String tenantId = "your-tenant-id"; // 你的tenant-id值

        OkHttpClient client = new OkHttpClient();

        // 创建请求体，使用MultipartBody构建multipart/form-data请求
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", uploadFile.getName(),
                        RequestBody.create(uploadFile, okhttp3.MediaType.parse("application/octet-stream")))
                .build();

        // 创建请求对象，并添加头部信息
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + token) // 设置Authorization头部
                .header("tenant-id", tenantId) // 设置tenant-id头部
                .post(requestBody)
                .build();

        try {
            // 同步执行请求
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                System.out.println("File uploaded successfully: " + response.body().string());
            } else {
                System.out.println("Failed to upload file: " + response.code() + " " + response.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String upload(String url, String token, String tenantId, String filePath) {
        // 创建 OkHttpClient
        OkHttpClient client = new OkHttpClient();

        // 要上传的文件
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("文件不存在：" + filePath);
            return null;
        }

        // 设置 multipart/form-data
        RequestBody fileBody = RequestBody.create(file, MediaType.parse("multipart/form-data"));
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), fileBody)
                .build();

        // 创建请求
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Authorization", token)
                .addHeader("tenant-id", tenantId)
                .addHeader("Content-Type", "multipart/form-data") // 确保 Content-Type 正确
                .build();

        // 发送请求
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String jsonstr = response.body().string();
                System.out.println("文件上传成功：" + jsonstr);
                // 解析JSON字符串为JSONObject对象
                JSONObject jsonObject = JSON.parseObject(jsonstr);

                // 获取"data"节点
                JSONObject dataNode = jsonObject.getJSONObject("data");

                if (dataNode != null && dataNode.containsKey("url")) {
                    // 如果存在url节点，则打印其值
                    String jsonurl = dataNode.getString("url");
                    System.out.println("URL: " + jsonurl);

                    return jsonurl;
                } else {
                    // 如果没有找到url节点，则给出提示
                    System.out.println("Warning: URL not found in the JSON.");
                }
            } else {
                System.out.println("文件上传失败，错误码：" + response.code() + "，错误信息：" + response.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) {
        HttpUtil httpUtil = new HttpUtil();
//        String s = httpUtil.uploadFile("http://113.207.111.33:48770/admin/sys-file/upload", "f4bf7edc-4220-40e3-8187-d99c56425776", "1877665103373783042");

        httpUtil.upload("http://113.207.111.33:48770/api/admin/sys-file/upload", "Bearer 9dadc78f-6fd5-4a09-8de7-d6fa181c7b06", "1877665103373783042", "/Users/jiehu/works/test/replacefile/templete.doc");
    }
}
