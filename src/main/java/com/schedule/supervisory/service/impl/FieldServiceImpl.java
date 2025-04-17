package com.schedule.supervisory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.supervisory.dao.mapper.FieldMapper;
import com.schedule.supervisory.entity.BzType;
import com.schedule.supervisory.entity.Field;
import com.schedule.supervisory.service.IFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FieldServiceImpl extends ServiceImpl<FieldMapper, Field> implements IFieldService {
    @Autowired
    private FieldMapper fieldMapper;

    @Override
    public List<Field> getFields(Boolean delete) {
        LambdaQueryWrapper<Field> queryWrapper = new LambdaQueryWrapper<>();
        if (delete != null) {
            queryWrapper.eq(Field::isDelete, delete); // 查询没有删除
        }
        queryWrapper.orderByAsc(Field::getId);
        return this.list(queryWrapper);
    }

    @Override
    public List<Field> getFieldsByParentId(Boolean delete, Long parentId) {
        LambdaQueryWrapper<Field> queryWrapper = new LambdaQueryWrapper<>();
        if (delete != null) {
            queryWrapper.eq(Field::isDelete, delete); // 查询没有删除
        }
        queryWrapper.eq(Field::getParentId, parentId);
        queryWrapper.orderByAsc(Field::getId);
        return this.list(queryWrapper);
    }

    @Override
    public List<Map<String, Object>> buildTree(Map<Integer, List<Field>> fieldMap, int parentId) {
        // 获取当前父级 ID 对应的所有子节点
        List<Field> children = fieldMap.getOrDefault(parentId, Collections.emptyList());

        // 转换为树形结构
        return children.stream().map(field -> {
            Map<String, Object> node = new HashMap<>();
            node.put("value", field.getId().intValue());
            node.put("label", field.getName());

            // 递归获取子节点
            List<Map<String, Object>> subChildren = buildTree(fieldMap, field.getId().intValue());
            if (!subChildren.isEmpty()) {
                node.put("children", subChildren);
            }

            return node;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getTreeData(Boolean delete) {
        // 查询所有数据
        List<Field> fields = getFields(delete);

        // 转换为 Map，方便按 parent_id 查找
        Map<Integer, List<Field>> fieldMap = fields.stream()
                .collect(Collectors.groupingBy(Field::getParentId));

        // 构建树形结构
        return buildTree(fieldMap, 0);
    }

    @Override
    public Field getFieldByName(String name) {
        LambdaQueryWrapper<Field> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Field::getName, name); // 查询没有删除
        return getOne(queryWrapper);
    }

    @Override
    public boolean deleteById(Long id) {
        LambdaUpdateWrapper<Field> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Field::getId, id);
        updateWrapper.set(Field::isDelete, true);
        return update(updateWrapper);
    }

    @Override
    public int insertField(Field field) {
        return fieldMapper.insert(field);
    }

    /**
     * 重新启用
     *
     * @param id
     * @return
     */
    @Override
    public boolean recover(Long id) {
        LambdaUpdateWrapper<Field> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Field::getId, id);
        updateWrapper.set(Field::isDelete, false);
        return update(updateWrapper);
    }
}