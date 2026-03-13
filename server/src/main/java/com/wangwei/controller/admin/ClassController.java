package com.wangwei.controller.admin;

import com.wangwei.dto.AddClassDTO;
import com.wangwei.dto.UpdateClassDTO;
import com.wangwei.result.Result;
import com.wangwei.service.ClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/class")
@Tag(name = "班级管理")
@Slf4j
@RequiredArgsConstructor
public class ClassController {
    private final ClassService classService;

    @PostMapping
    @Operation(summary = "添加班级")
    public Result<String> addClass(@RequestBody AddClassDTO addClassDTO) {
        classService.add(addClassDTO);
        return Result.success("添加班级成功");
    }

    @PutMapping
    @Operation(summary = "更新班级")
    public Result<String> updateClass(@RequestBody UpdateClassDTO updateClassDTO) {
        classService.update(updateClassDTO);
        return Result.success("更新班级成功");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除班级")
    public Result<String> deleteClass(@PathVariable Integer id) {
        classService.deleteById(id);
        return Result.success("删除班级成功");
    }
}
