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
@TableName("t_staff")
@ApiModel(value = "StaffPojo对象", description = "")
public class StaffPojo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键", hidden = true)
    @TableId(value = "id")
    private Long id;

    @ApiModelProperty(value = "创建时间", hidden = true)
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "创建人", hidden = true)
    @TableField(value = "create_staff", fill = FieldFill.INSERT)
    private Long createStaff;

    @ApiModelProperty(value = "更新时间", hidden = true)
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "修改人", hidden = true)
    @TableField(value = "update_staff", fill = FieldFill.INSERT_UPDATE)
    private Date updateStaff;

    @ApiModelProperty(value = "1：删除，0：未删除", hidden = true)
    @TableField("is_del")
    @TableLogic
    private Integer isDel;

    @ApiModelProperty(value = "用户名")
    @TableField("username")
    private String username;

    @ApiModelProperty(value = "昵称")
    @TableField("nickname")
    private String nickname;

    @ApiModelProperty(value = "密码")
    @TableField("secrete")
    private String secrete;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSecrete() {
        return secrete;
    }

    public void setSecrete(String secrete) {
        this.secrete = secrete;
    }

    @Override
    public String toString() {
        return "StaffPojo{" +
                ", id=" + id +
                ", createTime=" + createTime +
                ", createStaff=" + createStaff +
                ", updateTime=" + updateTime +
                ", updateStaff=" + updateStaff +
                ", isDel=" + isDel +
                ", username=" + username +
                ", nickname=" + nickname +
                ", secrete=" + secrete +
                "}";
    }
}
