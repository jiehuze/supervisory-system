package com.schedule.supervisory.dto;

import com.schedule.supervisory.entity.ProgressReport;
import com.schedule.supervisory.entity.StageNode;
import com.schedule.supervisory.entity.Task;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TaskDetailDTO implements Serializable {
    private Task task;
    private List<StageNode> stageNodes;
    private List<ProgressReport> progressReports;
}
