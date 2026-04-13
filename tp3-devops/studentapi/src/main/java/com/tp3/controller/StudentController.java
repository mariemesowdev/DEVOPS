package com.tp3.controller;

import com.tp3.model.Student;
import com.tp3.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping
    public List<Student> getAll() {
        return studentRepository.findAll();
    }

    @GetMapping("/{id}")
    public Student getById(@PathVariable Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Student create(@RequestBody Student student) {
        return studentRepository.save(student);
    }

    @PutMapping("/{id}")
    public Student update(@PathVariable Long id, @RequestBody Student student) {
        student.setId(id);
        return studentRepository.save(student);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        studentRepository.deleteById(id);
    }
}
