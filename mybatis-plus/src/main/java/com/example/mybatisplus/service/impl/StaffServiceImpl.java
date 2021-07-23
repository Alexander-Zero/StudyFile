package com.example.mybatisplus.service.impl;

import com.example.mybatisplus.entity.StaffPojo;
import com.example.mybatisplus.mapper.StaffMapper;
import com.example.mybatisplus.service.StaffService;
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
public class StaffServiceImpl extends ServiceImpl<StaffMapper, StaffPojo> implements StaffService {

}
