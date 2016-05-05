package dao;

import java.sql.Connection;
import java.sql.DriverManager;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class DbUtil {
     
	
	 @SuppressWarnings("unchecked")
	 
	 //��ȡ���ݿ������ļ�database.xml���������ݿ�
	 public static Connection getConn(String xml,SAXReader READER){
		 Connection con=null;
	  try{
	   Document doc = READER.read(xml);
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
