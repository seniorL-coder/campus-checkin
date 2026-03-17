package com.wangwei.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wangwei.constant.UserInfoConstant;
import com.wangwei.context.BaseContext;
import com.wangwei.dto.StudentDTO;
import com.wangwei.dto.StudentLoginDTO;
import com.wangwei.dto.StudentQueryDTO;
import com.wangwei.entity.Student;
import com.wangwei.exception.PasswordErrorException;
import com.wangwei.mapper.ClassTeacherRelationMapper;
import com.wangwei.mapper.StudentMapper;
import com.wangwei.properties.JwtProperties;
import com.wangwei.result.PageResult;
import com.wangwei.service.StudentService;
import com.wangwei.utils.JwtUtils;
import com.wangwei.utils.SHA256Util;
import com.wangwei.vo.ClassVO;
import com.wangwei.vo.StudentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StudentMapper studentMapper;
    private final ClassTeacherRelationMapper classTeacherRelationMapper;
    private final JwtUtils jwtUtils;
    private final JwtProperties jwtProperties;

    private static final String REDIS_TOKEN_KEY = "login:student:";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PageResult<StudentVO> list(StudentQueryDTO studentQueryDTO) {
        // 1. 从BaseContent 获取当前教师Id;
        // 2. 判断前端传递的classId 是否为空，不为空则查询该班级的学生，为空则查询该教师所有班级的学生
        // 2.1 如果为空, 需要查询中间表 拿到该教师所有班级的ID
        Long teacherId = BaseContext.getCurrentId();
        List<Long> classIds;
        if (studentQueryDTO.getClassId() == null) {
            // 2.1 如果为空, 需要查询中间表 拿到该教师所有班级的ID
            classIds = classTeacherRelationMapper.getClassesByTeacherId(teacherId);
        } else {
            classIds = List.of(studentQueryDTO.getClassId());
        }
        log.info("classIds: {}", classIds);
        studentQueryDTO.setTeacherId(teacherId);
        PageHelper.startPage(studentQueryDTO.getPage(), studentQueryDTO.getLimit());
        List<StudentVO> list = studentMapper.list(classIds, studentQueryDTO);
        Page<StudentVO> page = (Page<StudentVO>) list;
        return new PageResult<>(page.getPageNum(), page.getPages(), page.getPageSize(), page.getTotal(), page.getResult());


    }

    /**
     * 添加学生
     *
     * @param studentDTO 学生信息
     */
    @Override
    public void add(StudentDTO studentDTO) {
        String defaultPassword = SHA256Util.encrypt(UserInfoConstant.DEFAULT_PASSWORD);
        // 判断头像是否为空，为空则使用默认头像
        String avatar = StringUtils.isBlank(studentDTO.getAvatar())
                ? UserInfoConstant.DEFAULT_AVATAR + studentDTO.getUsername()
                : studentDTO.getAvatar();
        Student student = new Student();
        student.setUsername(studentDTO.getUsername());
        student.setPassword(defaultPassword);
        student.setRealName(studentDTO.getRealName());
        student.setAvatar(avatar);
        student.setClassId(studentDTO.getClassId());
        student.setRole(UserInfoConstant.DEFAULT_STUDENT_ROLE);
        studentMapper.add(student);
    }

    /**
     * 学生登录
     *
     * @param studentLoginDTO username and password
     */
    @Override
    public StudentVO login(StudentLoginDTO studentLoginDTO) {
        if (studentLoginDTO.getUsername() == null || studentLoginDTO.getPassword() == null || studentLoginDTO.getUsername().isEmpty() || studentLoginDTO.getPassword().isEmpty()) {
            throw new PasswordErrorException("用户名或密码为空");
        }
        // 加密密码
        String encryptedPassword = SHA256Util.encrypt(studentLoginDTO.getPassword());
        // 比对数据库的用户名和密码
        Student student = studentMapper.login(studentLoginDTO.getUsername(), encryptedPassword);
        if (student == null) {
            throw new PasswordErrorException("用户名或密码错误");
        }
        // 根据学生班级id查询班级信息
        log.info("student{}", student);
        ClassVO classVO = studentMapper.getStudentClassById(student.getClassId());
        // 登录成功，返回学生信息 和 token
        Map<String, Object> map = new HashMap<>();
        map.put("userId", student.getId());
        map.put("username", student.getUsername());
        map.put("role", student.getRole());
        String token = jwtUtils.generateToken(map);
        // 存入Redis , 这里 直接用 key 作为键, 覆盖掉旧的值 实现 单点登录
        redisTemplate.opsForValue().set(REDIS_TOKEN_KEY + student.getId(), token, jwtProperties.getTtl(), TimeUnit.SECONDS);
        return StudentVO.builder()
                .id(student.getId())
                .classId(student.getClassId())
                .role(student.getRole())
                .avatar(student.getAvatar())
                .realName(student.getRealName())
                .username(student.getUsername())
                .className(classVO.getClassName())
                .teacherName(classVO.getTeacherName())
                .token(token)
                .createTime(student.getCreateTime())
                .updateTime(student.getUpdateTime()).build();

    }

    /**
     * 登出
     */
    @Override
    public void logout() {
        Long userId = BaseContext.getCurrentId();
        redisTemplate.delete(REDIS_TOKEN_KEY + userId);

    }
}
