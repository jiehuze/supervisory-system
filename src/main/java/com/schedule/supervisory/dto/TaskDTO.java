package com.schedule.supervisory.dto;

import com.schedule.supervisory.entity.StageNode;
import com.schedule.supervisory.entity.Task;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TaskDTO implements Serializable {
    private Task task; // 包含任务详情的对象
    private List<StageNode> stageNodes; // 关联的任务阶段节点数组

}
