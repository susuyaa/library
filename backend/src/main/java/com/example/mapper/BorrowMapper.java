package com.example.mapper;

import com.example.entity.Borrow;
import com.example.entity.BorrowBookInfo;
import com.example.entity.HotBorrowBook;
import org.apache.ibatis.annotations.*;

import java.sql.Timestamp;
import java.util.List;

/**
 * @ClassName BorrowMapper
 * @Description
 * @Author syp10
 * @Data 2023/5/7 22:02
 */
public interface BorrowMapper {

    @Results({
            @Result(column = "book_id", property = "title",
                    one = @One(select = "findTitleByBid")),
            @Result(column = "account_id", property = "username",
                    one = @One(select = "findUsernameById")),
    })
    @Select("select * from borrow")
    List<Borrow> getAllBorrow();

    @Select("select * from borrow where borrow_id = #{borrow_id}")
    Borrow getBorrowById(String borrow_id);

    @Results({
            @Result(column = "book_id", property = "title",
                    one = @One(select = "findTitleByBid")),
            @Result(column = "account_id", property = "username",
                    one = @One(select = "findUsernameById")),
    })
    @Select("select * from borrow where due_time < NOW() and del_flag = 0")
    List<Borrow> getOverdueBorrow();

    //严禁删除！
    @Select("select title from book where bid = #{book_id}")
    String findTitleByBid(int book_id);
    //严禁删除！
    @Select("select username from account where id = #{account_id}")
    String findUsernameById(int account_id);

    @Select("select account_id from borrow where borrow_id = #{borrow_id}")
    String findAccountIdByBorrowId(String borrow_id);

    @Results({
            @Result(column = "book_id", property = "title",
                    one = @One(select = "findTitleByBid")),
            @Result(column = "account_id", property = "username",
                    one = @One(select = "findUsernameById")),
    })
    @Select("select * from borrow where del_flag = 0")
    List<Borrow> getUnreturnedBorrow();

    @Results({
            @Result(column = "book_id", property = "title",
                    one = @One(select = "findTitleByBid")),
            @Result(column = "account_id", property = "username",
                    one = @One(select = "findUsernameById")),
    })
    @Select("select * from borrow, account, book " +
            "where book_id = bid and account_id = id and username like '%${text}%' and borrow.del_flag = 0")
    List<Borrow> searchBorrowingByUsername(String text);

    @Results({
            @Result(column = "book_id", property = "title",
                    one = @One(select = "findTitleByBid")),
            @Result(column = "account_id", property = "username",
                    one = @One(select = "findUsernameById")),
    })
    @Select("select * from borrow, account, book " +
            "where book_id = bid and account_id = id and title like '%${text}%' and borrow.del_flag = 0")
    List<Borrow> searchBorrowingByTitle(String text);

    @Update("update borrow set due_time = DATE_ADD(due_time,INTERVAL 3 DAY) where borrow_id in (${ids})")
    int batchExtendBorrowByIds(String ids);

    @Update("update borrow set del_flag = 1, actual_time = NOW() where borrow_id in (${ids})")
    int batchReturnBorrowByIds(String ids);

    @Update("""
            update account, borrow
            set borrow.del_flag = 1, borrow.actual_time = NOW(), account.borrowing_nums = borrowing_nums - 1
            where borrow.account_id = account.id and borrow_id = #{borrow_id}
            """)
    void userSingleReturn(String borrow_id);

    @Update("update book set nums = nums - 1 where bid = #{bid} and nums > 0")
    int decreaseBookNumsByBid(String bid);

    @Insert("insert into borrow (book_id, account_id, borrow_time, due_time) values (#{book_id}, #{account_id}, #{borrow_time}, #{due_time})")
    int insertBorrow(String book_id, String account_id, Timestamp borrow_time, Timestamp due_time);

    @Select("select count(*) from borrow where book_id = #{book_id} and account_id = #{account_id} and del_flag = 0")
    int countBorrowingByBidAndAccountId(String book_id, String account_id);

    @Select("""
            select book_id, title, author, nums, cover_url, count(*) as borrow_count
            from borrow, book
            where book_id = bid
            group by book_id
            order by borrow_count DESC
            LIMIT 5
            """)
    List<HotBorrowBook> selectHotBorrowBookTop5();

    @Select("""
            select book_id, title, author, nums, cover_url, count(*) as borrow_count
            from borrow, book
            where book_id = bid
            group by book_id
            order by borrow_count DESC
            LIMIT 10
            """)
    List<HotBorrowBook> selectHotBorrowBookTop10();

    @Select("""
            select book_id, title, author, nums, cover_url, count(*) as borrow_count
            from borrow, book
            where borrow_time >= NOW() - INTERVAL 1 ${time_range} and book_id = bid
            group by book_id
            order by borrow_count DESC
            LIMIT 10
            """)
    List<HotBorrowBook> selectHotBorrowBookTop10WithTime(String time_range);


    @Select("""
            select book_id, title, author, nums, cover_url, count(*) as borrow_count
            from borrow, book
            where book_id = bid and type_id = #{type_id}
            group by book_id
            order by borrow_count DESC
            LIMIT 10
            """)
    List<HotBorrowBook> selectHotBorrowBookTop10WithType(String type_id);


    @Select("""
            select book_id, title, author, nums, cover_url, count(*) as borrow_count
            from borrow, book
            where borrow_time >= NOW() - INTERVAL 1 ${time_range} and book_id = bid and type_id = #{type_id}
            group by book_id
            order by borrow_count DESC
            LIMIT 10
            """)
    List<HotBorrowBook> selectHotBorrowBookTop10WithTimeAndType(String type_id, String time_range);



    @Select("""
            select borrow_id, borrow.book_id, book.title ,book.author, borrow.borrow_time, borrow.due_time, borrow.actual_time\s
            from book, borrow\s
            where book.bid = borrow.book_id and borrow.account_id = #{account_id} and borrow.del_flag = 0""")
    List<BorrowBookInfo> selectUserBorrowingBook(String account_id);

    @Select("""
            select borrow_id, borrow.book_id, book.title ,book.author, borrow.borrow_time, borrow.due_time, borrow.actual_time\s
            from book, borrow\s
            where book.bid = borrow.book_id and borrow.account_id = #{account_id} and borrow.del_flag = 1
            """)
    List<BorrowBookInfo> selectUserBorrowedBook(String account_id);

    @Select("""
            select borrow_id, borrow.book_id, book.title ,book.author, borrow.borrow_time, borrow.due_time, borrow.actual_time\s
            from book, borrow\s
            where book.bid = borrow.book_id and borrow.account_id = #{account_id} and borrow.del_flag = 1 and book.title like '%${searchValue}%'
            """)
    List<BorrowBookInfo> searchBorrowedByTitleWithAccount(String searchValue, String account_id);

    @Select("""
            select borrow_id, borrow.book_id, book.title ,book.author, borrow.borrow_time, borrow.due_time, borrow.actual_time\s
            from book, borrow\s
            where book.bid = borrow.book_id and borrow.account_id = #{account_id} and borrow.del_flag = 1 and book.author like '%${searchValue}%'
            """)
    List<BorrowBookInfo> searchBorrowedByAuthorWithAccount(String searchValue, String account_id);

    @Update("update borrow set is_extended = 1, due_time = DATE_ADD(due_time,INTERVAL 3 DAY) where borrow_id = #{borrow_id} and is_extended = 0")
    int userSingleExtendBorrowing(String borrow_id);
}
