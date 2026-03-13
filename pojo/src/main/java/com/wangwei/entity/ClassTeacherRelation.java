package com.wangwei.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ClassTeacherRelation implements Serializable {
    // 班级ID
    private Integer classId;
    // 教师ID
    private Integer teacherId;
}
