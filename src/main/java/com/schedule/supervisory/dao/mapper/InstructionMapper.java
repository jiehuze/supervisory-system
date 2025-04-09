package com.schedule.supervisory.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.schedule.supervisory.entity.Instruction;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper // 标记为MyBatis的Mapper接口
public interface InstructionMapper extends BaseMapper<Instruction> {
    // 可以根据需要添加自定义查询方法
    // 自定义插入方法
    @Insert("INSERT INTO public.instruction " +
            "(task_id, reviewer_id, reviewer, \"content\", review_time, bz_form_id, bz_issue_id, bz_form_target_id, bz_issue_target_id) " +
            "VALUES " +
            "(#{entity.taskId}, #{entity.reviewerId}, #{entity.reviewer}, #{entity.content}, CURRENT_TIMESTAMP, " +
            "#{entity.bzFormId}, #{entity.bzIssueId}, #{entity.bzFormTargetId}, #{entity.bzIssueTargetId})")
    int insert(@Param("entity") Instruction entity);
}