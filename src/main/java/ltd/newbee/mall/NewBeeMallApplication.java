package ltd.newbee.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan("ltd.newbee.mall.dao")
@EnableFeignClients("ltd.newbee.mall")
@SpringBootApplication
public class NewBeeMallApplication {
    public static void main(String[] args) {
        SpringApplication.run(NewBeeMallApplication.class, args);
    }
}
