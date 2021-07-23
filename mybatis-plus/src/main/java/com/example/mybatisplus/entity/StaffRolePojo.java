package com.example.mybatisplus.entity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * <p>
 * 
 * </p>
 *
 * @author AlexanderZero
 * @since 2021-07-14
 */
@TableName("r_staff_role")
@ApiModel(value="StaffRolePojo对象", description="")
public class StaffRolePojo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户ID")
    @TableField("staff_id")
    private Long staffId;

    @ApiModelProperty(value = "角色ID")
    @TableField("role_id")
    private Long roleId;

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return "StaffRolePojo{" +
        ", staffId=" + staffId +
        ", roleId=" + roleId +
        "}";
    }
}
