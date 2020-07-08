package com.j.openproject.service;

import java.util.List;

import com.j.openproject.entity.User;

/**
 * @author Joyuce
 * @Type UserService
 * @Desc
 * @date 2019年11月22日
 * @Version V1.0
 */
public interface UserService {

    public List<User> getList();

    public User getTheList();
}
