package com.wangwei.service;

import com.wangwei.entity.ClassTeacherRelation;

public interface ClassTeacherRelationService {
    void add(ClassTeacherRelation classTeacherRelation);

    /**
     * 根据班级id删除班级与教师关系
     * @param id
     */
    void deleteByClassId(Integer id);
}
