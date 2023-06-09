package com.example.mapper;

import com.example.entity.Account;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @ClassName UserMapper
 * @Description user的mapper类
 * @Author su
 * @Date 2023/4/19 21:32
 */
public interface UserMapper {

    @Select("select * from account where username = #{text} or email = #{text}")
    Account findAccountByUsernameOrEmail(String text);

    @Insert("insert into account (username, password, role, email) values (#{username}, #{password}, #{role}, #{email})")
    int creatAccount(String username, String password, String role, String email);

    @Update("update account set password = #{password} where email = #{email}")
    int resetPasswordByEmail(String password, String email);

    @Select("select password from account where id = #{account_id}")
    String getPasswordByAccountId(String account_id);

    @Update("update account set password = #{new_password} where id = #{account_id}")
    int setNewPasswordByAccountId(String account_id, String new_password);

    @Select("select * from account where role <> 'admin'")
    List<Account> getAllAccountUser();

    @Update("update account set credit = 3 where id = #{id}")
    int resetCreditById(String id);

    @Select("select count(*) from account where role <> 'admin'")
    int getUserCount();

    @Select("select * from account where username like '%${text}%' and del_flag = 0")
    List<Account> searchAccountByUsername(String text);

    @Select("select credit from account where id = #{id}")
    int getUserCreditById(String id);

    @Select("select role from account where id = #{id}")
    String getUserRoleById(String id);

    @Select("select borrowing_nums from account where id = #{id}")
    int getUserBorrowingNumsById(String id);

    @Update("update account set borrowing_nums = borrowing_nums + 1 where id = #{id}")
    int increaseBorrowingNumsById(String id);

    @Update("update account set borrowing_nums = borrowing_nums - 1 where id = #{id} and borrowing_nums > 0")
    void decreaseBorrowingNumsById(String id);

    @Update("update account set credit = credit - 1 where id = #{id} and credit > 0")
    void decreaseCreditById(String id);

    @Update("update account set role = #{new_role} where id = #{account_id}")
    int setNewRoleById(String account_id, String new_role);
}
