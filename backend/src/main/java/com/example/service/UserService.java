package com.example.service;

import com.example.entity.Account;

import java.util.List;

/**
 * @ClassName UserService
 * @Description
 * @Author syp10
 * @Data 2023/5/13 8:37
 */
public interface UserService {

    List<Account> getAllAccountUser();

    int resetCredit(String account_id);

    int getUserCount();

    List<Account> searchAccountByUsername(String text);

    String resetPassword(String account_id, String current_password, String new_password);

    String changeUserRole(String account_id, String new_role);
}
