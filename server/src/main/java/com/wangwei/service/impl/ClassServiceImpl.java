package com.wangwei.service.impl;

import com.wangwei.context.BaseContext;
import com.wangwei.dto.AddClassDTO;
import com.wangwei.dto.UpdateClassDTO;
import com.wangwei.entity.Class;
import com.wangwei.entity.ClassTeacherRelation;
import com.wangwei.entity.User;
import com.wangwei.exception.ResourceNotEmptyException;
import com.wangwei.mapper.ClassMapper;
import com.wangwei.mapper.UserMapper;
import com.wangwei.service.ClassService;
import com.wangwei.service.ClassTeacherRelationService;
import com.wangwei.vo.ClassVO;
import com.wangwei.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {
    private final ClassMapper classMapper;
    private  final ClassTeacherRelationService classTeacherRelationService;
    private final UserMapper userMapper;

    /**
     * 添加班级
     *
     * @param addClassDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(AddClassDTO addClassDTO) {
        Class newClass = Class.builder().className(addClassDTO.getClassName())
                .major(addClassDTO.getMajor()).build();
        classMapper.add(newClass);

        // 添加 班级与教师关系 teacherID 已经传递, class_id 已通过 mapper 配置返回
       ClassTeacherRelation classTeacherRelation=  ClassTeacherRelation.builder().classId(newClass.getId()).teacherId(addClassDTO.getTeacherId()).build();
        classTeacherRelationService.add(classTeacherRelation);
    }

    @Override
    public void update(UpdateClassDTO updateClassDTO) {
        Class updateClass = Class.builder().id(updateClassDTO.getId())
                .className(updateClassDTO.getClassName()).major(updateClassDTO.getMajor()).build();
        classMapper.updateById(updateClass);
    }

    /**
     * 根据id删除班级
     * @param id
     */
    @Override
    public void deleteById(Integer id) {
        // 首先要确认班级下没有学生
        // 1. 有学生则不允许删除
        // 2. 没有学生则允许删除(同时要删除班级与教师关系)
        List<UserVO> students = userMapper.getStudentsByClassId(id);
        if(Objects.isNull(students) || students.isEmpty()) {
            // 删除班级
            classMapper.deleteById(id);
            // 删除班级与教师关系
            classTeacherRelationService.deleteByClassId(id);
        } else {
            throw new ResourceNotEmptyException("班级下有学生，不允许删除");
        }
    }

    /**
     * 获取所有班级列表
     * @return
     */
    @Override
    public List<ClassVO> list() {
        Long id = BaseContext.getCurrentId();
        return classMapper.list(id);
    }
}
