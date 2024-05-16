package com.rainsoul.teamforge.job;

import com.rainsoul.teamforge.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class PreCacheJob {
    @Resource
    private UserService userService;


    //推荐用户
    private List<Long> recommendUserList = Arrays.asList(1L);

}
