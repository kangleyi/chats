package server;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class DBUnitHelper {

    private static String Driver;
    private static String Url;
    private static String username;
    private static String password;
    private static DBUnitHelper factory = new DBUnitHelper();

    static{
        Driver = "com.mysql.jdbc.Driver";
        Url = "jdbc:mysql://localhost:3306/chart?characterEncoding=utf8";
        username = "root";
        password = "";
    }
    /**
     * 获取数据库链接
     * @return
     */
    public static Connection getConn(){
        Connection conn=null;
        try{
            conn=DriverManager.getConnection(Url,username,password);
        }catch(SQLException e){
            e.printStackTrace();
        }
        return conn;
    }

    public static Integer executeUpdate(String sql,Object ...objects){
        Connection conn = getConn();
        QueryRunner qr = new QueryRunner();
        Integer rtn = 0;
        try {
            if(objects == null){
                rtn = qr.update(conn, sql);
            }else{
                rtn = qr.update(conn, sql, objects);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                DbUtils.close(conn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rtn;
    }

    public static Integer executeUpdate(String sql){
        return executeUpdate(sql, null);
    }

    public static <T> List<T> executeQuery(String sql,Class<T> cls,Object ...objects){

        Connection conn = getConn();
        List<T> list = null;
        try{
            QueryRunner rq = new QueryRunner();
            if(objects == null){
                list = rq.query(conn, sql,new BeanListHandler<T>(cls));
            }else{
                list = rq.query(conn, sql,new BeanListHandler<T>(cls),objects);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try {
                DbUtils.close(conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return list;
    }

    public static <T> List<T> executeQuery(String sql,Class<T> cls){
        return executeQuery(sql,cls,null);
    }


}
