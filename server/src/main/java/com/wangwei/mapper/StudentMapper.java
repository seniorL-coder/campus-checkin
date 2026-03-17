package com.wangwei.mapper;

import com.wangwei.annotation.AutoFill;
import com.wangwei.dto.StudentQueryDTO;
import com.wangwei.entity.Student;
import com.wangwei.enumeration.OperationType;
import com.wangwei.vo.ClassVO;
import com.wangwei.vo.LoginVO;
import com.wangwei.vo.StudentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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

    // 根据学生id查询自己班级信息
    ClassVO getStudentClassById(Long classId);
    /**
     * 学生登录
     * @param username 用户名
     * @param encryptedPassword 加密密码
     * @return Student
     */
    @Select("SELECT id, username, password, real_name, role, class_id, create_time, update_time, avatar FROM t_user WHERE username = #{username} AND password = #{encryptedPassword}")
    Student login(String username, String encryptedPassword);
}
