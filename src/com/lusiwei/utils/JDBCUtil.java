package com.lusiwei.utils;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.lusiwei.exception.MyException;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class JDBCUtil {
    private static DataSource ds;

    static {
        Properties properties = new Properties();
        InputStream rs = JDBCUtil.class.getClassLoader().getResourceAsStream("druid.properties");
        try {
            properties.load(rs);
            ds= DruidDataSourceFactory.createDataSource(properties);
        } catch (IOException e) {
            throw new MyException("加载配置文件失败");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DataSource getDs() {
        return ds;
    }

    public static Connection getConnection() {
        if (ds != null) {
            try {
                return ds.getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void close(Connection connection, ResultSet resultSet, Statement statement) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
