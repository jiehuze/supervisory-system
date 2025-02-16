package com.schedule.supervisory.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.schedule.common.BaseResponse;
import com.schedule.common.Licence;
import com.schedule.supervisory.dto.*;
import com.schedule.supervisory.entity.BzIssue;
import com.schedule.supervisory.entity.BzIssueTarget;
import com.schedule.supervisory.service.IBzIssueService;
import com.schedule.supervisory.service.IBzIssueTargetService;
import com.schedule.supervisory.service.IConfigService;
import com.schedule.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bzIssue")
public class BzIssueController {

    @Autowired
    private IBzIssueService bzIssueService;

    @Autowired
    private IBzIssueTargetService bzIssueTargetService;

    @Autowired
    private IConfigService configService;

    @GetMapping("/search")
    public BaseResponse list(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                             @RequestHeader(value = "tenant-id", required = false) String tenantId,
                             @ModelAttribute BzSearchDTO bzIssue,
                             @RequestParam(value = "current", defaultValue = "1") Integer pageNum,
                             @RequestParam(value = "size", defaultValue = "10") Integer pageSize) {
        if (!Licence.getLicence()) {
            String tenantIdex = configService.getTenantId();
            System.out.println("+++++++++++=========== tenantId: " + tenantIdex);
            if (!tenantId.equals(tenantIdex))
                return new BaseResponse(HttpStatus.OK.value(), "success", null, Integer.toString(0));
        }
        IPage<BzIssue> bzIssueByConditions = bzIssueService.getBzIssueByConditions(bzIssue, pageNum, pageSize);

        return new BaseResponse(HttpStatus.OK.value(), "success", bzIssueByConditions, Integer.toString(0));
    }

    @GetMapping("/detail/{id}")
    public BaseResponse detail(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                               @RequestHeader(value = "tenant-id", required = false) String tenantId,
                               @PathVariable Long id) {
        BzIssueDTO bzIssueDTO = new BzIssueDTO();
        bzIssueDTO.setBzIssue(bzIssueService.getById(id));
        bzIssueDTO.setBzIssueTargetList(bzIssueTargetService.getByIssueId(id));

        return new BaseResponse(HttpStatus.OK.value(), "success", bzIssueDTO, Integer.toString(0));
    }

    @PostMapping("/add")
    public BaseResponse saveOrUpdateTasks(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                          @RequestHeader(value = "tenant-id", required = false) String tenantId,
                                          @RequestBody BzIssueDTO bzFromDTO) {
        BzIssue bzIssue = bzFromDTO.getBzIssue();
        long count = bzIssueService.countBzIssue(bzIssue);
        if (count == -1) {
            return new BaseResponse(HttpStatus.METHOD_NOT_ALLOWED.value(), "参数错误", null, Integer.toString(0));
        } else if (count > 0) {
            return new BaseResponse(HttpStatus.GONE.value(), "已经存在该报表", null, Integer.toString(0));
        }
        Long id = bzIssueService.insertBzIssue(bzIssue);
        if (id == null) {
            return new BaseResponse(HttpStatus.NO_CONTENT.value(), "failed", id, Integer.toString(0));
        }
        if (!Licence.getLicence()) {
            String tenantIdex = configService.getTenantId();
            System.out.println("+++++++++++=========== tenantId: " + tenantIdex);
            if (!tenantId.equals(tenantIdex))
                return new BaseResponse(HttpStatus.OK.value(), "success", null, Integer.toString(0));
        }
        for (BzIssueTarget bzIssueTarget : bzFromDTO.getBzIssueTargetList()) {
            bzIssueTarget.setBzIssueId(id);
        }
        if (bzFromDTO.getBzIssueTargetList().size() != 0) {
            bzIssueTargetService.saveBatch(bzFromDTO.getBzIssueTargetList());
        }

        return new BaseResponse(HttpStatus.OK.value(), "success", 0, Integer.toString(0));
    }

    @PutMapping("/update")
    public BaseResponse updateBzIssue(@RequestBody BzIssue bzIssue) {
        boolean upate = bzIssueService.updateBzIssue(bzIssue);
        return new BaseResponse(HttpStatus.OK.value(), "success", upate, Integer.toString(0));
    }

    @GetMapping("/statisticalSType")
    public BaseResponse statisticalType() {
        ArrayList<DataTypeDTO> dataList = new ArrayList<>();
        // 初始化数据
        for (int type = 1; type <= 8; type++) {
            DataTypeDTO dataTypeDTO = new DataTypeDTO();
            dataTypeDTO.setTypeId(type);
            dataTypeDTO.setTotal(0);
            Map<Integer, CountDTO> countDTOMap = new HashMap<>();
            dataTypeDTO.setCountDTOMap(countDTOMap);
            for (int level = 1; level <= 5; level++) {
                CountDTO countDTO = new CountDTO(0, "0%");
                dataTypeDTO.getCountDTOMap().put(level, countDTO);
            }

            dataList.add(dataTypeDTO);
        }

        List<Map<String, Object>> countList = bzIssueService.countEffectiveGear();

        for (Map<String, Object> map : countList) {
            System.out.println("-----key: " + map.get("count_effective_gear"));
            System.out.println("-----key: " + map.get("type_id"));
            System.out.println("-----key: " + map.get("effective_gear"));
            if (map.get("count_effective_gear") == null || map.get("type_id") == null || map.get("effective_gear") == null) {
                continue;
            }
            DataTypeDTO dataType = dataList.get((Integer) map.get("type_id") - 1);
            CountDTO levelData = new CountDTO(((Long) map.get("count_effective_gear")).intValue(), String.format("%d%%", 0));
            dataType.getCountDTOMap().put((Integer) map.get("effective_gear"), levelData);
            dataType.setTotal(dataType.getTotal() + ((Long) map.get("count_effective_gear")).intValue());
        }
        for (int type = 1; type <= 8; type++) {
            DataTypeDTO dataTypeDTO = dataList.get(type - 1);
            int total = dataTypeDTO.getTotal();
            if (total == 0) continue;
            for (int level = 1; level <= 5; level++) {
                CountDTO countDTO = dataTypeDTO.getCountDTOMap().get(level);
                int rate = countDTO.getCount() * 100 / total;
                countDTO.setPercentage(String.format("%d%%", rate));
            }
        }

        return new BaseResponse(HttpStatus.OK.value(), "success", dataList, Integer.toString(0));
    }

    @GetMapping("/collectByQuarter")
    public BaseResponse collectByQuarter(@RequestParam(value = "type", defaultValue = "quarter") String type) {
        List<DateInfo> dateInfos = null;
        if ("quarter".equals(type)) {
            dateInfos = DateUtils.getCurrentQuarters();
        } else {
            dateInfos = DateUtils.getCurrentYears();
        }
        HashMap<Integer, Map<Integer, CountDTO>> collectMap = new HashMap<>();
        for (DateInfo dateInfo : dateInfos) {
            List<EffectiveGearCount> effectiveGearCounts = bzIssueService.countGearCollectTargetByDate(
                    dateInfo.getStartTime(),
                    dateInfo.getEndTime());

            //初始化5个档位
            Map<Integer, CountDTO> countMap = new HashMap<>();
            for (int level = 1; level <= 4; level++) {
                CountDTO countDTO = new CountDTO(0, "");
                countMap.put(level, countDTO);
            }
            collectMap.put(dateInfo.getNumber(), countMap);

            for (EffectiveGearCount effectiveGearCount : effectiveGearCounts) {
                CountDTO countDTO = new CountDTO(effectiveGearCount.getCountEffectiveGear().intValue(), "");

                collectMap.get(dateInfo.getNumber()).put(effectiveGearCount.getEffectiveGear(), countDTO);
            }

        }

        return new BaseResponse(HttpStatus.OK.value(), "success", collectMap, Integer.toString(0));
    }

    @GetMapping("/collectIssueByDate")
    public BaseResponse collectIssueByDate(@RequestParam(value = "type", defaultValue = "quarter") String type) {
        List<DateInfo> dateInfos = null;
        if ("quarter".equals(type)) {
            dateInfos = DateUtils.getCurrentQuarters();
        } else {
            dateInfos = DateUtils.getCurrentYears();
        }
        HashMap<Integer, Map<Integer, CountDTO>> collectMap = new HashMap<>();
        for (DateInfo dateInfo : dateInfos) {
            List<EffectiveGearCount> effectiveGearCounts = bzIssueService.countGearCollectByDate(
                    dateInfo.getStartTime(),
                    dateInfo.getEndTime());

            //初始化5个档位
            Map<Integer, CountDTO> countMap = new HashMap<>();
            for (int level = 1; level <= 5; level++) {
                CountDTO countDTO = new CountDTO(0, "");
                countMap.put(level, countDTO);
            }
            collectMap.put(dateInfo.getNumber(), countMap);

            for (EffectiveGearCount effectiveGearCount : effectiveGearCounts) {
                CountDTO countDTO = new CountDTO(effectiveGearCount.getCountEffectiveGear().intValue(), "");

                collectMap.get(dateInfo.getNumber()).put(effectiveGearCount.getEffectiveGear(), countDTO);
            }

        }

        return new BaseResponse(HttpStatus.OK.value(), "success", collectMap, Integer.toString(0));
    }

    /**
     * 根据指定的类型（季度或全年）和档位获取统计数据
     *
     * @param type 类型（0: 全年, 1-4: 季度）
     * @param gear 档位
     * @return 统计结果列表
     */
    @GetMapping("/gearTargetCount")
    public BaseResponse getStatsByQuarterAndGear(@RequestParam(value = "type", defaultValue = "0") int type,
                                                 @RequestParam(value = "year", defaultValue = "0") int year,
                                                 @RequestParam(value = "quarter", defaultValue = "0") int quarter,
                                                 @RequestParam("gear") Integer gear) {
        int number = 0;
        DateInfo dateInfo = null;
        List<DateInfo> dateInfos = null;
        if (year >= 2025) {
            dateInfos = DateUtils.getCurrentYears();
            number = year;
        } else if (quarter >= 1 && quarter <= 4) {
            dateInfos = DateUtils.getCurrentQuarters();
            number = quarter;
        }
        for (DateInfo di : dateInfos) {
            if (di.getNumber() == number) {
                dateInfo = di;
                break;
            }
        }
        List<BzFromTargetNameCount> bzFromTargetNameCounts = bzIssueService.selectByTimeAndGear(dateInfo.getStartTime(), dateInfo.getEndTime(), gear);


        return new BaseResponse(HttpStatus.OK.value(), "success", bzFromTargetNameCounts, Integer.toString(0));
    }
}