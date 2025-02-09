package com.schedule.supervisory.dto;

import com.schedule.supervisory.entity.BzForm;
import com.schedule.supervisory.entity.BzFormTarget;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BzFromDTO implements Serializable {
    private BzForm bzForm;

    private List<BzFormTarget> bzFormTargetList;
}
