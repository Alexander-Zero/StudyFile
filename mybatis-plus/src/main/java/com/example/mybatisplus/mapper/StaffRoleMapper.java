package com.example.mybatisplus.mapper;

import com.example.mybatisplus.entity.StaffRolePojo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.mybatisplus.dto.FindByUsernameResultDto;
import com.example.mybatisplus.dto.FindByUsernameParamDto;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author AlexanderZero
 * @since 2021-07-14
 */
public interface StaffRoleMapper extends BaseMapper<StaffRolePojo> {

    List<FindByUsernameResultDto> findByUsername(FindByUsernameParamDto params);
}
