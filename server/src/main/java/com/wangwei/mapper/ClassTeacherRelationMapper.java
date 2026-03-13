package com.wangwei.mapper;

import com.wangwei.entity.ClassTeacherRelation;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ClassTeacherRelationMapper {
    /**
     * 插入班级教师关系
     *
     * @param classTeacherRelation classId teacherId 必填
     */
    @Insert("INSERT INTO t_class_teacher_relation (class_id, teacher_id) VALUES (#{classId}, #{teacherId})")
    void insert(ClassTeacherRelation classTeacherRelation);

    /**
     * 根据班级id删除班级与教师关系
     *
     * @param id 班级id
     */
    @Delete("DELETE FROM t_class_teacher_relation WHERE class_id = #{id}")
    void deleteByClassId(Integer id);
}
