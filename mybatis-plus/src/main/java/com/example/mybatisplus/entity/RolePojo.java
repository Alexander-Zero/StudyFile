package com.example.mybatisplus.entity;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableLogic;
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
@TableName("t_role")
@ApiModel(value="RolePojo对象", description="")
public class RolePojo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键",hidden=true)
    @TableId("id")
    private Long id;

    @ApiModelProperty(value = "创建时间",hidden=true)
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "创建人",hidden=true)
    @TableField("create_staff")
    private Long createStaff;

    @ApiModelProperty(value = "更新时间",hidden=true)
    @TableField("update_time")
    private Date updateTime;

    @ApiModelProperty(value = "修改人",hidden=true)
    @TableField("update_staff")
    private Date updateStaff;

    @ApiModelProperty(value = "1：删除，0：未删除",hidden=true)
    @TableField("is_del")
    @TableLogic
    private Integer isDel;

    @ApiModelProperty(value = "用户名")
    @TableField("role_name")
    private String roleName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getCreateStaff() {
        return createStaff;
    }

    public void setCreateStaff(Long createStaff) {
        this.createStaff = createStaff;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getUpdateStaff() {
        return updateStaff;
    }

    public void setUpdateStaff(Date updateStaff) {
        this.updateStaff = updateStaff;
    }

    public Integer getIsDel() {
        return isDel;
    }

    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String toString() {
        return "RolePojo{" +
        ", id=" + id +
        ", createTime=" + createTime +
        ", createStaff=" + createStaff +
        ", updateTime=" + updateTime +
        ", updateStaff=" + updateStaff +
        ", isDel=" + isDel +
        ", roleName=" + roleName +
        "}";
    }
}
