package com.wangwei.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wangwei.context.BaseContext;
import com.wangwei.dto.StudentQueryDTO;
import com.wangwei.mapper.ClassTeacherRelationMapper;
import com.wangwei.mapper.StudentMapper;
import com.wangwei.result.PageResult;
import com.wangwei.service.StudentService;
import com.wangwei.vo.StudentVO;
import com.wangwei.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentMapper studentMapper;
    private final ClassTeacherRelationMapper classTeacherRelationMapper;

    @Override
    public PageResult<StudentVO> list(StudentQueryDTO studentQueryDTO) {
        // 1. 从BaseContent 获取当前教师Id;
        // 2. 判断前端传递的classId 是否为空，不为空则查询该班级的学生，为空则查询该教师所有班级的学生
        // 2.1 如果为空, 需要查询中间表 拿到该教师所有班级的ID
        Long teacherId = BaseContext.getCurrentId();
        List<Long> classIds;
        if (studentQueryDTO.getClassId() == null) {
            // 2.1 如果为空, 需要查询中间表 拿到该教师所有班级的ID
            classIds = classTeacherRelationMapper.getClassesByTeacherId(teacherId);
        } else {
            classIds = List.of(studentQueryDTO.getClassId());
        }
        log.info("classIds: {}", classIds);
        studentQueryDTO.setTeacherId(teacherId);
        PageHelper.startPage(studentQueryDTO.getPage(), studentQueryDTO.getLimit());
        List<StudentVO> list = studentMapper.list(classIds, studentQueryDTO);
        Page<StudentVO> page = (Page<StudentVO>) list;
        return new PageResult<>(page.getPageNum(), page.getPages(), page.getPageSize(), page.getTotal(), page.getResult());


    }
}
