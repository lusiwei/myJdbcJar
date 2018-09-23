package com.lusiwei.test;

import com.lusiwei.dao.MyDao;
import com.lusiwei.pojo.User;
import org.junit.Test;

import java.util.List;

public class TestDemo {
    MyDao myDao = new MyDao();

    @Test
    public void querySingle() {
        String sql = "select * from user_info where u_id =?";
        System.out.println(myDao.querySingle(User.class, sql, 2));
    }

    @Test
    public void queryList() {
        String sql = "select * from user_info";
        List list = myDao.queryForList(User.class, sql);
        list.forEach(x -> System.out.println(x));
    }

    @Test
    public void queryCount() {
        String sql = "select count(*) from user_info where u_name=?";
        System.out.println("查到的数据条数为:"+myDao.queryCount(sql,"max"));
    }
    @Test
    public void update(){
        String sql="insert into user_info(u_name,u_sex,u_age,u_birthplace,u_qq,u_email) values(?,?,?,?,?,?)";
        System.out.println(myDao.update(sql, "令狐冲", "男", 33, "重庆", "1214214", "2142141@qq.com"));
    }
}
