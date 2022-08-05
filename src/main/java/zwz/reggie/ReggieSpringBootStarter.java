package zwz.reggie;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@ServletComponentScan
@Slf4j
@SpringBootApplication
@EnableTransactionManagement
@EnableCaching
public class ReggieSpringBootStarter {
    public static void main(String[] args) {
        SpringApplication.run(ReggieSpringBootStarter.class, args);
        log.info("项目启动成功！");
    }
}
