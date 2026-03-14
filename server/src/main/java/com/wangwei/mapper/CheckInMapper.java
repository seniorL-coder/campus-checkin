package com.wangwei.mapper;

import com.wangwei.entity.CheckIn;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CheckInMapper {
    /**
     * 插入签到记录
     *
     * @param checkIns 签到记录 List<CheckIn>
     */
    void insertCheckIns(List<CheckIn> checkIns);
}
