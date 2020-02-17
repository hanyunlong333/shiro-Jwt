package com.hyl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyl.mapper.UserMapper;
import com.hyl.pojo.User;
import com.hyl.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceIpl extends ServiceImpl<UserMapper, User> implements UserService {

}
