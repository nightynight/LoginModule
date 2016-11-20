package service;

import com.brokepal.entity.User;
import com.brokepal.exception.ValidateException;
import com.brokepal.service.UserService;
import com.sun.mail.smtp.SMTPAddressFailedException;
import org.dom4j.DocumentException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by Administrator on 2016/9/26.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring/spring-dao.xml","classpath:spring/spring-service.xml"})
public class UserServiceTest {
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private UserService service;

    @Test
    public void getUser(){
        System.out.println(service.getUserByUsername("gg"));
    }

    @Test
    public void createUser(){
        User user = new User("老宋","gg","gg","qgg",null);
        user.setSalt("sdfsdsdfsdf");
        service.createUser(user);
    }

    @Test
    public void updateUser(){
        service.updateUser(new User("老宋","laosong","222222","1024234001@qq.com","123456"));
    }

    @Test
    public void sendActivateEmail() throws SMTPAddressFailedException {
        service.sendActivateEmail("qgg");
    }

    @Test
    public void sendValidateEmail() throws SMTPAddressFailedException {
        service.sendValidateCodeEmial("1024234001@qq.com");
    }

    @Test
    public void activate() throws ValidateException, DocumentException {
        service.activate("qgg","6DB3A178B8353E3353147ED82CE39F01","zh");
    }
}
