package com.example.mybatisplus.controller;

import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.mybatisplus.service.PermissionService;
import com.example.mybatisplus.entity.PermissionPojo;
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
@RequestMapping("/permission-pojo")
public class PermissionController {


    @Autowired
    private PermissionService permissionService;

    @GetMapping(value = "/")
    public ResponseEntity<Page<PermissionPojo>> list(@RequestParam(required = false) Integer current, @RequestParam(required = false) Integer pageSize) {
        if (current == null) {
            current = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        Page<PermissionPojo> aPage = permissionService.page(new Page<>(current, pageSize));
        return new ResponseEntity<>(aPage, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<PermissionPojo> getById(@PathVariable("id") String id) {
        return new ResponseEntity<>(permissionService.getById(id), HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Object> create(@RequestBody PermissionPojo params) {
        permissionService.save(params);
        return new ResponseEntity<>("created successfully", HttpStatus.OK);
    }

    @PostMapping(value = "/delete/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") String id) {
        permissionService.removeById(id);
        return new ResponseEntity<>("deleted successfully", HttpStatus.OK);
    }

    @PostMapping(value = "/update")
    public ResponseEntity<Object> delete(@RequestBody PermissionPojo params) {
        permissionService.updateById(params);
        return new ResponseEntity<>("updated successfully", HttpStatus.OK);
    }
}
