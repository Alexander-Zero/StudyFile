package com.example.mybatisplus.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2021/7/14
 */
@Component
public class CustomMetaObjectHandler implements MetaObjectHandler {
    private AtomicLong atomicLong = new AtomicLong(1L);
    @Override
    public void insertFill(MetaObject metaObject) {
        Date date = new Date();
        this.strictInsertFill(metaObject, "id", Long.class, atomicLong.incrementAndGet());
        this.strictInsertFill(metaObject, "createTime", Date.class, date);
        this.strictInsertFill(metaObject, "createStaff", Long.class, atomicLong.incrementAndGet());
        this.strictInsertFill(metaObject, "updateTime", Date.class, date);
        this.strictInsertFill(metaObject, "updateStaff", Long.class, atomicLong.incrementAndGet());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Date date = new Date();
        this.strictInsertFill(metaObject, "updateTime", Date.class, date);
        this.strictInsertFill(metaObject, "updateStaff", Long.class, atomicLong.incrementAndGet());
    }
}
