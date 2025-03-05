package com.schedule.common;

import com.alibaba.fastjson.JSON;
import com.schedule.supervisory.dto.RoleDeptRequestDTO;
import com.schedule.supervisory.dto.TokenRespDTO;
import com.schedule.supervisory.dto.UserDataDTO;
import com.schedule.utils.HttpUtil;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Data
public class YkbMessage {
    private TokenRespDTO tokenRespDTO;

    public YkbMessage(String url) {
        HttpUtil httpUtil = new HttpUtil();
        this.tokenRespDTO = httpUtil.oauthen2(url);
        log("getRoleUserId oauthen2 token: " + tokenRespDTO.toString());
    }

    public ArrayList<String> getRoleUserId(String url, List<String> roleList, List<String> deptList) {
        HttpUtil httpUtil = new HttpUtil();
        ArrayList<String> userIdList = new ArrayList<>();
        RoleDeptRequestDTO requestDTO = new RoleDeptRequestDTO();
        requestDTO.setRoleCodes(roleList);
        requestDTO.setDeptIds(deptList);
        log("RoleDeptRequestDTO: " + requestDTO.toString());

        String jsonString = JSON.toJSONString(requestDTO);
        log("getUserListByRoleCodeList request json: " + jsonString);

        String userListData = httpUtil.post(url,
                String.format("%s %s", tokenRespDTO.getToken_type(), tokenRespDTO.getAccess_token()),
                "1877665103373783042",
                jsonString);
//        log("getUserListByRoleCodeList listdata: " + userListData);

        if (userListData != null) {
            // 当code为0时，将data解析为List<UserDataDTO>
            List<UserDataDTO> userDataList = JSON.parseArray(userListData, UserDataDTO.class);

            for (UserDataDTO userData : userDataList) {
//                log("user  =  " + userData.toString());
                userIdList.add(userData.getUserId());
            }
        }

        return userIdList;
    }

    public boolean sendYkbMessage(String pcMessageUrl, String phoneMessageUrl, ArrayList<String> userIds, String msg, String url) {
//        log("userdid: " + userIds);
//        log("message: " + msg);
//        log("msgUrl: " + msgUrl);
        // 定义JSON字符串模板
        String jsonTemplate = "{\n" +
                "    \"userIds\": [%s],\n" +
                "    \"messageYzkBody\": {\n" +
                "        \"msgtype\": \"oa\",\n" +
                "        \"body\": {\n" +
                "            \"message_url\": \"%s\",\n" +
                "            \"pc_message_url\": \"%s\",\n" +
                "            \"body\": {\n" +
                "                \"title\": \"%s\",\n" +
                "                \"content\": \"%s\"\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";

        // 替换模板中的占位符
//        String jsonString = String.format(jsonTemplate, String.join(",", userIds), phoneMessageUrl, pcMessageUrl, msg, msg);
        String jsonString = String.format(jsonTemplate, "1889955984543158273", phoneMessageUrl, pcMessageUrl, msg, msg);

        // 输出最终的JSON字符串
        log(jsonString);

        HttpUtil httpUtil = new HttpUtil();
        String userListData = httpUtil.post(url,
                String.format("%s %s", this.getTokenRespDTO().getToken_type(), this.getTokenRespDTO().getAccess_token()),
                "1877665103373783042",
                jsonString);
        log("****** listdata: " + userListData);
        return true;
    }

    private void log(String print) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println(time + ": " + print);
    }
}
