package com.example.mybatisplus.controller;

import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.mybatisplus.service.RoleService;
import com.example.mybatisplus.entity.RolePojo;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author AlexanderZero
 * @since 2021-07-14
 */
@RestController
@RequestMapping("/role-pojo")
public class RoleController {


    @Autowired
    private RoleService roleService;

    @GetMapping(value = "/")
    public ResponseEntity<Page<RolePojo>> list(@RequestParam(required = false) Integer current, @RequestParam(required = false) Integer pageSize) {
        if (current == null) {
            current = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        Page<RolePojo> aPage = roleService.page(new Page<>(current, pageSize));
        return new ResponseEntity<>(aPage, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<RolePojo> getById(@PathVariable("id") String id) {
        return new ResponseEntity<>(roleService.getById(id), HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Object> create(@RequestBody RolePojo params) {
        roleService.save(params);
        return new ResponseEntity<>("created successfully", HttpStatus.OK);
    }

    @PostMapping(value = "/delete/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") String id) {
        roleService.removeById(id);
        return new ResponseEntity<>("deleted successfully", HttpStatus.OK);
    }

    @PostMapping(value = "/update")
    public ResponseEntity<Object> delete(@RequestBody RolePojo params) {
        roleService.updateById(params);
        return new ResponseEntity<>("updated successfully", HttpStatus.OK);
    }
}
