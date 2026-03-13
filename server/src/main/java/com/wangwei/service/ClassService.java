package com.wangwei.service;

import com.wangwei.dto.AddClassDTO;
import com.wangwei.dto.UpdateClassDTO;

public interface ClassService {
    /**
     * 添加班级
     * @param classDTO
     */
    void add(AddClassDTO classDTO);

    /**
     * 更新班级
     * @param updateClassDTO
     */
    void update(UpdateClassDTO updateClassDTO);

    /**
     * 根据id删除班级
     * @param id
     */
    void deleteById(Integer id);
}
