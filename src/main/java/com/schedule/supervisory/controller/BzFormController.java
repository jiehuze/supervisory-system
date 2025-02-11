package com.schedule.supervisory.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.schedule.common.BaseResponse;
import com.schedule.supervisory.dto.*;
import com.schedule.supervisory.entity.BzForm;
import com.schedule.supervisory.entity.BzFormTarget;
import com.schedule.supervisory.service.IBzFormService;
import com.schedule.supervisory.service.IBzFormTargetService;
import com.schedule.supervisory.service.IBzIssueService;
import com.schedule.supervisory.service.IBzIssueTargetService;
import com.schedule.utils.DateUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bzform")
public class BzFormController {

    @Autowired
    private IBzFormService bzFormService;

    @Autowired
    private IBzIssueService bzIssueService;

    @Autowired
    private IBzFormTargetService bzFormTargetService;
    @Autowired
    private IBzIssueTargetService bzIssueTargetService;

    @GetMapping("/search")
    public BaseResponse list(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                             @RequestHeader(value = "tenant-id", required = false) String tenantId,
                             @ModelAttribute BzForm bzForm,
                             @RequestParam(value = "current", defaultValue = "1") Integer pageNum,
                             @RequestParam(value = "size", defaultValue = "10") Integer pageSize) {
//        Page<BzForm> bzFormPage = bzFormService.page(new Page<>(pageNum, pageSize));
        IPage<BzForm> bzFormByConditions = bzFormService.getBzFormByConditions(bzForm, pageNum, pageSize);

        return new BaseResponse(HttpStatus.OK.value(), "success", bzFormByConditions, Integer.toString(0));
    }

    @GetMapping("/detail/{id}")
    public BaseResponse detail(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                               @RequestHeader(value = "tenant-id", required = false) String tenantId,
                               @PathVariable Long id) {
        BzFormDTO bzFormDTO = new BzFormDTO();
        bzFormDTO.setBzForm(bzFormService.getById(id));
        bzFormDTO.setBzFormTargetList(bzFormTargetService.getByFormId(id));

        return new BaseResponse(HttpStatus.OK.value(), "success", bzFormDTO, Integer.toString(0));
    }

    @PostMapping("/add")
    public BaseResponse saveOrUpdateTasks(@RequestBody BzFormDTO bzFromDTO) {
        BzForm bzForm = bzFromDTO.getBzForm();
        Long id = bzFormService.insertBzForm(bzForm);
        if (id == null) {
            return new BaseResponse(HttpStatus.NO_CONTENT.value(), "failed", id, Integer.toString(0));
        }
        for (BzFormTarget bzFormTarget : bzFromDTO.getBzFormTargetList()) {
            bzFormTarget.setBzFormId(id);
        }
        if (bzFromDTO.getBzFormTargetList().size() != 0) {
            bzFormTargetService.saveBatch(bzFromDTO.getBzFormTargetList());
        }

        return new BaseResponse(HttpStatus.OK.value(), "success", 0, Integer.toString(0));
    }

    @PutMapping("/update")
    public BaseResponse updateBzForm(@RequestBody BzForm bzForm) {
        boolean upate = bzFormService.updateBzFrom(bzForm);
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

        List<Map<String, Object>> countList = bzFormService.countEffectiveGear();

        for (Map<String, Object> map : countList) {
//            System.out.println("-----key: " + map.get("count_effective_gear"));
//            System.out.println("-----key: " + map.get("type_id"));
//            System.out.println("-----key: " + map.get("effective_gear"));

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

    @GetMapping("/collect")
    public BaseResponse statisticalCount() {
        Long bzFomeCount = bzFormService.count();
        Long bzIssueCount = bzIssueService.count();
        BzFormStatistics bzFormStatistics = new BzFormStatistics();
        bzFormStatistics.setBzFromCount(bzFomeCount);
        bzFormStatistics.setBzIssueCount(bzIssueCount);
        bzFormStatistics.setBzFormTargetCount(bzFormTargetService.count());
        bzFormStatistics.setBzIssueTargetCount(bzIssueTargetService.count());
        List<EffectiveGearCount> bzFormGearCounts = bzFormService.countGearCollect();
        Map<Integer, CountDTO> bzFormGearCountMap = new HashMap<>();
        for (int level = 1; level <= 5; level++) {
            CountDTO countDTO = new CountDTO(0, "0%");
            bzFormGearCountMap.put(level, countDTO);
        }
        for (EffectiveGearCount bzFormGearCount : bzFormGearCounts) {
            CountDTO countDTO = new CountDTO(bzFormGearCount.getCountEffectiveGear().intValue(), String.format("%d%%", bzFormGearCount.getCountEffectiveGear() * 100 / bzFomeCount));

            bzFormGearCountMap.put(bzFormGearCount.getEffectiveGear(), countDTO);
        }
        bzFormStatistics.setBzFormGears(bzFormGearCountMap);

        List<EffectiveGearCount> bzIssueGearCounts = bzIssueService.countGearCollect();
        Map<Integer, CountDTO> bzIssueGearCountMap = new HashMap<>();
        for (int level = 1; level <= 5; level++) {
            CountDTO countDTO = new CountDTO(0, "0%");
            bzIssueGearCountMap.put(level, countDTO);
        }
        for (EffectiveGearCount bzIssueGearCount : bzIssueGearCounts) {
            CountDTO countDTO = new CountDTO(bzIssueGearCount.getCountEffectiveGear().intValue(), String.format("%d%%", bzIssueGearCount.getCountEffectiveGear() * 100 / bzIssueCount));

            bzIssueGearCountMap.put(bzIssueGearCount.getEffectiveGear(), countDTO);
        }
        bzFormStatistics.setBzIssueGears(bzIssueGearCountMap);

        return new BaseResponse(HttpStatus.OK.value(), "success", bzFormStatistics, Integer.toString(0));
    }

    @GetMapping("/collectByQuarter")
    public BaseResponse collectByQuarter(@RequestParam(value = "type", defaultValue = "quarter") String type) {
        List<Quarter> quarters = null;
        if ("quarter".equals(type)) {
            quarters = DateUtils.getCurrentQuarters();
        } else {
            quarters = DateUtils.getCurrentYearQuarters();
        }
        HashMap<Integer, Map<Integer, CountDTO>> collectMap = new HashMap<>();
        for (Quarter quarter : quarters) {
            List<EffectiveGearCount> effectiveGearCounts = bzFormService.countGearCollectByQuarter(
                    quarter.getStartTime(),
                    quarter.getEndTime());

            //初始化5个档位
            Map<Integer, CountDTO> countMap = new HashMap<>();
            for (int level = 1; level <= 5; level++) {
                CountDTO countDTO = new CountDTO(0, "");
                countMap.put(level, countDTO);
            }
            collectMap.put(quarter.getQuarterNumber(), countMap);

            for (EffectiveGearCount effectiveGearCount : effectiveGearCounts) {
                CountDTO countDTO = new CountDTO(effectiveGearCount.getCountEffectiveGear().intValue(), "");

                collectMap.get(quarter.getQuarterNumber()).put(effectiveGearCount.getEffectiveGear(), countDTO);
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
    public BaseResponse getStatsByQuarterAndGear(@RequestParam(value = "type", defaultValue = "0") int quarter,
                                                 @RequestParam("gear") Integer gear) {
        Quarter quarterSearch = null;
        List<Quarter> quarters = null;
        if (quarter == 0) {
            quarters = DateUtils.getCurrentYearQuarters();
        } else {
            quarters = DateUtils.getCurrentQuarters();
        }
        for (Quarter quarterNode : quarters) {
            if (quarterNode.getQuarterNumber() == quarter) {
                quarterSearch = quarterNode;
                break;
            }
        }
        List<BzFromTargetNameCount> bzFromTargetNameCounts = bzFormService.selectByTimeAndGear(quarterSearch.getStartTime(), quarterSearch.getEndTime(), gear);


        return new BaseResponse(HttpStatus.OK.value(), "success", bzFromTargetNameCounts, Integer.toString(0));
    }
}