package com.rainsoul.teamforge.importuser;

import com.rainsoul.teamforge.mapper.UserMapper;
import com.rainsoul.teamforge.model.domain.User;
import com.rainsoul.teamforge.service.UserService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class InsertUsersTest {


    @Resource
    private UserMapper userMapper;

    @Resource
    private UserService userService;

    @Test
    public void updateUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            User user = new User();
            user.setUsername("username" + i);
            user.setUserAccount("userAccount" + i);
            user.setGender(0);
            user.setUserPassword("userPassword" + i);
            user.setUserStatus(0);
            user.setPhone("phone" + i);
            user.setEmail("email" + i);
            user.setStudentId("studentId" + i);
            user.setTags("[\"java\",\"python\"]");
            user.setAvatarUrl("avatarUrl" + i);
            user.setUserRole(0);
            userMapper.insert(user);
            System.out.println(user.getId());
            userList.add(user);
        }
        userService.saveBatch(userList, 100);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

}