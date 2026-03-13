package com.wangwei.mapper;

import com.wangwei.annotation.AutoFill;
import com.wangwei.entity.Class;
import com.wangwei.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ClassMapper {
    /**
     * 添加班级
     * @param newClass
     */
    @AutoFill(value = OperationType.INSERT)
    void add(Class newClass);

    /**
     * 更新班级
     * @param updateClass
     */
    @AutoFill(value = OperationType.UPDATE)
    void updateById(Class updateClass);
    /**
     * 删除班级
     * @param id
     */
    @Delete("delete from t_class where id = #{id}")
    void deleteById(Integer id);
}
