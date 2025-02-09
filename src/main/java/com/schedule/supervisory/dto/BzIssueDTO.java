package com.schedule.supervisory.dto;

import com.schedule.supervisory.entity.BzIssue;
import com.schedule.supervisory.entity.BzIssueTarget;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BzIssueDTO implements Serializable {
    private BzIssue bzIssue;

    private List<BzIssueTarget> bzIssueTargetList;
}
