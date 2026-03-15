package com.wangwei.mapper;

import com.wangwei.dto.StudentQueryDTO;
import com.wangwei.vo.StudentVO;
import com.wangwei.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentMapper {
    List<StudentVO> list(@Param("classIds") List<Long> classIds, StudentQueryDTO studentQueryDTO);
}
