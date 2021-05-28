package com.zzn.student;

import com.zzn.student.entity.Student;
import com.zzn.student.utils.DBUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ArrayList<Student> array = new ArrayList<>();

        while (true) {
            System.out.println("--------欢迎来到学生管理系统--------");
            System.out.println("1 添加学生");
            System.out.println("2 删除学生");
            System.out.println("3 修改学生");
            System.out.println("4 查看所有学生");
            System.out.println("5 退出学生信息管理系统");
            System.out.println("请输入你的选择：");

            Scanner sc = new Scanner(System.in);
            String line = sc.nextLine();

            switch (line) {
                case "1":
//                    System.out.println("添加学生");
                    addStudent();
                    break;
                case "2":
//                    System.out.println("删除学生");
                    deleteStudent(array);
                    break;
                case "3":
//                    System.out.println("修改学生");
                    updateStudent(array);
                    break;
                case "4":
//                    System.out.println("查看所有学生");
                    findAllStudent(array);
                    break;
                case "5":
                    System.out.println("谢谢使用!");
                    System.exit(0);
            }
        }
    }

    public static void addStudent() {
        Scanner sc = new Scanner(System.in);

        String name=null;
        Long sid=null;
        try {
            while(true) {
                System.out.println("请输入学生姓名：");
                name = sc.nextLine();
                System.out.println("请输入学生学号：");
                String sidStr = sc.nextLine();
                sid=Long.parseLong(sidStr);
                boolean flag = exist(sid);
                if (flag) {
                    System.out.println("该学号已经被使用，请重新输入");
                }else{
                    break;
                }
            }

            System.out.println("请输入学生年龄：");
            Integer age = Integer.parseInt(sc.nextLine());
            System.out.println("请输入学生居住地：");
            String address = sc.nextLine();

            Student s = new Student();
            s.setName(name);
            s.setId(sid);
            s.setAge(age);
            s.setAddress(address);
            DBUtils.executeUpdateStudent("insert into student (sid,name,age,address) values(?,?,?,?)",s,false);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        array.add(s);
        System.out.println("\n添加成功!");
    }

    public static void findAllStudent(ArrayList<Student> array) {
        if (array.size() == 0) {
            System.out.println("无学生信息，请先添加后再查询");
            return;
        }

        for (int i = 0; i < array.size(); i++) {
            Student s = array.get(i);
            System.out.println("姓名：" + s.getName() + "\t\t学号：" + s.getId() + "\t\t年龄：" + s.getAge() + "\t\t居住地：" + s.getAddress());
        }
    }

    public static void deleteStudent(ArrayList<Student> array) {
        Scanner sc = new Scanner(System.in);

        System.out.println("请输入需要删除的学生的学号：");
        String sid = sc.nextLine();
        int index = -1;
        for (int i = 0; i < array.size(); i++) {
            Student s = array.get(i);
            if (s.getId().equals(sid)) {
                index = i;
                array.remove(i);
                System.out.println("删除成功！");
                break;
            } else {
                System.out.println("删除失败，没有该学生信息");
            }
        }
    }

    public static void updateStudent(ArrayList<Student> array) {
        Scanner sc = new Scanner(System.in);

        System.out.println("请输入需要修改的学生的学号：");
        String sid = sc.nextLine();

        System.out.println("请输入学生新姓名：");
        String name = sc.nextLine();
        System.out.println("请输入学生新年龄：");
        String age = sc.nextLine();
        System.out.println("请输入学生新居住地：");
        String address = sc.nextLine();

        Student s = new Student();
        s.setName(name);
//        s.setSid(sid);
//        s.setAge(age);
        s.setAddress(address);

        for (int i = 0; i < array.size(); i++) {
            Student student = array.get(i);
            if (student.getId().equals(sid)) {
                array.set(i, s);
                System.out.println("修改成功！");
                break;
            }
        }
    }

    public static boolean exist(Long sid){
        boolean flag = false;
        List<Student> students = DBUtils.executeQuery("select * from student where sid=?", sid);
        if(students!=null&&students.size()>0){
            flag=true;
        }
        return flag;

//        for(int i = 0;i < array.size();i++) {
//            Student s = array.get(i);
//            if (s.getSid().equals(sid)) {
//                flag = true;
//                break;
//            }
//        }
//        return flag;
    }
}
