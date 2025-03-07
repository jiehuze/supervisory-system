package com.schedule.supervisory.service.impl;

import com.schedule.supervisory.dto.TaskSearchDTO;
import com.schedule.supervisory.entity.StageNode;
import com.schedule.supervisory.entity.Task;
import com.schedule.supervisory.service.IProgressReportService;
import com.schedule.supervisory.service.IStageNodeService;
import com.schedule.supervisory.service.ITaskService;
import com.schedule.supervisory.service.IYkbMessageService;
import com.schedule.utils.util;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class TaskSchedulerService {

    private final ITaskService taskService;
    private final IStageNodeService stageNodeService;
    private final IProgressReportService progressReportService;
    private final IYkbMessageService ykbMessageService;

    public TaskSchedulerService(ITaskService taskService, IStageNodeService stageNodeService, IProgressReportService progressReportService, IYkbMessageService ykbMessageService) {
        this.taskService = taskService;
        this.stageNodeService = stageNodeService;
        this.progressReportService = progressReportService;
        this.ykbMessageService = ykbMessageService;
    }

    // 任务1：每天 01:00 执行
    @Scheduled(cron = "0 02 1 * * ?")
    public void executeTaskAt1AM() {
        logTime("01:00 定时任务");
        //每天1点更新下过期时间
        //先更新阶段性目标超期的
        stageNodeService.updateOverdueDays();

        //获取所有没有完成的任务
        TaskSearchDTO taskSearchDTO = new TaskSearchDTO();
        taskSearchDTO.setUnfinished(true);
        List<Task> taskList = taskService.getTasksBySearchDTO(taskSearchDTO);

        for (Task task : taskList) {
            long taskoverdueDays = 0;
            if (util.daysDifference(task.getDeadline()) > 0) {
                taskoverdueDays = util.daysDifference(task.getDeadline());
            }
            List<StageNode> stageNodeList = stageNodeService.getStageNodeForOverdue(task.getId());
            for (StageNode stageNode : stageNodeList) {
                taskoverdueDays = Math.max(util.daysDifference(stageNode.getDeadline()), taskoverdueDays);
            }

//            if (taskoverdueDays > 0) {
            task.setOverdueDays((int) taskoverdueDays);
            taskService.updateOverdueDays(task.getId(), (int) taskoverdueDays);
//            }
        }

//        taskService.updateOverdueDays();
    }

    // 任务2：每天 09:00 执行
    @Scheduled(cron = "0 2 9 * * ?")
    public void executeTaskAt9AM() {
        logTime("09:00 定时任务");
        List<Task> tasks = taskService.getTasksDueInHours(24);
        for (Task task : tasks) {
            //todo 发送消息，不到24小时消息
            logTime(task.getSource() + "不到24小时消息");
            ykbMessageService.sendMessageForOverdue(task, 24);
        }

        List<Task> overdueTasks = taskService.ListTasksOverdue();
        for (Task task : overdueTasks) {
            //发送逾期提醒
            logTime(task.getSource() + "逾期提醒");
            ykbMessageService.sendMessageForOverdueWarn(task);
        }


        //查询是否有快超期的任务，并做提醒
    }

    //检查周期填报任务是否过期
    @Scheduled(cron = "0 2 10 * * ?")
    public void executeTaskAt10AM() {
        logTime("09:00 定时任务");
        //10点检查填报状态
//        List<TaskWithProgressReportDTO> tasks = progressReportService.checkFileCycle();
//        for (TaskWithProgressReportDTO task : tasks) {
//            //todo 发送消息，不到24小时消息
//            logTime(task.getSource() + "不到24小时消息");
//        }

        //查询是否有快超期的任务，并做提醒
    }

    // 任务3：每天 12:00 执行
    @Scheduled(cron = "0 2 12 * * ?")
    public void executeTaskAt12PM() {
        logTime("12:00 定时任务");
        List<Task> tasks = taskService.getTasksDueInHours(12);
        for (Task task : tasks) {
            //todo 发送消息，不到12小时消息
            logTime(task.getSource() + "不到12小时消息");
            ykbMessageService.sendMessageForOverdue(task, 12); //逾期12小时提醒
        }
        //查询是否有快超级的任务，并做提醒
    }

    // 任务4：每 5 秒执行一次
//    @Scheduled(fixedRate = 5000)  // 或者 @Scheduled(cron = "*/5 * * * * ?")
//    public void executeTaskEvery5Seconds() {
//        logTime("每 5 秒执行的任务");
//    }

    // 打印执行时间
    private void logTime(String taskName) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println(taskName + " 执行时间：" + time);
    }
}
