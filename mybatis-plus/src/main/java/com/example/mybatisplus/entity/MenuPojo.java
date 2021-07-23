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
@TableName("t_menu")
@ApiModel(value="MenuPojo对象", description="")
public class MenuPojo implements Serializable {

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

    @ApiModelProperty(value = "菜单名/按钮名")
    @TableField("menu_name")
    private String menuName;

    @ApiModelProperty(value = "是否为按钮")
    @TableField("is_button")
    private Integer isButton;

    @ApiModelProperty(value = "父id")
    @TableField("pid")
    private Long pid;

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

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public Integer getIsButton() {
        return isButton;
    }

    public void setIsButton(Integer isButton) {
        this.isButton = isButton;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    @Override
    public String toString() {
        return "MenuPojo{" +
        ", id=" + id +
        ", createTime=" + createTime +
        ", createStaff=" + createStaff +
        ", updateTime=" + updateTime +
        ", updateStaff=" + updateStaff +
        ", isDel=" + isDel +
        ", menuName=" + menuName +
        ", isButton=" + isButton +
        ", pid=" + pid +
        "}";
    }
}
