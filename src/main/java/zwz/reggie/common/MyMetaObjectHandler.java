package zwz.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

// 自定义元数据对象处理器，自动填充
// 发送一次Http请求，服务端就会新创建一个线程来处理来处理该请求，即处理请求用到的所有方法使用同一个线程
//   可以通过ThreadLocal来获取 处理该http请求的线程的唯一标识 线程id

@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]....");
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());

        metaObject.setValue("createUser",BaseContext.getCurrentId());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());


    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充[update]....");

        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());

    }
}
