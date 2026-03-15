package com.wangwei.mapper;

import com.wangwei.annotation.AutoFill;
import com.wangwei.dto.StudentQueryDTO;
import com.wangwei.entity.Student;
import com.wangwei.enumeration.OperationType;
import com.wangwei.vo.StudentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentMapper {
    List<StudentVO> list(@Param("classIds") List<Long> classIds, StudentQueryDTO studentQueryDTO);

    /**
     * 添加学生
     * @param student 学生信息
     */
    @AutoFill(OperationType.INSERT)
    void add(Student student);
}
