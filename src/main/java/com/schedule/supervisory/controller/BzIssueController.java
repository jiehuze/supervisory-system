package com.schedule.supervisory.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.schedule.common.BaseResponse;
import com.schedule.common.Licence;
import com.schedule.excel.FormTemplateExcel;
import com.schedule.excel.IssueTemplateExcel;
import com.schedule.supervisory.dto.*;
import com.schedule.supervisory.entity.*;
import com.schedule.supervisory.service.IBzIssueService;
import com.schedule.supervisory.service.IBzIssueTargetService;
import com.schedule.supervisory.service.IBzTypeService;
import com.schedule.supervisory.service.IConfigService;
import com.schedule.utils.DateUtils;
import com.schedule.utils.ExcelUtil;
import com.schedule.utils.HttpUtil;
import com.schedule.utils.util;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/bzIssue")
public class BzIssueController {

    @Autowired
    private IBzIssueService bzIssueService;

    @Autowired
    private IBzIssueTargetService bzIssueTargetService;

    @Autowired
    private IBzTypeService bzTypeService;

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
        if (!configService.getConfig(tenantId)) {
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
        IPage<BzIssue> bzIssueByConditions = bzIssueService.getBzIssueByConditions2(bzSearchDTO, pageNum, pageSize, deptDTOs);
        for (BzIssue bzIssue : bzIssueByConditions.getRecords()) {
            bzSearchDTO.setBzIssueId(bzIssue.getId().longValue());
            bzSearchDTO.setCheckStatus("4");
            System.out.println("============bzSearchDTO: " + bzSearchDTO);
            List<BzIssueTarget> bzIssueTargets = bzIssueTargetService.getCheckByIssueId(bzSearchDTO, deptDTOs);
            if (bzIssueTargets != null && bzIssueTargets.size() > 0) {
                bzIssue.setCheckStatus(util.joinString(bzIssue.getCheckStatus(), "4"));
                String reviewTargetIds = "";
                for (BzIssueTarget bzIssueTarget : bzIssueTargets) {
                    reviewTargetIds = util.joinString(reviewTargetIds, bzIssueTarget.getProcessInstanceReviewIds());
                }
                bzIssue.setProcessInstanceTargetReviewIds(reviewTargetIds);
            }
        }
        return new BaseResponse(HttpStatus.OK.value(), "success", bzIssueByConditions, Integer.toString(0));
    }

    @GetMapping("/detail/{id}")
    public BaseResponse detail(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                               @RequestHeader(value = "tenant-id", required = false) String tenantId,
                               @ModelAttribute BzSearchDTO bzSearchDTO) {
        BzIssueDTO bzIssueDTO = new BzIssueDTO();
        bzIssueDTO.setBzIssue(bzIssueService.getById(bzSearchDTO.getId()));
        List<DeptDTO> deptDTOs = null;
        HttpUtil httpUtil = new HttpUtil();
        String deptJson = httpUtil.get(parameterDTO.getPermissionUrl(), authorizationHeader, tenantId);
        if (deptJson != null) {
            deptDTOs = JSON.parseArray(deptJson, DeptDTO.class);
            System.out.println("Dept list size: " + deptDTOs.size());
        } else {
            return new BaseResponse(HttpStatus.OK.value(), "鉴权失败，获取权限失败！", false, Integer.toString(0));
        }
        bzSearchDTO.setBzIssueId(bzSearchDTO.getId());
        bzIssueDTO.setBzIssueTargetList(bzIssueTargetService.getByIssueId(bzSearchDTO, deptDTOs));

        return new BaseResponse(HttpStatus.OK.value(), "success", bzIssueDTO, Integer.toString(0));
    }

    @PostMapping("/add")
    public BaseResponse saveOrUpdateTasks(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                          @RequestHeader(value = "tenant-id", required = false) String tenantId,
                                          @RequestBody BzIssueDTO bzIssueDTO) {
        if (!configService.getConfig(tenantId)) {
            return new BaseResponse(HttpStatus.OK.value(), "success", null, Integer.toString(0));
        }

        BzIssue bzIssue = bzIssueDTO.getBzIssue();
        long count = bzIssueService.countBzIssue(bzIssue);
        if (count == -1) {
            return new BaseResponse(HttpStatus.METHOD_NOT_ALLOWED.value(), "参数错误", null, Integer.toString(0));
        } else if (count > 0) {
            return new BaseResponse(HttpStatus.GONE.value(), "已经存在该报表", null, Integer.toString(0));
        }

        bzIssue.setOperator(bzIssue.getAssigner());
        bzIssue.setOperatorId(bzIssue.getAssignerId());
        for (BzIssueTarget bzIssueTarget : bzIssueDTO.getBzIssueTargetList()) {
            bzIssue.setResponsibleDept(util.joinString(bzIssue.getResponsibleDept(), bzIssueTarget.getDept()));
            bzIssue.setResponsibleDeptId(util.joinString(bzIssue.getResponsibleDeptId(), bzIssueTarget.getDeptId()));
        }
        Integer id = bzIssueService.insertBzIssue(bzIssue);
        if (id == null) {
            return new BaseResponse(HttpStatus.NO_CONTENT.value(), "failed", id, Integer.toString(0));
        }
        for (BzIssueTarget bzIssueTarget : bzIssueDTO.getBzIssueTargetList()) {
            bzIssueTarget.setBzIssueId(id);
            bzIssueTarget.setLeadingDepartment(bzIssue.getLeadingDepartment());
            bzIssueTarget.setLeadingDepartmentId(bzIssue.getLeadingDepartmentId());
            if (bzIssueTarget.getAssigner() == null || bzIssueTarget.getAssignerId() == null) {
                bzIssueTarget.setAssigner(bzIssue.getAssigner());
                bzIssueTarget.setAssignerId(bzIssue.getAssignerId());
            }
            bzIssueTarget.setOperator(bzIssue.getAssigner());
            bzIssueTarget.setOperatorId(bzIssue.getAssignerId());
        }
        if (bzIssueDTO.getBzIssueTargetList().size() != 0) {
            bzIssueTargetService.saveBatch(bzIssueDTO.getBzIssueTargetList());
        }

        return new BaseResponse(HttpStatus.OK.value(), "success", 0, Integer.toString(0));
    }

    @PutMapping("/update")
    public BaseResponse updateBzIssue(@RequestBody BzIssue bzIssue) {
        BzIssue bi = bzIssueService.getById(bzIssue.getId());
        if (bi.getLeadingDepartmentId() != null && !bi.getLeadingDepartmentId().equals(bzIssue.getLeadingDepartmentId())) {
            BzSearchDTO bzSearchDTO = new BzSearchDTO();
            bzSearchDTO.setBzIssueId(bzIssue.getId().longValue());

            List<BzIssueTarget> bzIssueTargetList = bzIssueTargetService.getByIssueId(bzSearchDTO, null);
            for (BzIssueTarget bzIssueTarget : bzIssueTargetList) {
                bzIssueTarget.setLeadingDepartmentId(bzIssue.getLeadingDepartmentId());
                bzIssueTarget.setLeadingDepartment(bzIssue.getLeadingDepartment());
            }
            bzIssueTargetService.updateBatchById(bzIssueTargetList);
        }
        boolean upate = bzIssueService.updateBzIssue(bzIssue);
        return new BaseResponse(HttpStatus.OK.value(), "success", upate, Integer.toString(0));
    }

    //审核清单
    @PutMapping("/all/update")
    public BaseResponse checkBzIssue(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                     @RequestHeader(value = "tenant-id", required = false) String tenantId,
                                     @RequestBody BzIssueDTO bzIssueDTO) {
        if (!configService.getConfig(tenantId)) {
            return new BaseResponse(HttpStatus.OK.value(), "success", null, Integer.toString(0));
        }

        BzIssue bzIssue = bzIssueDTO.getBzIssue();
        for (BzIssueTarget bzIssueTarget : bzIssueDTO.getBzIssueTargetList()) {
            bzIssueTarget.setBzIssueId(bzIssue.getId());
            //需要修改牵头单位到每个target中去
            bzIssueTarget.setLeadingDepartment(bzIssue.getLeadingDepartment());
            bzIssueTarget.setLeadingDepartmentId(bzIssue.getLeadingDepartmentId());

            //将每个指标的责任单位写入到清单中
            bzIssue.setResponsibleDept(util.joinString(bzIssue.getResponsibleDept(), bzIssueTarget.getDept()));
            bzIssue.setResponsibleDeptId(util.joinString(bzIssue.getResponsibleDeptId(), bzIssueTarget.getDeptId()));
        }

        bzIssueTargetService.saveOrUpdateBatch(bzIssueDTO.getBzIssueTargetList());
        boolean upate = bzIssueService.updateBzIssue(bzIssue);
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
            for (int level = 1; level <= 5; level++) {
                CountDTO countDTO = new CountDTO(0, "0%");
                dataTypeDTO.getCountDTOMap().put(level, countDTO);
            }

            dataList.add(dataTypeDTO);
        }
        List<EffectiveGearCount> bzFormGearCounts = bzIssueService.countGearCollect();
        for (EffectiveGearCount bzFormGearCount : bzFormGearCounts) {
            DataTypeDTO dataType = dataList.get(0);
            CountDTO levelData = new CountDTO(bzFormGearCount.getCountEffectiveGear().intValue(), String.format("%d%%", 0));
            dataType.getCountDTOMap().put(bzFormGearCount.getEffectiveGear(), levelData);
            dataType.setTotal(dataType.getTotal() + bzFormGearCount.getCountEffectiveGear().intValue());
        }

        List<Map<String, Object>> countList = bzIssueService.countEffectiveGear();

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

    @GetMapping("/grearsByDate")
    public BaseResponse grearsByDate(@ModelAttribute BzSearchDTO bzSearch) {
        LocalDate now = LocalDate.now();
        List<DateInfo> dateInfos = null;
        if (bzSearch.getDateType() == 2) {
            dateInfos = DateUtils.getCurrentQuarters();
        } else {
            dateInfos = DateUtils.getCurrentYears();
        }

        // 第一层：季度或者年；第二层：八层表；值为1-5（A-E）
//        HashMap<Integer, Map<Integer, Integer>> collectMap = new HashMap<>();
        BzType bzType = new BzType();
        bzType.setType("2");
        bzType.setDelete(false);
        List<BzType> bzTypeByContains = bzTypeService.getBzTypeByContains(bzType);
        LinkedHashMap<Integer, LinkedHashMap<String, Integer>> collectMap = new LinkedHashMap<>();

        for (DateInfo dateInfo : dateInfos) {
            LinkedHashMap<String, Integer> dateMap = new LinkedHashMap<>();

//            for (int i = 1; i <= 8; i++) {
//                dateMap.put(i, 0);
//            }
            for (BzType bt : bzTypeByContains) {
                dateMap.put(bt.getName(), 0);
            }
            collectMap.put(dateInfo.getNumber(), dateMap);
        }

        List<BzIssue> gearsByConditions = bzIssueService.getGearsByConditions(bzSearch);
        for (BzIssue bzIssue : gearsByConditions) {

            Map<String, Integer> typeIdS = null;
            if (bzSearch.getDateType() == 1) { //按照年
//                if (bzIssue.getYear() > now.getYear()) {
//                    continue;
//                }
                typeIdS = collectMap.get(bzIssue.getYear());
            } else {
//                if (bzIssue.getQuarter() > ((now.getMonthValue() - 1) / 3 + 1)) {
//                    continue;
//                }
                typeIdS = collectMap.get(bzIssue.getQuarter());
            }

            Integer gear = bzIssue.getActualGear();

            if (typeIdS.containsKey(bzIssue.getType()) == false) {
                continue;
            }

            if (bzIssue.getActualGear() == null) {
                gear = bzIssue.getPredictedGear();
            }
            typeIdS.put(bzIssue.getType(), gear);
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

    @GetMapping(value = "/export")
    public void export(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                       @RequestHeader(value = "tenant-id", required = false) String tenantId,
                       @ModelAttribute BzSearchDTO bzSearchDTO,
                       HttpServletResponse response) throws Exception {

        List<IssueTemplateExcel> issueTemplateExcels = new ArrayList<>();
        List<DeptDTO> deptDTOs = null;
        HttpUtil httpUtil = new HttpUtil();
        String deptJson = httpUtil.get(parameterDTO.getPermissionUrl(), authorizationHeader, tenantId);
        if (deptJson != null) {
            deptDTOs = JSON.parseArray(deptJson, DeptDTO.class);
            System.out.println("Dept list size: " + deptDTOs.size());
        } else {
            return;
        }
        IPage<BzIssue> bzIssueByConditions = bzIssueService.getBzIssueByConditions2(bzSearchDTO, 1, 100, deptDTOs);
        for (BzIssue bzIssue : bzIssueByConditions.getRecords()) {
            bzSearchDTO.setBzIssueId(bzIssue.getId().longValue());
            List<BzIssueTarget> bzIssueTargets = bzIssueTargetService.getByIssueId(bzSearchDTO, deptDTOs);
            for (BzIssueTarget bzIssueTarget : bzIssueTargets) {
                IssueTemplateExcel issueTemplateExcel = new IssueTemplateExcel();
                issueTemplateExcel.setType(bzIssue.getType());
                if (bzIssue.getPredictedGear() != null) {
                    issueTemplateExcel.setPredictedGear(String.valueOf((char) (bzIssue.getPredictedGear() + 'A' - 1)));
                }
                issueTemplateExcel.setName(bzIssueTarget.getName());
                issueTemplateExcel.setWorkProgress(bzIssueTarget.getWorkProgress());
                issueTemplateExcel.setIssues(bzIssueTarget.getIssues());
                issueTemplateExcel.setDept(bzIssueTarget.getDept());
                System.out.println("-------->>> IssueTemp: " + issueTemplateExcel.toString());
                issueTemplateExcels.add(issueTemplateExcel);
            }
        }

        ExcelUtil.exportExcelToTargetWithTemplate(response, null, "任务", issueTemplateExcels, IssueTemplateExcel.class, "doc/bzIssue.xlsx");
    }
}