package com.zzn.student.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzn.student.dao.StudentMapper;
import com.zzn.student.entity.Student;
import com.zzn.student.service.StudentService;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper,Student> implements StudentService {
}
