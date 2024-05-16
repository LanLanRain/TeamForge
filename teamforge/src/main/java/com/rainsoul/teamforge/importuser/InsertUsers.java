package com.rainsoul.teamforge.importuser;

import com.rainsoul.teamforge.mapper.UserMapper;
import com.rainsoul.teamforge.model.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
public class InsertUsers {

    @Autowired
    private UserMapper userMapper;

    @Scheduled(cron = "0 0 0 * * *")
    public void insertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
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
        }
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    public static void main(String[] args) {
        InsertUsers insertUsers = new InsertUsers();
        insertUsers.insertUsers();
    }
}
