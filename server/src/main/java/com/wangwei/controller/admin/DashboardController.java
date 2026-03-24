package com.wangwei.controller.admin;

import com.wangwei.result.Result;
import com.wangwei.service.UserService;
import com.wangwei.vo.StudentStatsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final UserService userService;

    /**
     * 获取学生统计信息
     * @return
     */
    @GetMapping("/student/stats")
    public Result<StudentStatsVO> getStudentStats() {
        // 相应的 service 方法
        StudentStatsVO stats = StudentStatsVO.builder()
                .totalCount(userService.getTotalStudentCount())
                .todayCount(userService.getTodayNewStudentCount())
                .build();
        return Result.success(stats);
    }

}
