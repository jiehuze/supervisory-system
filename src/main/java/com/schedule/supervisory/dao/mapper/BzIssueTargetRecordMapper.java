package com.schedule.supervisory.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.schedule.supervisory.entity.BzIssueTargetRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BzIssueTargetRecordMapper extends BaseMapper<BzIssueTargetRecord> {
    @Insert("INSERT INTO public.bz_issue_target_record " +
            "(target_id, work_progress, issue, updated_by, operator, operator_id, process_instance_id, status) " +
            "VALUES " +
            "(#{entity.targetId}, #{entity.workProgress}, #{entity.issue}, #{entity.updatedBy}, " +
            "#{entity.operator}, #{entity.operatorId}, #{entity.processInstanceId}, #{entity.status})")
    int insert(@Param("entity") BzIssueTargetRecord entity);

    List<BzIssueTargetRecord> selectByTargetId(@Param("targetId") Integer targetId);

    @Select("""
                WITH ranked_records AS (
                    SELECT
                        id,
                        target_id,
                        work_progress,
                        issue,
                        updated_by,
                        created_at,
                        "operator",
                        operator_id,
                        process_instance_id,
                        status,
                        target_name,
                        predicted_gear,
                        LAG(predicted_gear) OVER (PARTITION BY target_id ORDER BY created_at) AS prev_predicted_gear
                    FROM
                        public.bz_issue_target_record
                    WHERE
                        target_id = #{targetId}
                )
                SELECT
                    id,
                    target_id,
                    work_progress,
                    issue,
                    updated_by,
                    created_at,
                    "operator",
                    operator_id,
                    process_instance_id,
                    status,
                    target_name,
                    predicted_gear
                FROM
                    ranked_records
                WHERE
                    predicted_gear IS DISTINCT FROM prev_predicted_gear
                ORDER BY
                    created_at
            """)
    List<BzIssueTargetRecord> selectDistinctPredictedGearRecords(@Param("targetId") Integer targetId);
}