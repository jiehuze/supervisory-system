package com.schedule.supervisory.service;

import com.schedule.supervisory.entity.*;

public interface IYkbMessageService {
    //逾期提醒,推承办人
    public boolean sendMessageForOverdue(Task task, int hour);

    //即将逾期提醒,推交办人，承办人，承办领导
    public boolean sendMessageForOverdueWarn(Task task);
    public boolean sendMessageForOverdueWarn(Task task, StageNode stageNode);

    public boolean sendMessageForCountDownWarn(Task task);

    public boolean sendMessageForFillWarn(Task task);

    //审核，两种审核，
    public boolean sendMessageForCheck(Task task, int status);

    public boolean sendMessageForCheckNew(Check check, String userIds);

    public boolean sendMessageForInstruction(Task task);

    public boolean sendMessageForNewTask(Task task);

    public boolean sendMessageForUrgent(Task task);

    public boolean sendMessageForExternal(ExternalTask externalTask);

    public boolean sendMessageForConsult(Consultation consultation);

}
