package com.example.mybatisplus.controller;

import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.mybatisplus.service.MenuService;
import com.example.mybatisplus.entity.MenuPojo;
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
@RequestMapping("/menu-pojo")
public class MenuController {


    @Autowired
    private MenuService menuService;

    @GetMapping(value = "/")
    public ResponseEntity<Page<MenuPojo>> list(@RequestParam(required = false) Integer current, @RequestParam(required = false) Integer pageSize) {
        if (current == null) {
            current = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        Page<MenuPojo> aPage = menuService.page(new Page<>(current, pageSize));
        return new ResponseEntity<>(aPage, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<MenuPojo> getById(@PathVariable("id") String id) {
        return new ResponseEntity<>(menuService.getById(id), HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Object> create(@RequestBody MenuPojo params) {

        menuService.save(params);
        return new ResponseEntity<>("created successfully", HttpStatus.OK);
    }

    @PostMapping(value = "/delete/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") String id) {
        menuService.removeById(id);
        return new ResponseEntity<>("deleted successfully", HttpStatus.OK);
    }

    @PostMapping(value = "/update")
    public ResponseEntity<Object> delete(@RequestBody MenuPojo params) {
        menuService.updateById(params);
        return new ResponseEntity<>("updated successfully", HttpStatus.OK);
    }
}
