package com.schedule.supervisory.dto;

import com.schedule.supervisory.entity.Instruction;
import com.schedule.supervisory.entity.InstructionReply;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class InstructionResponse implements Serializable {
    private Instruction instruction;
    private List<InstructionReply> instructionReplieList;
}
