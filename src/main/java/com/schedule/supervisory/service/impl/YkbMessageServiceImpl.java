package com.schedule.supervisory.service.impl;

import com.schedule.common.YkbMessage;
import com.schedule.supervisory.dto.ParameterDTO;
import com.schedule.supervisory.entity.Check;
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
        String message = "您的任务【" + task.getContent() + "】将于" + hour + "小时之后逾期，请及时处理。";
        String[] deptIds = task.getLeadingDepartmentId().split(",");
        ArrayList<String> userIds = ykbMessage.getRoleUserId(parameterDTO.getUsersUrl(), List.of("CBR"), List.of(deptIds));//承办人

        String phoneMessageUrl = configService.getExternConfig("duban.message.phone");
        if (phoneMessageUrl == null || phoneMessageUrl.equals("")) {
            phoneMessageUrl = parameterDTO.getPhoneMessageUrl();
            configService.setExternConfig("duban.message.phone", phoneMessageUrl);
        }
        String pcMessageUrl = configService.getExternConfig("duban.message.pc");
        if (pcMessageUrl == null || pcMessageUrl.equals("")) {
            pcMessageUrl = parameterDTO.getPcMessageUrl();
            configService.setExternConfig("duban.message.pc", pcMessageUrl);
        }

        ykbMessage.sendYkbMessage(pcMessageUrl + task.getId(), phoneMessageUrl + task.getId(), userIds, message, parameterDTO.getMessageUrl(), parameterDTO.getServiceEnv());
        return true;
    }

    @Override
    public boolean sendMessageForOverdueWarn(Task task) {
        YkbMessage ykbMessage = new YkbMessage(parameterDTO.getAuthUrl());
        String message = "您的任务【" + task.getContent() + "】已逾期，请及时处理。";

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
        if (pcMessageUrl == null || pcMessageUrl.equals("")) {
            pcMessageUrl = parameterDTO.getPcMessageUrl();
            configService.setExternConfig("duban.message.pc", pcMessageUrl);
        }

        ykbMessage.sendYkbMessage(pcMessageUrl + task.getId(), phoneMessageUrl + task.getId(), userIds, message, parameterDTO.getMessageUrl(), parameterDTO.getServiceEnv());
        return true;
    }

    @Override
    public boolean sendMessageForCountDownWarn(Task task) {
        YkbMessage ykbMessage = new YkbMessage(parameterDTO.getAuthUrl());
        String message = "您的任务【" + task.getContent() + "】已临期，点击查看。";

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
        if (pcMessageUrl == null || pcMessageUrl.equals("")) {
            pcMessageUrl = parameterDTO.getPcMessageUrl();
            configService.setExternConfig("duban.message.pc", pcMessageUrl);
        }

        ykbMessage.sendYkbMessage(pcMessageUrl + task.getId(), phoneMessageUrl + task.getId(), userIds, message, parameterDTO.getMessageUrl(), parameterDTO.getServiceEnv());
        return true;
    }

    @Override
    public boolean sendMessageForFillWarn(Task task) {
        YkbMessage ykbMessage = new YkbMessage(parameterDTO.getAuthUrl());
        String message = "贵单位办理的【" + task.getContent() + "】任务已到填报期限了，请及时更新进展。";

        ArrayList<String> userIds = new ArrayList<>();
//        userIds.add(task.getAssignerId()); //交办人
        String[] deptIds = task.getLeadingDepartmentId().split(",");
        ArrayList<String> roleUserIdList = ykbMessage.getRoleUserId(parameterDTO.getUsersUrl(), List.of("CBLD", "CBR"), List.of(deptIds));//承办人，承办领导
        userIds.addAll(roleUserIdList);

        String phoneMessageUrl = configService.getExternConfig("duban.message.phone");
        if (phoneMessageUrl == null || phoneMessageUrl.equals("")) {
            phoneMessageUrl = parameterDTO.getPhoneMessageUrl();
            configService.setExternConfig("duban.message.phone", phoneMessageUrl);
        }
        String pcMessageUrl = configService.getExternConfig("duban.message.pc");
        if (pcMessageUrl == null || pcMessageUrl.equals("")) {
            pcMessageUrl = parameterDTO.getPcMessageUrl();
            configService.setExternConfig("duban.message.pc", pcMessageUrl);
        }

        ykbMessage.sendYkbMessage(pcMessageUrl + task.getId(), phoneMessageUrl + task.getId(), userIds, message, parameterDTO.getMessageUrl(), parameterDTO.getServiceEnv());
        return true;
    }

    /**
     * @param task 任务信息
     * @param role 角色表示，1：承办人申请；2：承办领导申请
     * @param type 处理类型： 1： 办结申请；2： 终止申请
     * @return
     */
    @Override
    public boolean sendMessageForCheck(Task task, int status) {
        YkbMessage ykbMessage = new YkbMessage(parameterDTO.getAuthUrl());
        String message = null;
        switch (status) {
            case 4:
            case 5:
            case 10:
            case 12:
                message = "有一条办结申请任务需要您审核，请在24小时内处理，如已经处理请忽略。";
                break;
            case 7:
            case 8:
            case 11:
                message = "有一条终止申请任务需要您审核，请在24小时内处理，如已经处理请忽略。";
                break;
            case 100:
                message = "有一条【" + task.getSource() + "】任务填报需要您审核，请在24小时内处理，如已经处理请忽略。";
                break;
        }

        String phoneMessageUrl = configService.getExternConfig("duban.message.phone");
        if (phoneMessageUrl == null || phoneMessageUrl.equals("")) {
            phoneMessageUrl = parameterDTO.getPhoneMessageUrl();
            configService.setExternConfig("duban.message.phone", phoneMessageUrl);
        }
        String pcMessageUrl = configService.getExternConfig("duban.message.pc");
        if (pcMessageUrl == null || pcMessageUrl.equals("")) {
            pcMessageUrl = parameterDTO.getPcMessageUrl();
            configService.setExternConfig("duban.message.pc", pcMessageUrl);
        }

        //为0是承办人办结申请
        switch (status) {
            case 4:
            case 7:
            case 12:
            case 100:
                //获取办结领导
                String[] deptIds = task.getLeadingDepartmentId().split(",");
                ArrayList<String> roleUserIdList = ykbMessage.getRoleUserId(parameterDTO.getUsersUrl(), List.of("CBLD"), List.of(deptIds));
                if (roleUserIdList.size() > 0) {
                    ykbMessage.sendYkbMessage(pcMessageUrl + task.getId(), phoneMessageUrl + task.getId(), roleUserIdList, message, parameterDTO.getMessageUrl(), parameterDTO.getServiceEnv());
                }
                break;
            case 5:
            case 8:
                ArrayList<String> userIds = new ArrayList<>();
                userIds.add(task.getAssignerId());
                ykbMessage.sendYkbMessage(pcMessageUrl + task.getId(), phoneMessageUrl + task.getId(), userIds, message, parameterDTO.getMessageUrl(), parameterDTO.getServiceEnv());
                break;
            case 10:
            case 11:
                String[] deptzIds = task.getLeadingDepartmentId().split(",");
                ArrayList<String> roleUserIdListz = ykbMessage.getRoleUserId(parameterDTO.getUsersUrl(), List.of("JBLD"), List.of(deptzIds));
                ykbMessage.sendYkbMessage(pcMessageUrl + task.getId(), phoneMessageUrl + task.getId(), roleUserIdListz, message, parameterDTO.getMessageUrl(), parameterDTO.getServiceEnv());
                break;
        }

        return true;
    }

    @Override
    public boolean sendMessageForCheckNew(Check check, String userIds) {
        YkbMessage ykbMessage = new YkbMessage(parameterDTO.getAuthUrl());
        String message = null;
        String pcMessageUrl = null;
        String phoneMessageUrl = null;

        switch (check.getCheckType()) {
            case 1:
            case 2:
            case 7:
            case 8:
                if (check.getCheckType() == 1) {
                    message = "有一条任务填报需要您审核，请在24小时内处理，如已经处理请忽略。";
                } else if (check.getCheckType() == 2) {
                    message = "有一条阶段性任务需要您审核，请在24小时内处理，如已经处理请忽略。";
                } else if (check.getCheckType() == 7) {
                    message = "有一条办结申请任务需要您审核，请在24小时内处理，如已经处理请忽略。";
                } else if (check.getCheckType() == 8) {
                    message = "有一条终止申请任务需要您审核，请在24小时内处理，如已经处理请忽略。";
                }
                phoneMessageUrl = configService.getExternConfig("duban.message.phone");
                if (phoneMessageUrl == null || phoneMessageUrl.equals("")) {
                    phoneMessageUrl = parameterDTO.getPhoneMessageUrl();
                    configService.setExternConfig("duban.message.phone", phoneMessageUrl);
                }
                phoneMessageUrl += check.getTaskId();
                pcMessageUrl = configService.getExternConfig("duban.message.pc");
                if (pcMessageUrl == null || pcMessageUrl.equals("")) {
                    pcMessageUrl = parameterDTO.getPcMessageUrl();
                    configService.setExternConfig("duban.message.pc", pcMessageUrl);
                }
                pcMessageUrl += check.getTaskId();
                break;
            case 3:
                message = "有一条报表需要您审核，请在24小时内处理，如已经处理请忽略。";
                phoneMessageUrl = parameterDTO.getPhoneFormMessageUrl() + check.getBzFormId();
                pcMessageUrl = parameterDTO.getPcFormMessageUrl() + check.getBzFormId();
                break;
            case 4:
                message = "有一条报表指标需要您审核，请在24小时内处理，如已经处理请忽略。";
                phoneMessageUrl = parameterDTO.getPhoneFormTargetMessageUrl() + check.getBzFormId();
                pcMessageUrl = parameterDTO.getPcFormTargetMessageUrl() + check.getBzFormId();
                break;
            case 5:
                message = "有一条清单需要您审核，请在24小时内处理，如已经处理请忽略。";
                phoneMessageUrl = parameterDTO.getPhoneIssueMessageUrl() + check.getBzIssueId();
                pcMessageUrl = parameterDTO.getPcIssueMessageUrl() + check.getBzIssueId();
                break;
            case 6:
                message = "有一条清单指标需要您审核，请在24小时内处理，如已经处理请忽略。";
                phoneMessageUrl = parameterDTO.getPhoneIssueTargetMessageUrl() + check.getBzIssueId();
                pcMessageUrl = parameterDTO.getPcIssueTargetMessageUrl() + check.getBzIssueId();
                break;
        }

        ArrayList<String> userIdList = new ArrayList<>(List.of(userIds.split(",")));
        ykbMessage.sendYkbMessage(pcMessageUrl, phoneMessageUrl, userIdList, message, parameterDTO.getMessageUrl(), parameterDTO.getServiceEnv());
        return true;

    }

    @Override
    public boolean sendMessageForInstruction(Task task) {
        YkbMessage ykbMessage = new YkbMessage(parameterDTO.getAuthUrl());
        String message = "您的任务【" + task.getContent() + "】被领导批示，请及时查看。";
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
        if (pcMessageUrl == null || pcMessageUrl.equals("")) {
            pcMessageUrl = parameterDTO.getPcMessageUrl();
            configService.setExternConfig("duban.message.pc", pcMessageUrl);
        }

        ykbMessage.sendYkbMessage(pcMessageUrl + task.getId(), phoneMessageUrl + task.getId(), userIds, message, parameterDTO.getMessageUrl(), parameterDTO.getServiceEnv());
        return true;
    }

    @Override
    public boolean sendMessageForNewTask(Task task) {
        YkbMessage ykbMessage = new YkbMessage(parameterDTO.getAuthUrl());
        String message = "您有一个新任务需要接收，请及时处理。";
        String[] deptIds = task.getLeadingDepartmentId().split(",");
        ArrayList<String> userIds = ykbMessage.getRoleUserId(parameterDTO.getUsersUrl(), List.of("CBR", "XBLD"), List.of(deptIds));//承办人

        String phoneMessageUrl = configService.getExternConfig("duban.message.phone");
        if (phoneMessageUrl == null || phoneMessageUrl.equals("")) {
            phoneMessageUrl = parameterDTO.getPhoneMessageUrl();
            configService.setExternConfig("duban.message.phone", phoneMessageUrl);
        }
        String pcMessageUrl = configService.getExternConfig("duban.message.pc");
        if (pcMessageUrl == null || pcMessageUrl.equals("")) {
            pcMessageUrl = parameterDTO.getPcMessageUrl();
            configService.setExternConfig("duban.message.pc", pcMessageUrl);
        }

        ykbMessage.sendYkbMessage(pcMessageUrl + task.getId(), phoneMessageUrl + task.getId(), userIds, message, parameterDTO.getMessageUrl(), parameterDTO.getServiceEnv());
        return true;
    }

    @Override
    public boolean sendMessageForUrgent(Task task) {
        YkbMessage ykbMessage = new YkbMessage(parameterDTO.getAuthUrl());
        String message = "您的任务【" + task.getContent() + "】收到一条催办提醒，请及时处理。";
        String[] deptIds = task.getLeadingDepartmentId().split(",");
        ArrayList<String> userIds = ykbMessage.getRoleUserId(parameterDTO.getUsersUrl(), List.of("CBR", "XBLD"), List.of(deptIds));//承办人

        String phoneMessageUrl = configService.getExternConfig("duban.message.phone");
        if (phoneMessageUrl == null || phoneMessageUrl.equals("")) {
            phoneMessageUrl = parameterDTO.getPhoneMessageUrl();
            configService.setExternConfig("duban.message.phone", phoneMessageUrl);
        }
        String pcMessageUrl = configService.getExternConfig("duban.message.pc");
        if (pcMessageUrl == null || pcMessageUrl.equals("")) {
            pcMessageUrl = parameterDTO.getPcMessageUrl();
            configService.setExternConfig("duban.message.pc", pcMessageUrl);
        }

        ykbMessage.sendYkbMessage(pcMessageUrl + task.getId(), phoneMessageUrl + task.getId(), userIds, message, parameterDTO.getMessageUrl(), parameterDTO.getServiceEnv());
        return true;
    }
}
