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

import java.time.DayOfWeek;
import java.time.LocalDate;
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

            if (task.getOverdueDays() != taskoverdueDays) {
                task.setOverdueDays((int) taskoverdueDays);
                taskService.updateOverdueDays(task.getId(), (int) taskoverdueDays);
            }
        }

        //临期任务更新，临期天数
        taskService.updateCountDownDays();

        //更新周期填报情况，设置is_fill
        taskService.updateIsFilled();

//        taskService.updateOverdueDays();
    }

//    @Scheduled(cron = "0 04 12 * * ?")
//    public void executeTaskAttestAM() {
//        taskService.updateIsFilled();
//    }

    // 任务2：每天 09:00 执行
    @Scheduled(cron = "0 02 9 * * ?")
    public void executeTaskAt9AM() {
        logTime("09:00 定时任务");
        // 获取当前日期
        LocalDate today = LocalDate.now();

        // 获取当前星期几
        DayOfWeek dayOfWeek = today.getDayOfWeek();

        // 判断是否为周六或周日并打印结果
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            System.out.println("今天是周末：" + dayOfWeek.name());
            return;
        }

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

        List<Task> countDownTasks = taskService.ListTasksCountDown();
        for (Task task : countDownTasks) {
            //发送逾期提醒
            logTime(task.getSource() + "临期提醒");
            ykbMessageService.sendMessageForCountDownWarn(task);
        }
        List<Task> unfilledTasks = taskService.ListTasksUnfilled();
        for (Task task : unfilledTasks) {
            //发送逾期提醒
            logTime(task.getSource() + "周期填报提醒");
            ykbMessageService.sendMessageForFillWarn(task);
        }
        //查询是否有快超期的任务，并做提醒
    }

    // 任务3：每天 12:00 执行
    @Scheduled(cron = "0 2 12 * * ?")
    public void executeTaskAt12PM() {
        logTime("12:00 定时任务");
        // 获取当前日期
        LocalDate today = LocalDate.now();

        // 获取当前星期几
        DayOfWeek dayOfWeek = today.getDayOfWeek();

        // 判断是否为周六或周日并打印结果
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            System.out.println("今天是周末：" + dayOfWeek.name());
            return;
        }
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
        System.out.println(" 执行时间：" + time + "  : "+taskName);
    }
}
