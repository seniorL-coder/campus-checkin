package com.wangwei.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ClassTeacherRelation implements Serializable {
    // 班级ID
    private Long classId;
    // 教师ID
    private Long teacherId;
}
