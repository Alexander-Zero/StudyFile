package com.example.mybatisplus.dto;
import java.util.Date;
/**
 * mapper.StaffRoleMapper.findByUsername的查询结果集，该代码由mybatis-plus-generator-ui自动生成
 *
 * @author AlexanderZero
 * @since 2021-07-14
 */
public class FindByUsernameResultDto {

    private Long id;

    private Date createTime;

    private Long createStaff;

    private Date updateTime;

    private Date updateStaff;

    private Integer isDel;

    private String username;

    private String nickname;

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
        return "FindByUsernameResultDto{" +
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
