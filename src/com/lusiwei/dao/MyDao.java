package com.lusiwei.dao;

import com.lusiwei.exception.MyException;
import com.lusiwei.utils.JDBCUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MyDao<T> {
    private Statement statement;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private ResultSetMetaData metaData;
    private Connection connection;

    /**
     * 增删改
     *
     * @param sql
     * @param objects
     * @return
     */
    public int update(String sql, Object... objects) {
        connection = JDBCUtil.getConnection();
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < objects.length; i++) {
                preparedStatement.setObject(i + 1, objects[i]);
            }
            int i = preparedStatement.executeUpdate();
            connection.commit();
            return i;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            return 0;
        } finally {
            JDBCUtil.close(null, null, preparedStatement);
        }
    }

    /**
     * @param cls
     * @param sql
     * @param objects
     * @return
     */

    //查询单个记录并设置到对象里面
    public T querySingle(Class<T> cls, String sql, Object... objects) {
        T t = null;
        boolean flag = false;
        try {
            setPreparedStatement(sql, objects);
            metaData = preparedStatement.getMetaData();
            while (resultSet.next()) {
                if (flag) {
                    throw new MyException("期望一条数据,查询到多条数据");
                }
                t = setValue(metaData, cls);
                flag = true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            JDBCUtil.close(null, resultSet, preparedStatement);
        }
        return t;
    }

    /**
     * @param cls
     * @param sql
     * @param objects
     * @return
     */
    //把查询到的结果对象封装到对象中再添加到List
    public List<T> queryForList(Class<T> cls, String sql, Object... objects) {
        List<T> list = new ArrayList<>();
        T t = null;
        try {
            setPreparedStatement(sql, objects);
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                t = setValue(metaData, cls);
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.close(null, resultSet, preparedStatement);
        }
        return list;
    }

    //预编译sql语句
    private void setPreparedStatement(String sql, Object... objects) throws SQLException {
        connection = JDBCUtil.getConnection();
        preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < objects.length; i++) {
            preparedStatement.setObject(i + 1, objects[i]);
        }
        resultSet = preparedStatement.executeQuery();
    }

    public long queryCount(String sql, Object... objects) {
        try {
            setPreparedStatement(sql, objects);
            while (resultSet.next()) {
                return resultSet.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.close(null, resultSet, preparedStatement);
        }
        return 0;
    }

    //把查询到的结果设置到对象里面
    private T setValue(ResultSetMetaData resultSetMetaData, Class cls) {
        int columnCount = 0;
        Method method;
        String columnTypeName;
        String columnName;
        String fieldName;
        String methodName;
        T t = null;
        try {
            t = (T) cls.getConstructor().newInstance();
            columnCount = resultSetMetaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                columnTypeName = resultSetMetaData.getColumnTypeName(i);
                columnName = resultSetMetaData.getColumnName(i);
                fieldName = columnName.substring(columnName.indexOf("_") + 1);
                methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                if (columnTypeName.equals("INT")) {
                    method = cls.getMethod(methodName, Integer.class);
                    method.invoke(t, resultSet.getInt(i));
                } else if (columnTypeName.equals("VARCHAR")) {
                    method = cls.getMethod(methodName, String.class);
                    method.invoke(t, resultSet.getString(i));
                } else if (columnTypeName.equals("DOUBLE")) {
                    method = cls.getMethod(methodName, Double.class);
                    method.invoke(t, resultSet.getDouble(i));
                } else if (columnTypeName.equals("DATE") || columnTypeName.equals("TIMESTAMP")) {
                    method = cls.getMethod(methodName, java.util.Date.class);
                    java.util.Date date = new Date(resultSet.getDate(i).getTime());
                    method.invoke(t, date);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return t;
    }


}
