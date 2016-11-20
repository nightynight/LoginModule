package dao;

import com.brokepal.dao.UserDao;
import com.brokepal.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Administrator on 2016/9/26.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:spring/spring-dao.xml")
public class UserDaoTest {
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private UserDao dao;

    @Test
    public void getUser(){
        System.out.println(dao.getUserByUsername("aa"));
    }

    @Test
    public void createUser(){
        User user = new User("老宋","laosong","asdfasd5vas1dfa","qq@qq.com","123456");
        user.setValidateCode("adfa");
        user.setSalt("sdfgsd");
        dao.createUser(user);
        System.out.println(dao.getUserByUsername("laosong"));
    }

    @Test
    public void updateUser(){
        User user = new User("laosong","laosong","asdfasd5vas1dfa","qq@qq.com","123456");
        user.setValidateCode("adfdda");
        dao.updateUser(user);
        System.out.println(dao.getUserByUsername("laosong"));
    }

    @Test
    public void updateStatus(){
        dao.updateStatus(1,"nightynight_cc@163.com");
    }

    @Test
    public void updateSendActivateCodeTime(){
        dao.updateActivateCodeAndTime("adfs",new Timestamp(new Date().getTime()),"qgg");
    }

    @Test
    public void updateValidateCodeAndTime(){
        dao.updateValidateCodeAndTime("sdfsdf",new Timestamp(new Date().getTime()),"nightynight_cc@163.com");
    }
}
