package com.schedule.supervisory.service;

import com.schedule.supervisory.entity.Task;

public interface IYkbMessageService {
    //逾期提醒,推承办人
    public boolean sendMessageForOverdue(Task task, int hour);

    //即将逾期提醒,推交办人，承办人，承办领导
    public boolean sendMessageForOverdueWarn(Task task);

    //审核，两种审核，
    public boolean sendMessageForCheck(Task task, int status);

    public boolean sendMessageForInstruction(Task task);

    public boolean sendMessageForNewTask(Task task);
}
