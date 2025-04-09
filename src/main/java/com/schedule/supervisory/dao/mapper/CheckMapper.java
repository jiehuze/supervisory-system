package com.schedule.supervisory.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.schedule.supervisory.entity.Check;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CheckMapper extends BaseMapper<Check> {
    @Insert("INSERT INTO public.duban_check " +
            "(task_id, stage_id, bz_form_id, bz_issue_id, bz_form_target_id, bz_issue_target_id, " +
            "data_json, status, operator, operator_id, check_type, process_instance_id, flow_id) " +
            "VALUES " +
            "(#{entity.taskId}, #{entity.stageId}, #{entity.bzFormId}, #{entity.bzIssueId}, #{entity.bzFormTargetId}, " +
            "#{entity.bzIssueTargetId}, #{entity.dataJson}, #{entity.status}, #{entity.operator}, #{entity.operatorId}, " +
            "#{entity.checkType}, #{entity.processInstanceId}, #{entity.flowId})")
    int insert(@Param("entity") Check entity);
}