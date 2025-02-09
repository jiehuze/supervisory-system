package com.schedule.supervisory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.supervisory.dao.mapper.DuchaReportMapper;
import com.schedule.supervisory.dao.mapper.TaskMapper;
import com.schedule.supervisory.dto.DuchaReportCreationDTO;
import com.schedule.supervisory.dto.ReportUpdateDTO;
import com.schedule.supervisory.entity.DuchaReport;
import com.schedule.supervisory.entity.Task;
import com.schedule.supervisory.service.IDuchaReportService;
import com.schedule.utils.HttpUtil;
import com.schedule.utils.WordFileReplace;

import java.util.AbstractMap.SimpleEntry;
import java.util.AbstractMap.SimpleEntry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DuchaReportServiceImpl extends ServiceImpl<DuchaReportMapper, DuchaReport> implements IDuchaReportService {
    @Autowired
    private TaskMapper taskMapper; // 假设已经有了TaskMapper

    @Autowired
    private DuchaReportMapper duchaReportMapper;

    @Override
    public Page<DuchaReport> searchReports(String userId, String reportName, Integer pageNum, Integer pageSize) {
        // 创建分页对象
        Page<DuchaReport> page = new Page<>(pageNum, pageSize);

        // 创建查询条件包装器
        LambdaQueryWrapper<DuchaReport> queryWrapper = new LambdaQueryWrapper<>();

        // 添加模糊查询条件
        if (userId != null && !userId.trim().isEmpty()) {
            queryWrapper.or(wrapper -> wrapper
                    .like(DuchaReport::getSubmitterId, userId)
                    .or()
                    .like(DuchaReport::getLeadingOfficialId, userId)
            );
        }

        if (reportName != null && !reportName.trim().isEmpty()) {
            queryWrapper.like(DuchaReport::getReportName, reportName);  // 使用 like 进行模糊匹配
        }
        queryWrapper.eq(DuchaReport::getIsDeleted, false); //只查询没有删除的报告

        // 按照id降序排列
        queryWrapper.orderByDesc(DuchaReport::getId);

        // 执行查询并返回结果
        return this.page(page, queryWrapper);
    }

    @Override
    public boolean generateReportFromTasks(DuchaReportCreationDTO duchaReportCreationDTO, String token, String tenantId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        List<Long> taskIds = duchaReportCreationDTO.getTaskIds();
        String collect = taskIds.stream()
                .map(String::valueOf) // 将 Long 转换为 String
                .collect(Collectors.joining(","));
        LambdaQueryWrapper<DuchaReport> queryWrapper = new LambdaQueryWrapper<>();

        DuchaReport report = new DuchaReport();
        report.setTasks(collect);
        report.setSubmitter(duchaReportCreationDTO.getSubmitter());
        report.setSubmitterId(duchaReportCreationDTO.getSubmitterId());
        report.setIsSubmitted(false);
        report.setIsDeleted(false);
//        report.setLeadingOfficial(duchaReportCreationDTO.getLeadingOfficial());
//        report.setLeadingOfficialId(duchaReportCreationDTO.getLeadingOfficialId());
        report.setPeriod(((Long) count(queryWrapper)).intValue() + 1);
        report.setTaskCount(taskIds.size());
        report.setInProgressCount(0);
        report.setOverdueCount(0);
        report.setIssueCount(0);
        report.setCompleteCount(0);
        report.setCompleteOnTimeCount(0);

        List<Task> tasks = taskMapper.selectBatchIds(taskIds);
        for (Task task : tasks) {
            report.setReportName(task.getSourceDate().format(formatter) + task.getSource());
            //进行中
            if (!task.getStatus().equals(6) && task.getStatus().equals(9)) {
                report.setInProgressCount(report.getInProgressCount() + 1);
            }
            //超期任务
            if (task.getStatus() == 3) {
                report.setOverdueCount(report.getOverdueCount() + 1);
            }
            //按期完成和完成数
            LocalDateTime deadlineDateTime = task.getDeadline().atStartOfDay();
            if (task.getStatus().equals(6)) {
                report.setCompleteCount(report.getCompleteCount() + 1);
                if (deadlineDateTime.isAfter(task.getUpdatedAt())) {
                    report.setCompleteOnTimeCount(report.getCompleteOnTimeCount() + 1);
                }
            }
            //有问题数
            if (task.getIssuesAndChallenges() != null && task.getIssuesAndChallenges().length() > 0) {
                report.setIssueCount(report.getIssueCount() + 1);
            }
        }

        //todo，创建报告，上传报告
        int rate = report.getCompleteOnTimeCount() * 100 / report.getTaskCount();
//        WordFileReplace.replace();
        Map<String, String> replacements = Map.ofEntries(
                new SimpleEntry<>("{{name}}", report.getReportName()),
                new SimpleEntry<>("{{Y}}", "2025"),
                new SimpleEntry<>("{{M}}", "01"),
                new SimpleEntry<>("{{D}}", "11"),
                new SimpleEntry<>("{{Q}}", report.getPeriod().toString()),
                new SimpleEntry<>("{{total}}", report.getTaskCount().toString()),
                new SimpleEntry<>("{{complete}}", report.getCompleteCount().toString()),
                new SimpleEntry<>("{{inprogress}}", report.getInProgressCount().toString()),
                new SimpleEntry<>("{{issues}}", report.getIssueCount().toString()),
                new SimpleEntry<>("{{overdue}}", report.getOverdueCount().toString()),
                new SimpleEntry<>("{{rate}}", String.valueOf(rate)),
                new SimpleEntry<>("{{extra}}", "New Value") // 添加额外键值对
        );

        String outputPath = "/tmp/report" + System.currentTimeMillis() + ".docx";
        WordFileReplace.replaceTextInWordX(replacements, outputPath);

        //上传文件
        HttpUtil httpUtil = new HttpUtil();
        String uploadUrl = httpUtil.upload("http://113.207.111.33:48770/api/admin/sys-file/upload", token, tenantId, outputPath);
        report.setReportFile(uploadUrl);

        return this.save(report);
    }

    @Override
    public boolean updateSubmitById(Long duchaReportId, ReportUpdateDTO reportSubmissionDTO) {
        LambdaUpdateWrapper<DuchaReport> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(DuchaReport::getId, duchaReportId)
                .set(DuchaReport::getIsSubmitted, true)
                .set(DuchaReport::getLeadingOfficial, reportSubmissionDTO.getLeadingOfficial())
                .set(DuchaReport::getLeadingOfficialId, reportSubmissionDTO.getLeadingOfficialId());
        return update(updateWrapper);
    }

    @Override
    public boolean updateReportFileById(Long duchaReportId, ReportUpdateDTO reportSubmissionDTO) {
        LambdaUpdateWrapper<DuchaReport> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(DuchaReport::getId, duchaReportId)
                .set(DuchaReport::getReportFile, reportSubmissionDTO.getReportFile());
        return update(updateWrapper);
    }

    @Override
    public boolean deleteById(Long duchaReportId) {
        LambdaUpdateWrapper<DuchaReport> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(DuchaReport::getId, duchaReportId)
                .set(DuchaReport::getIsDeleted, true);
        return update(updateWrapper);
    }
}