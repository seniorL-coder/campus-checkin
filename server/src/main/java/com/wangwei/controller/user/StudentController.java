package com.wangwei.controller.user;

import com.wangwei.dto.StudentLoginDTO;
import com.wangwei.entity.Student;
import com.wangwei.result.Result;
import com.wangwei.service.StudentService;
import com.wangwei.vo.LoginVO;
import com.wangwei.vo.StudentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("userStudentController")
@RequestMapping("/user/student")
@RequiredArgsConstructor
@Tag(name = "学生用户相关接口")
public class StudentController {

    private final StudentService studentService;


    @Operation(summary = "学生登录")
    @PostMapping("/login")
    public Result<StudentVO> login(@RequestBody StudentLoginDTO studentLoginDTO) {
        StudentVO studentVO = studentService.login(studentLoginDTO);
        return Result.success(studentVO);
    }

    @Operation(summary = "学生退出登录")
    @PostMapping("/logout")
    public Result<String> logout() {
        studentService.logout();
        return Result.success("退出登录成功");
    }

}
