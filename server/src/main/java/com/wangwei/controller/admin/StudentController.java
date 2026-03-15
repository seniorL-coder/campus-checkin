package com.wangwei.controller.admin;

import com.wangwei.dto.StudentQueryDTO;
import com.wangwei.result.PageResult;
import com.wangwei.result.Result;
import com.wangwei.service.StudentService;
import com.wangwei.vo.StudentVO;
import com.wangwei.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/student")
@RequiredArgsConstructor
@Tag(name = "学生管理")
public class StudentController {

    private final StudentService studentService;

    @Operation(summary = "获取学生列表")
    @PostMapping("/list")
    public Result<PageResult<StudentVO>> list(@RequestBody StudentQueryDTO studentQueryDTO) {
        PageResult<StudentVO> list = studentService.list(studentQueryDTO);
        return Result.success(list);
    }
}
