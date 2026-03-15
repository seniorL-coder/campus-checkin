package com.wangwei.service;

import com.wangwei.dto.StudentQueryDTO;
import com.wangwei.result.PageResult;
import com.wangwei.vo.StudentVO;
import com.wangwei.vo.UserVO;

import java.util.List;

public interface StudentService {
    PageResult<StudentVO> list(StudentQueryDTO studentQueryDTO);
}
