package com.wangwei.service.impl;

import com.wangwei.entity.ClassTeacherRelation;
import com.wangwei.mapper.ClassTeacherRelationMapper;
import com.wangwei.service.ClassTeacherRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClassTeacherRelationServiceImpl implements ClassTeacherRelationService {
    private final ClassTeacherRelationMapper classTeacherRelationMapper;

    /**
     * 添加班级教师关系
     * @param classTeacherRelation
     */
    @Override
    public void add(ClassTeacherRelation classTeacherRelation) {
        classTeacherRelationMapper.insert(classTeacherRelation);
    }

    /**
     * 根据班级id删除班级与教师关系
     * @param id
     */
    @Override
    public void deleteByClassId(Integer id) {
        classTeacherRelationMapper.deleteByClassId(id);
    }
}
