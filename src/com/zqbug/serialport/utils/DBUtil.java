package com.zqbug.serialport.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.net.InetAddress;
import java.net.NetworkInterface;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/*
 * JDBC处理
 * 
 */
public class DBUtil
{

	String drivename="com.mysql.jdbc.Driver";

    String url="jdbc:mysql://localhost:3306/TEST";

    String user="root";

    String password="";

    Connection conn;

    public Connection ConnectMysql(){
        try{
        	/*
        	 * 读取config目录下面的配置文件
        	 * jdbc.properties
        	 */
        	InputStream jdbc = new BufferedInputStream(new FileInputStream("config/jdbc.properties"));
        	Properties p = new Properties();
        	p.load(jdbc);
        	drivename = p.getProperty("jdbc.driver");
        	url = p.getProperty("jdbc.url");
        	user = p.getProperty("jdbc.usename");
        	password = p.getProperty("jdbc.password");
            Class.forName(drivename);// 加载驱动，必须导入包mysql-connector-java-bin.jar
            conn = (Connection) DriverManager.getConnection(url, user, password);
            if (!conn.isClosed()) {
                System.out.println("Succeeded connecting to the Database!");
            } else {
                System.out.println("Falled connecting to the Database!");
            }
        }
        // 捕获加载驱动程序异常
        catch (ClassNotFoundException cnfex) {
            System.err.println("装载 JDBC/ODBC 驱动程序失败。");
            cnfex.printStackTrace();
        }
        // 捕获连接数据库异常
        catch (SQLException sqlex) {
            System.err.println("无法连接数据库");
            sqlex.printStackTrace();
        } 
        // 捕获获取配置文件库异常
        catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
        	System.err.println("获取配置文件失败.");
			e.printStackTrace();
		} 
        catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return conn;
    }

    public void CutConnection() throws SQLException{
       try{
           if(conn!=null);
       }catch(Exception e){
           e.printStackTrace();
       }finally{
           conn.close();
       }
    }

    //插入
    public boolean InsertSql(HashMap<String, String> hmcondition){
        try{
        	//当前时间
        	Date day=new Date();    
        	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        	String insql="insert into hbs_lock_equipment_infor(uid,sn,mac,barcode,type,createtime,device) values(?,?,?,?,?,?,?)";
            PreparedStatement ps=conn.prepareStatement(insql);
            ps.setString(1, hmcondition.get("uid"));
            ps.setString(2, hmcondition.get("sn"));
            ps.setString(3, hmcondition.get("mac"));
            ps.setString(4, hmcondition.get("barcode"));
            ps.setString(5, "1");
            ps.setString(6, df.format(day));
            ps.setString(7, getSystem());
            int result=ps.executeUpdate();
            if(result>0)return true;
         }catch(Exception e){
            e.printStackTrace();
         }
         return false;
     }

    public boolean UpdateSql(String upsql){
        try {
            PreparedStatement ps = conn.prepareStatement(upsql);
            int result=ps.executeUpdate();//返回行数或者0
            if(result>0)return true;
        } catch (SQLException ex) {
        }
        return false;
    }
    
    //与其他操作相比较，查询语句在查询后需要一个查询结果集（ResultSet）来保存查询结果
    public boolean SelectSql(String sql){
         try{
             Statement statement=conn.createStatement();
             ResultSet rs = statement.executeQuery(sql);
             while(rs.next()){
            	 System.out.println("barcode="+rs.getString("barcode"));
            	 return false;
             }
             rs.close();
             return true;
          }catch(Exception e){
             e.printStackTrace();
             return false;
          }
    }
    
  //与其他操作相比较，查询语句在查询后需要一个查询结果集（ResultSet）来保存查询结果
    public List<String> SelectSqlAll(String sql){
         try{
             Statement statement=conn.createStatement();
             ResultSet rs = statement.executeQuery(sql);
             List<String> list = new ArrayList();
             while(rs.next()){
            	 list.add(rs.getString("content"));
             }
             rs.close();
             return list;
          }catch(Exception e){
             e.printStackTrace();
             return null;
          }
    }
    
    //操作记录
    public boolean InsertSqlLog(HashMap<String, String> hmcondition){
        try{
        	//当前时间
        	Date day=new Date();    
        	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        	String insql="insert into hbs_lock_frock_log(createtime,result,serialport,barcode,device) values(?,?,?,?,?)";
            PreparedStatement ps=conn.prepareStatement(insql);
            ps.setString(1, df.format(day));
            ps.setString(2, hmcondition.get("typeString"));
            ps.setString(3, hmcondition.get("sqdata"));
            ps.setString(4, hmcondition.get("barcode"));
            ps.setString(5, getSystem());
            int result=ps.executeUpdate();
            if(result>0)return true;
         }catch(Exception e){
            e.printStackTrace();
         }
         return false;
     }
    
    
    
    
    //获取当前机器系统信息
    public static String getSystem(){
    	 try{
    	    InetAddress addr = InetAddress.getLocalHost();
    	    String ip = addr.getHostAddress().toString(); //获取本机ip
    	    String hostName = addr.getHostName().toString(); //获取本机计算机名称
    	    NetworkInterface ni = NetworkInterface.getByInetAddress(addr);
    	    byte[] mac = ni.getHardwareAddress();
    	    String sMAC = "";
    	    Formatter formatter = new Formatter();
    	    for (int i = 0; i < mac.length; i++) {
    	            sMAC = formatter.format(Locale.getDefault(), "%02X%s", mac[i],(i < mac.length - 1) ?"-" : "").toString();
    	    }
    		return "计算机名称:"+hostName+" MAC:"+sMAC+" IP:"+ip;
    	 }catch(Exception e){
             e.printStackTrace();
         }
    	 return "";
    }
}