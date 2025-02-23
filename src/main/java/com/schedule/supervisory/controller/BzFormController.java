package com.schedule.supervisory.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.schedule.common.BaseResponse;
import com.schedule.common.Licence;
import com.schedule.supervisory.dto.*;
import com.schedule.supervisory.entity.BzForm;
import com.schedule.supervisory.entity.BzFormTarget;
import com.schedule.supervisory.service.*;
import com.schedule.utils.DateUtils;
import com.schedule.utils.HttpUtil;
import com.schedule.utils.util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @Autowired
    private IConfigService configService;
    @Autowired
    private ParameterDTO parameterDTO;

    @GetMapping("/search")
    public BaseResponse list(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                             @RequestHeader(value = "tenant-id", required = false) String tenantId,
                             @ModelAttribute BzSearchDTO bzSearchDTO,
                             @RequestParam(value = "current", defaultValue = "1") Integer pageNum,
                             @RequestParam(value = "size", defaultValue = "10") Integer pageSize) {
        if (!Licence.getLicence()) {
//            String tenantIdex = configService.getTenantId();
            String tenantIdex = configService.getExternConfig("tenant.id");
            System.out.println("+++++++++++=========== tenantId: " + tenantIdex);
            if (!tenantId.equals(tenantIdex))
                return new BaseResponse(HttpStatus.OK.value(), "success", null, Integer.toString(0));
        }
        List<DeptDTO> deptDTOs = null;
        HttpUtil httpUtil = new HttpUtil();
        String deptJson = httpUtil.get(parameterDTO.getPermissionUrl(), authorizationHeader, tenantId);
        if (deptJson != null) {
            deptDTOs = JSON.parseArray(deptJson, DeptDTO.class);
            System.out.println("Dept list size: " + deptDTOs.size());
        } else {
            return new BaseResponse(HttpStatus.OK.value(), "鉴权失败，获取权限失败！", false, Integer.toString(0));
        }
        IPage<BzForm> bzFormByConditions = bzFormService.getBzFormByConditions(bzSearchDTO, pageNum, pageSize, deptDTOs);
        for (BzForm bzForm : bzFormByConditions.getRecords()) {
            System.out.println("============bzForm: " + bzForm);
            bzSearchDTO.setBzFormId(bzForm.getId());
            bzSearchDTO.setCheckStatus("4");
            System.out.println("============bzSearchDTO: " + bzSearchDTO);
            List<BzFormTarget> bzFormTargets = bzFormTargetService.getByFormId(bzSearchDTO, deptDTOs);
            if (bzFormTargets != null && bzFormTargets.size() > 0) {
                bzForm.setCheckStatus(util.joinString(bzForm.getCheckStatus(), "4"));
            }
        }

        return new BaseResponse(HttpStatus.OK.value(), "success", bzFormByConditions, Integer.toString(0));
    }

    @GetMapping("/detail/{id}")
    public BaseResponse detail(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                               @RequestHeader(value = "tenant-id", required = false) String tenantId,
                               @ModelAttribute BzSearchDTO bzSearchDTO) {
        BzFormDTO bzFormDTO = new BzFormDTO();
        bzFormDTO.setBzForm(bzFormService.getById(bzSearchDTO.getId()));

        List<DeptDTO> deptDTOs = null;
        HttpUtil httpUtil = new HttpUtil();
        String deptJson = httpUtil.get(parameterDTO.getPermissionUrl(), authorizationHeader, tenantId);
        if (deptJson != null) {
            deptDTOs = JSON.parseArray(deptJson, DeptDTO.class);
            System.out.println("Dept list size: " + deptDTOs.size());
        } else {
            return new BaseResponse(HttpStatus.OK.value(), "鉴权失败，获取权限失败！", false, Integer.toString(0));
        }

        bzSearchDTO.setBzFormId(bzSearchDTO.getId());
        bzFormDTO.setBzFormTargetList(bzFormTargetService.getByFormId(bzSearchDTO, deptDTOs));

        return new BaseResponse(HttpStatus.OK.value(), "success", bzFormDTO, Integer.toString(0));
    }

    @PostMapping("/add")
    public BaseResponse saveOrUpdateTasks(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                          @RequestHeader(value = "tenant-id", required = false) String tenantId,
                                          @RequestBody BzFormDTO bzFromDTO) {
        BzForm bzForm = bzFromDTO.getBzForm();
        bzForm.setAssigner(bzForm.getOperator());
        bzForm.setAssignerId(bzForm.getOperatorId());
        long count = bzFormService.countBzForm(bzForm);
        if (count == -1) {
            return new BaseResponse(HttpStatus.METHOD_NOT_ALLOWED.value(), "参数错误", null, Integer.toString(0));
        } else if (count > 0) {
            return new BaseResponse(HttpStatus.GONE.value(), "已经存在该报表", null, Integer.toString(0));
        }

        bzForm.setAssigner(bzForm.getOperator());
        bzForm.setAssignerId(bzForm.getOperatorId());
        Long id = bzFormService.insertBzForm(bzForm);
        if (id == null) {
            return new BaseResponse(HttpStatus.NO_CONTENT.value(), "failed", id, Integer.toString(0));
        }
        if (!Licence.getLicence()) {
//            String tenantIdex = configService.getTenantId();
            String tenantIdex = configService.getExternConfig("tenant.id");
            System.out.println("+++++++++++=========== tenantId: " + tenantIdex);
            if (!tenantId.equals(tenantIdex))
                return new BaseResponse(HttpStatus.OK.value(), "success", null, Integer.toString(0));
        }
        for (BzFormTarget bzFormTarget : bzFromDTO.getBzFormTargetList()) {
            bzFormTarget.setBzFormId(id);
            bzFormTarget.setAssigner(bzForm.getAssigner());
            bzFormTarget.setAssignerId(bzForm.getAssignerId());
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
        for (int type = 0; type <= 8; type++) {
            DataTypeDTO dataTypeDTO = new DataTypeDTO();
            dataTypeDTO.setTypeId(type);
            dataTypeDTO.setTotal(0);
            Map<Integer, CountDTO> countDTOMap = new HashMap<>();
            dataTypeDTO.setCountDTOMap(countDTOMap);
            for (int level = 1; level <= 4; level++) {
                CountDTO countDTO = new CountDTO(0, "0%");
                dataTypeDTO.getCountDTOMap().put(level, countDTO);
            }

            dataList.add(dataTypeDTO);
        }

        List<EffectiveGearCount> bzFormGearCounts = bzFormService.countGearCollect();
        for (EffectiveGearCount bzFormGearCount : bzFormGearCounts) {
            DataTypeDTO dataType = dataList.get(0);
            CountDTO levelData = new CountDTO(bzFormGearCount.getCountEffectiveGear().intValue(), String.format("%d%%", 0));
            dataType.getCountDTOMap().put(bzFormGearCount.getEffectiveGear(), levelData);
            dataType.setTotal(dataType.getTotal() + bzFormGearCount.getCountEffectiveGear().intValue());
        }

        List<Map<String, Object>> countList = bzFormService.countEffectiveGear();

        for (Map<String, Object> map : countList) {
//            System.out.println("-----key: " + map.get("count_effective_gear"));
//            System.out.println("-----key: " + map.get("type_id"));
//            System.out.println("-----key: " + map.get("effective_gear"));
            if (map.get("count_effective_gear") == null || map.get("type_id") == null || map.get("effective_gear") == null) {
                continue;
            }

            DataTypeDTO dataType = dataList.get((Integer) map.get("type_id"));
            CountDTO levelData = new CountDTO(((Long) map.get("count_effective_gear")).intValue(), String.format("%d%%", 0));
            dataType.getCountDTOMap().put((Integer) map.get("effective_gear"), levelData);
            dataType.setTotal(dataType.getTotal() + ((Long) map.get("count_effective_gear")).intValue());
        }

        for (int type = 0; type <= 8; type++) {
            DataTypeDTO dataTypeDTO = dataList.get(type);
            int total = dataTypeDTO.getTotal();
            if (total == 0) continue;
            for (int level = 1; level <= 4; level++) {
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
        List<DateInfo> dataInfos = null;
        if ("quarter".equals(type)) {
            dataInfos = DateUtils.getCurrentQuarters();
        } else {
            dataInfos = DateUtils.getCurrentYears();
        }
        HashMap<Integer, Map<Integer, CountDTO>> collectMap = new HashMap<>();
        for (DateInfo di : dataInfos) {
            List<EffectiveGearCount> effectiveGearCounts = bzFormService.countGearCollectTargetByDate(
                    di.getStartTime(),
                    di.getEndTime());

            //初始化4个档位
            Map<Integer, CountDTO> countMap = new HashMap<>();
            for (int level = 1; level <= 4; level++) {
                CountDTO countDTO = new CountDTO(0, "");
                countMap.put(level, countDTO);
            }
            collectMap.put(di.getNumber(), countMap);

            for (EffectiveGearCount effectiveGearCount : effectiveGearCounts) {
                CountDTO countDTO = new CountDTO(effectiveGearCount.getCountEffectiveGear().intValue(), "");

                collectMap.get(di.getNumber()).put(effectiveGearCount.getEffectiveGear(), countDTO);
            }

        }

        return new BaseResponse(HttpStatus.OK.value(), "success", collectMap, Integer.toString(0));
    }

    @GetMapping("/collectFormByDate")
    public BaseResponse collectFormByDate(@RequestParam(value = "type", defaultValue = "quarter") String type) {
        List<DateInfo> dataInfos = null;
        if ("quarter".equals(type)) {
            dataInfos = DateUtils.getCurrentQuarters();
        } else {
            dataInfos = DateUtils.getCurrentYears();
        }
        HashMap<Integer, Map<Integer, CountDTO>> collectMap = new HashMap<>();
        for (DateInfo di : dataInfos) {
            List<EffectiveGearCount> effectiveGearCounts = bzFormService.countGearCollectByDate(
                    di.getStartTime(),
                    di.getEndTime());

            //初始化5个档位
            Map<Integer, CountDTO> countMap = new HashMap<>();
            for (int level = 1; level <= 5; level++) {
                CountDTO countDTO = new CountDTO(0, "");
                countMap.put(level, countDTO);
            }
            collectMap.put(di.getNumber(), countMap);

            for (EffectiveGearCount effectiveGearCount : effectiveGearCounts) {
                CountDTO countDTO = new CountDTO(effectiveGearCount.getCountEffectiveGear().intValue(), "");

                collectMap.get(di.getNumber()).put(effectiveGearCount.getEffectiveGear(), countDTO);
            }

        }

        return new BaseResponse(HttpStatus.OK.value(), "success", collectMap, Integer.toString(0));
    }

    @GetMapping("/grearsByDate")
    public BaseResponse grearsByDate(@ModelAttribute BzSearchDTO bzSearch) {
        LocalDate now = LocalDate.now();
        List<DateInfo> dateInfos = null;
        if (bzSearch.getDateType() == 2) {
            dateInfos = DateUtils.getCurrentQuarters();
        } else {
            dateInfos = DateUtils.getCurrentYears();
        }

        List<BzForm> gearsByConditions = bzFormService.getGearsByConditions(bzSearch);
        // 第一层：季度或者年；第二层：八层表；值为1-5（A-E）
        HashMap<Integer, Map<Integer, Integer>> collectMap = new HashMap<>();

        for (DateInfo dateInfo : dateInfos) {
            Map<Integer, Integer> dateMap = new HashMap<>();
            for (int i = 1; i <= 8; i++) {
                dateMap.put(i, 0);
            }
            collectMap.put(dateInfo.getNumber(), dateMap);
        }

        for (BzForm bzForm : gearsByConditions) {

            Map<Integer, Integer> typeIdS = null;
            if (bzSearch.getDateType() == 1) { //按照年
//                if (bzForm.getYear() > now.getYear()) {
//                    continue;
//                }
                typeIdS = collectMap.get(bzForm.getYear());
            } else {
//                if (bzForm.getQuarter() > ((now.getMonthValue() - 1) / 3 + 1)) {
//                    continue;
//                }
                typeIdS = collectMap.get(bzForm.getQuarter());
            }

            Integer gear = bzForm.getActualGear();
            if (bzForm.getActualGear() == null) {
                gear = bzForm.getPredictedGear();
            }
            typeIdS.put(bzForm.getTypeId(), gear);
        }

        return new BaseResponse(HttpStatus.OK.value(), "success", collectMap, Integer.toString(0));
    }

    /**
     * 根据指定的类型（季度或全年）和档位获取统计数据
     *
     * @param type 类型（大于2025: 为全年, 1-4: 季度）
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
        List<BzFromTargetNameCount> bzFromTargetNameCounts = bzFormService.selectByTimeAndGear(dateInfo.getStartTime(), dateInfo.getEndTime(), gear);


        return new BaseResponse(HttpStatus.OK.value(), "success", bzFromTargetNameCounts, Integer.toString(0));
    }
}