package com.schedule.supervisory.service.impl;

import com.schedule.common.YkbMessage;
import com.schedule.supervisory.dto.ParameterDTO;
import com.schedule.supervisory.entity.Task;
import com.schedule.supervisory.service.IConfigService;
import com.schedule.supervisory.service.IYkbMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class YkbMessageServiceImpl implements IYkbMessageService {
    @Autowired
    private ParameterDTO parameterDTO;

    @Autowired
    private IConfigService configService;

    @Override
    public boolean sendMessageForOverdue(Task task, int hour) {
        YkbMessage ykbMessage = new YkbMessage(parameterDTO.getAuthUrl());
        String message = "您的任务【" + task.getSource() + "】将于" + hour + "小时之后逾期，请及时处理。";
        String[] deptIds = task.getLeadingDepartmentId().split(",");
        ArrayList<String> userIds = ykbMessage.getRoleUserId(parameterDTO.getUsersUrl(), List.of("CBR"), List.of(deptIds));//承办人

        String phoneMessageUrl = configService.getExternConfig("duban.message.phone");
        if (phoneMessageUrl == null || phoneMessageUrl.equals("")) {
            phoneMessageUrl = parameterDTO.getPhoneMessageUrl();
            configService.setExternConfig("duban.message.phone", phoneMessageUrl);
        }
        String pcMessageUrl = configService.getExternConfig("duban.message.pc");
        {
            pcMessageUrl = parameterDTO.getPcMessageUrl();
            configService.setExternConfig("duban.message.pc", pcMessageUrl);
        }

        ykbMessage.sendYkbMessage(pcMessageUrl + task.getId(), phoneMessageUrl + task.getId(), userIds, message, parameterDTO.getMessageUrl());
        return true;
    }

    @Override
    public boolean sendMessageForOverdueWarn(Task task) {
        YkbMessage ykbMessage = new YkbMessage(parameterDTO.getAuthUrl());
        String message = "您的任务【" + task.getSource() + "已逾期，请及时处理。";

        ArrayList<String> userIds = new ArrayList<>();
        userIds.add(task.getAssignerId()); //交办人
        String[] deptIds = task.getLeadingDepartmentId().split(",");
        ArrayList<String> roleUserIdList = ykbMessage.getRoleUserId(parameterDTO.getUsersUrl(), List.of("CBLD", "CBR"), List.of(deptIds));//承办人，承办领导
        userIds.addAll(roleUserIdList);

        String phoneMessageUrl = configService.getExternConfig("duban.message.phone");
        if (phoneMessageUrl == null || phoneMessageUrl.equals("")) {
            phoneMessageUrl = parameterDTO.getPhoneMessageUrl();
            configService.setExternConfig("duban.message.phone", phoneMessageUrl);
        }
        String pcMessageUrl = configService.getExternConfig("duban.message.pc");
        {
            pcMessageUrl = parameterDTO.getPcMessageUrl();
            configService.setExternConfig("duban.message.pc", pcMessageUrl);
        }

        ykbMessage.sendYkbMessage(pcMessageUrl + task.getId(), phoneMessageUrl + task.getId(), userIds, message, parameterDTO.getMessageUrl());
        return true;
    }

    /**
     * @param task 任务信息
     * @param role 角色表示，1：承办人申请；2：承办领导申请
     * @param type 处理类型： 1： 办结申请；2： 终止申请
     * @return
     */
    @Override
    public boolean sendMessageForCheck(Task task, int role, int type) {
        YkbMessage ykbMessage = new YkbMessage(parameterDTO.getAuthUrl());
        String message = null;
        if (type == 1) {
            message = "有一条办结申请任务需要您审核，请在24小时内处理，如已经处理请忽略。";
        } else {
            message = "有一条终止申请任务需要您审核，请在24小时内处理，如已经处理请忽略。";
        }
        String phoneMessageUrl = configService.getExternConfig("duban.message.phone");
        if (phoneMessageUrl == null || phoneMessageUrl.equals("")) {
            phoneMessageUrl = parameterDTO.getPhoneMessageUrl();
            configService.setExternConfig("duban.message.phone", phoneMessageUrl);
        }
        String pcMessageUrl = configService.getExternConfig("duban.message.pc");
        {
            pcMessageUrl = parameterDTO.getPcMessageUrl();
            configService.setExternConfig("duban.message.pc", pcMessageUrl);
        }

        //为0是承办人办结申请
        if (role == 1) {
            //获取办结领导
            String[] deptIds = task.getLeadingDepartmentId().split(",");
            System.out.println("========deptIds: " + List.of(deptIds));
            System.out.println("========role: " + "CBLD");

            ArrayList<String> roleUserIdList = ykbMessage.getRoleUserId(parameterDTO.getUsersUrl(), List.of("CBLD"), List.of(deptIds));
            if (roleUserIdList.size() > 0) {
                ykbMessage.sendYkbMessage(pcMessageUrl + task.getId(), phoneMessageUrl + task.getId(), roleUserIdList, message, parameterDTO.getMessageUrl());
            }
        } else {
            ArrayList<String> userIds = new ArrayList<>();
            userIds.add(task.getAssignerId());
            ykbMessage.sendYkbMessage(pcMessageUrl + task.getId(), phoneMessageUrl + task.getId(), userIds, message, parameterDTO.getMessageUrl());

        }

        return false;
    }

    @Override
    public boolean sendMessageForInstruction(Task task) {
        YkbMessage ykbMessage = new YkbMessage(parameterDTO.getAuthUrl());
        String message = "您的任务【" + task.getSource() + "】被领导批示，请及时查看。";
        ArrayList<String> userIds = new ArrayList<>();
        userIds.add(task.getAssignerId()); //交办人
        String[] deptIds = task.getLeadingDepartmentId().split(",");
        ArrayList<String> roleUserIdList = ykbMessage.getRoleUserId(parameterDTO.getUsersUrl(), List.of("CBLD", "CBR"), List.of(deptIds));//承办人，承办领导
        userIds.addAll(roleUserIdList);

        String phoneMessageUrl = configService.getExternConfig("duban.message.phone");
        if (phoneMessageUrl == null || phoneMessageUrl.equals("")) {
            phoneMessageUrl = parameterDTO.getPhoneMessageUrl();
            configService.setExternConfig("duban.message.phone", phoneMessageUrl);
        }
        String pcMessageUrl = configService.getExternConfig("duban.message.pc");
        {
            pcMessageUrl = parameterDTO.getPcMessageUrl();
            configService.setExternConfig("duban.message.pc", pcMessageUrl);
        }

        ykbMessage.sendYkbMessage(pcMessageUrl + task.getId(), phoneMessageUrl + task.getId(), userIds, message, parameterDTO.getMessageUrl());
        return true;
    }

    @Override
    public boolean sendMessageForNewTask(Task task) {
        YkbMessage ykbMessage = new YkbMessage(parameterDTO.getAuthUrl());
        String message = "您有一个新任务需要接收，请及时处理。";
        String[] deptIds = task.getLeadingDepartmentId().split(",");
        ArrayList<String> userIds = ykbMessage.getRoleUserId(parameterDTO.getUsersUrl(), List.of("CBR", "XBLD", "XBLD"), List.of(deptIds));//承办人

        String phoneMessageUrl = configService.getExternConfig("duban.message.phone");
        if (phoneMessageUrl == null || phoneMessageUrl.equals("")) {
            phoneMessageUrl = parameterDTO.getPhoneMessageUrl();
            configService.setExternConfig("duban.message.phone", phoneMessageUrl);
        }
        String pcMessageUrl = configService.getExternConfig("duban.message.pc");
        {
            pcMessageUrl = parameterDTO.getPcMessageUrl();
            configService.setExternConfig("duban.message.pc", pcMessageUrl);
        }

        ykbMessage.sendYkbMessage(pcMessageUrl + task.getId(), phoneMessageUrl + task.getId(), userIds, message, parameterDTO.getMessageUrl());
        return true;
    }
}
