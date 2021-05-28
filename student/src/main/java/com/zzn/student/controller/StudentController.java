package com.zzn.student.controller;

import com.zzn.student.entity.Student;
import com.zzn.student.service.StudentService;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @GetMapping("/list")
    public List<Student> getList(){
        List<Student> list = studentService.list();
        return list;
    }

    @PostMapping("/")
    public Boolean add(@RequestBody Student student){
        boolean save = studentService.save(student);
        return save;
    }

    @PutMapping("/")
    public Boolean update(@RequestBody Student student){
        boolean save = studentService.updateById(student);
        return save;
    }

    @DeleteMapping("/{id}")
    public Boolean del(@PathVariable("id")Long id){
        boolean b = studentService.removeById(id);
        return b;
    }
 }
