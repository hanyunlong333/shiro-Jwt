package com.hyl.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hyl.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
