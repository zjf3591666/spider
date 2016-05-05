package dao;

import java.sql.Connection;
import java.sql.DriverManager;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class PreDbUtil {
     
	 private static SAXReader reader = new SAXReader();
	 public static String XML = "database.xml";
	 @SuppressWarnings("unchecked")
	 
	 //读取数据库配置文件database.xml，连接数据库
	 public static Connection getConn(){
		 Connection con=null;
	  try{
	   Document doc = reader.read(PreDbUtil.class.getResourceAsStream(XML));
	   Element e = doc.getRootElement();
	   // Element e = root.element("database");
	    if (e != null){
	     String url = e.elementTextTrim("url");
	     String driver =  e.elementTextTrim("driver");
	     String user =  e.elementTextTrim("user");
	     String password =  e.elementTextTrim("password");
	     Class.forName(driver);
	     con=DriverManager.getConnection(url, user, password);
	    } 
	    return con;
	  
	  }
	  catch (Exception e)
	  {
	   e.printStackTrace();
	  }
	  return null;
	 }
}
