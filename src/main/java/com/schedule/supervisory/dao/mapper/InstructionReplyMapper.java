package com.schedule.supervisory.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.schedule.supervisory.entity.InstructionReply;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface InstructionReplyMapper extends BaseMapper<InstructionReply> {
    @Insert("INSERT INTO public.instruction_reply " +
            "(instruction_id, reply_content, \"operator\", operator_id, created_at) " +
            "VALUES " +
            "(#{entity.instructionId}, #{entity.replyContent}, #{entity.operator}, #{entity.operatorId}, CURRENT_TIMESTAMP)")
    int insert(@Param("entity") InstructionReply entity);

    // 根据 instruction_id 查询回复列表
    @Select("SELECT id, instruction_id AS instructionId, reply_content AS replyContent, " +
            "\"operator\", operator_id AS operatorId, created_at AS createdAt " +
            "FROM public.instruction_reply " +
            "WHERE instruction_id = #{instructionId} " +
            "ORDER BY created_at DESC")
    List<InstructionReply> selectByInstructionId(@Param("instructionId") Integer instructionId);
}