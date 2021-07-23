package com.example.mybatisplus.service.impl;

import com.example.mybatisplus.entity.MenuPojo;
import com.example.mybatisplus.mapper.MenuMapper;
import com.example.mybatisplus.service.MenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author AlexanderZero
 * @since 2021-07-14
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, MenuPojo> implements MenuService {

}
