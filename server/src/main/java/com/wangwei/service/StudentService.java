package com.wangwei.service;

import com.wangwei.dto.StudentDTO;
import com.wangwei.dto.StudentLoginDTO;
import com.wangwei.dto.StudentQueryDTO;
import com.wangwei.entity.Student;
import com.wangwei.result.PageResult;
import com.wangwei.vo.LoginVO;
import com.wangwei.vo.StudentVO;
import com.wangwei.vo.UserVO;

import java.util.List;

public interface StudentService {
    /**
     * 分页获取学生列表
     * @param studentQueryDTO 查询条件
     * @return 学生列表
     */
    PageResult<StudentVO> list(StudentQueryDTO studentQueryDTO);

    /**
     * 添加学生
     * @param studentDTO 学生信息
     */
    void add(StudentDTO studentDTO);

    /**
     * 学生登录
     * @param  studentLoginDTO username and password
     */
    StudentVO login(StudentLoginDTO studentLoginDTO);

    /**
     * 登出
     */
    void logout();
}
