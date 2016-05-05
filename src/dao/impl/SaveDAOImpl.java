package dao.impl;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.io.SAXReader;

import dao.DbUtil;
import dao.PreDbUtil;
import dao.SaveDAO;


public class SaveDAOImpl implements SaveDAO {
	private Connection Con;
	public void CreateDB(String ct){
		try {
			Con = PreDbUtil.getConn();
			PreparedStatement pstmt = null ; 
			String sql = "CREATE DATABASE IF NOT EXISTS xz_"+ct+" DEFAULT CHARSET utf8 COLLATE utf8_unicode_ci";
			pstmt = Con.prepareStatement(sql);
			pstmt.executeUpdate();
			String userdir = System.getProperty("user.dir");
			String xml_path = userdir+"\\src\\dao\\database_"+ct+".xml";
			File f = new File (xml_path) ;
			if (!f.exists()){
				f.createNewFile();
				FileWriter fw = new FileWriter(xml_path);
				String xml_cn = new xml_template().XML_CONTENT;
				xml_cn=xml_cn.replace("test", "xz_"+ct);
				fw.write(xml_cn);
				fw.flush();
				fw.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void ConnectToDB(String xml){
		try {
			Con = DbUtil.getConn(xml,new SAXReader()) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void DisconnectToDB(){
		try {
			Con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void SavePage1(Map<String,String> hs,String city){
		PreparedStatement pstmt = null ; 
		String sql = "CREATE TABLE IF NOT EXISTS Page1_HOUSE(hid VARCHAR(20),location VARCHAR(20),price VARCHAR(10),link VARCHAR(60),type VARCHAR(20),comNum VARCHAR(10),houseName VARCHAR(60),fdid VARCHAR(20),fdlink VARCHAR(60),area VARCHAR(10),rooms VARCHAR(60),guestsnum VARCHAR(10),living_condition VARCHAR(60),beds VARCHAR(5),CITY VARCHAR(10)) ";
		try{
			pstmt = Con.prepareStatement(sql);
			pstmt.execute();
			sql = "insert into Page1_HOUSE values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			pstmt = Con.prepareStatement(sql);
			pstmt.setString(1, hs.get("hid"));
			pstmt.setString(2, hs.get("location" ));
			pstmt.setString(3, hs.get("price" ));
			pstmt.setString(4, hs.get("link"));
			pstmt.setString(5, hs.get("type"));
			pstmt.setString(6, hs.get("comNum"));
			pstmt.setString(7, hs.get("houseName"));
			pstmt.setString(8, hs.get("fdid"));
			pstmt.setString(9, hs.get("fdlink"));
			pstmt.setString(10, hs.get("area"));
			pstmt.setString(11, hs.get("rooms"));
			pstmt.setString(12, hs.get("guestsnum"));
			pstmt.setString(13, hs.get("living_condition"));
			pstmt.setString(14, hs.get("beds"));
			pstmt.setString(15, city);
			pstmt.execute();
			pstmt.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	public List<String> FindhidFromPage1(String city){
		List<String> hidlist = new ArrayList<String>();
		String sql = "SELECT distinct hid FROM Page1_HOUSE WHERE CITY=?";
		PreparedStatement pstmt = null ;
		ResultSet rs=null;
		try{
			pstmt = Con.prepareStatement(sql); 
			pstmt.setString(1, city);
			rs = pstmt.executeQuery();
			while (rs.next()){
				hidlist.add(rs.getString(1));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return hidlist;
	}
	
	public void SavePage2(Map<String,String> comt,String city){
		PreparedStatement pstmt = null ; 
		String sql = "CREATE TABLE IF NOT EXISTS Page2_COMMENT(hid VARCHAR(20),did VARCHAR(20),uid VARCHAR(20),uname VARCHAR(20),ulink VARCHAR(60),checkindate VARCHAR(20),content text,reply text,CITY VARCHAR(10)) ";
		try{
			pstmt = Con.prepareStatement(sql);
			pstmt.execute();
			sql = "insert into Page2_COMMENT values(?,?,?,?,?,?,?,?,?)";
			pstmt = Con.prepareStatement(sql);
			pstmt.setString(1, comt.get("hid"));
			pstmt.setString(2, comt.get("did" ));
			pstmt.setString(3, comt.get("uid" ));
			pstmt.setString(4, comt.get("uname"));
			pstmt.setString(5, comt.get("ulink"));
			pstmt.setString(6, comt.get("checkindate"));
			pstmt.setString(7, comt.get("content"));
			pstmt.setString(8, comt.get("reply"));
			pstmt.setString(9, city);
			pstmt.execute();
			pstmt.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	public List<String> FinduidFromPage2(String city){
		List<String> uidlist = new ArrayList<String>();
		String sql = "SELECT distinct uid FROM Page2_COMMENT WHERE CITY=?";
		PreparedStatement pstmt = null ;
		ResultSet rs=null;
		try{
			pstmt = Con.prepareStatement(sql); 
			pstmt.setString(1, city);
			rs = pstmt.executeQuery();
			while (rs.next()){
				uidlist.add(rs.getString(1));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return uidlist;
	}
	public void SavePage3(Map<String,String> fangke,String city){
		PreparedStatement pstmt = null ; 
		String sql = "CREATE TABLE IF NOT EXISTS Page3_FANGKE(uid VARCHAR(20),uname VARCHAR(30),registertime VARCHAR(20),age VARCHAR(10),star VARCHAR(10),sx VARCHAR(10),livingplace VARCHAR(30),hometown VARCHAR(30),education VARCHAR(30),job VARCHAR(30),sjyz VARCHAR(5),yxyz VARCHAR(5),socialyz VARCHAR(5),CITY VARCHAR(10)) ";
		try{
			pstmt = Con.prepareStatement(sql);
			pstmt.execute();
			sql = "insert into Page3_FANGKE values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			pstmt = Con.prepareStatement(sql);
			pstmt.setString(1, fangke.get("uid"));
			pstmt.setString(2, fangke.get("uname" ));
			pstmt.setString(3, fangke.get("registertime" ));
			pstmt.setString(4, fangke.get("age"));
			pstmt.setString(5, fangke.get("star"));
			pstmt.setString(6, fangke.get("sx"));
			pstmt.setString(7, fangke.get("livingplace"));
			pstmt.setString(8, fangke.get("hometown"));
			pstmt.setString(9, fangke.get("education"));
			pstmt.setString(10, fangke.get("job"));
			pstmt.setString(11, fangke.get("sjyz"));
			pstmt.setString(12, fangke.get("yxyz"));
			pstmt.setString(13, fangke.get("socialyz"));
			pstmt.setString(14, city);
			pstmt.execute();
			pstmt.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	public void SavePage3_trace(Map<String,String> trace,String city){
		PreparedStatement pstmt = null ; 
		String sql = "CREATE TABLE IF NOT EXISTS Page3_FANGKE_trace(uid VARCHAR(20),uname VARCHAR(30),date VARCHAR(20),action VARCHAR(10),fdid VARCHAR(10),hid VARCHAR(10),info TEXT,CITY VARCHAR(10)) ";
		try{
			pstmt = Con.prepareStatement(sql);
			pstmt.execute();
			sql = "insert into Page3_FANGKE_trace values(?,?,?,?,?,?,?,?)";
			pstmt = Con.prepareStatement(sql);
			pstmt.setString(1,trace.get("uid"));
			pstmt.setString(2,trace.get("uname"));
			pstmt.setString(3,trace.get("date"));
			pstmt.setString(4,trace.get("action"));
			pstmt.setString(5,trace.get("fdid"));
			pstmt.setString(6,trace.get("hid"));
			pstmt.setString(7,trace.get("info"));
			pstmt.setString(8, city);
			pstmt.execute();
			pstmt.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	public List<String> FindfdidFromPage1(String city){
		List<String> fdidlist = new ArrayList<String>();
		String sql = "SELECT distinct fdid FROM Page1_HOUSE WHERE CITY=?";
		PreparedStatement pstmt = null ;
		ResultSet rs=null;
		try{
			pstmt = Con.prepareStatement(sql); 
			pstmt.setString(1, city);
			rs = pstmt.executeQuery();
			while (rs.next()){
				fdidlist.add(rs.getString(1));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return fdidlist;
	}
	public void SavePage4_FANGDONG(Map<String,String> fangdong,String city){
		PreparedStatement pstmt = null ; 
		String sql = "CREATE TABLE IF NOT EXISTS Page4_FANGDONG(fdid VARCHAR(20),fdname VARCHAR(30),smrz VARCHAR(5),zstxrz VARCHAR(5),zmxy VARCHAR(5),zmxy_credits VARCHAR(10),gender VARCHAR(5),age VARCHAR(5),star VARCHAR(10),sx VARCHAR(5),livingplace VARCHAR(30),hometown VARCHAR(30),bloodtype VARCHAR(5),job VARCHAR(30),zw VARCHAR(30),education VARCHAR(30),reply_rate VARCHAR(10),comfirm_time VARCHAR(10),accept_rate VARCHAR(10),dairy_total VARCHAR(10),houses_total VARCHAR(10),comments_total VARCHAR(10),order_total VARCHAR(10),CITY VARCHAR(10)) ";
		try{
			pstmt = Con.prepareStatement(sql);
			pstmt.execute();
			sql = "insert into Page4_FANGDONG values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			pstmt = Con.prepareStatement(sql);
			pstmt.setString(1, fangdong.get("fdid"));
			pstmt.setString(2, fangdong.get("fdname" ));
			pstmt.setString(3, fangdong.get("smrz" ));
			pstmt.setString(4, fangdong.get("zstxrz"));
			pstmt.setString(5, fangdong.get("zmxy"));
			pstmt.setString(6, fangdong.get("zmxy_credits"));
			pstmt.setString(7, fangdong.get("gender"));
			pstmt.setString(8, fangdong.get("age"));
			pstmt.setString(9, fangdong.get("star"));
			pstmt.setString(10, fangdong.get("sx"));
			pstmt.setString(11, fangdong.get("livingplace"));
			pstmt.setString(12, fangdong.get("hometown"));
			pstmt.setString(13, fangdong.get("bloodtype"));
			pstmt.setString(14, fangdong.get("job"));
			pstmt.setString(15, fangdong.get("zw"));
			pstmt.setString(16, fangdong.get("education"));
			pstmt.setString(17, fangdong.get("reply_rate"));
			pstmt.setString(18, fangdong.get("comfirm_time"));
			pstmt.setString(19, fangdong.get("accept_rate"));
			pstmt.setString(20, fangdong.get("dairy_total"));
			pstmt.setString(21, fangdong.get("houses_total"));
			pstmt.setString(22, fangdong.get("comments_total"));
			pstmt.setString(23, fangdong.get("order_total"));
			pstmt.setString(24, city);
			pstmt.execute();
			pstmt.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void SavePage4_ORDER(Map<String,String> order,String city){
		PreparedStatement pstmt = null ; 
		String sql = "CREATE TABLE IF NOT EXISTS Page4_ORDER(fdid VARCHAR(20),order_no VARCHAR(10),hlink VARCHAR(60),hid VARCHAR(20),house_info VARCHAR(60),days VARCHAR(10),checkin_date VARCHAR(20),checkout_date VARCHAR(20),fkname VARCHAR(30),fkphone VARCHAR(20),CITY VARCHAR(10)) ";
		try{
			pstmt = Con.prepareStatement(sql);
			pstmt.execute();
			sql = "insert into Page4_ORDER values(?,?,?,?,?,?,?,?,?,?,?)";
			pstmt = Con.prepareStatement(sql);
			pstmt.setString(1, order.get("fdid"));
			pstmt.setString(2, order.get("order_no" ));
			pstmt.setString(3, order.get("hlink" ));
			pstmt.setString(4, order.get("hid"));
			pstmt.setString(5, order.get("house_info"));
			pstmt.setString(6, order.get("days"));
			pstmt.setString(7, order.get("checkin_date"));
			pstmt.setString(8, order.get("checkout_date"));
			pstmt.setString(9, order.get("fkname"));
			pstmt.setString(10, order.get("fkphone"));
			pstmt.setString(11, city);
			pstmt.execute();
			pstmt.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
	


