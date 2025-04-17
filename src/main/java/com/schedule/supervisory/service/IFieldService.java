package com.schedule.supervisory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.supervisory.entity.BzType;
import com.schedule.supervisory.entity.Field;

import java.util.List;
import java.util.Map;

public interface IFieldService extends IService<Field> {
    List<Field> getFields(Boolean delete);

    List<Field> getFieldsByParentId(Boolean delete, Long parentId);

    List<Map<String, Object>> buildTree(Map<Integer, List<Field>> fieldMap, int parentId);

    List<Map<String, Object>> getTreeData(Boolean delete);

    Field getFieldByName(String name);

    boolean deleteById(Long id);

    int insertField(Field field);

    boolean recover(Long id);
}
