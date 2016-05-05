package dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.dom4j.io.SAXReader;



public interface SaveDAO {
	public void CreateDB(String ct);
	public void ConnectToDB(String xml);
	public void DisconnectToDB();
	public void SavePage1(Map<String,String> hs,String city);
	public List<String> FindhidFromPage1(String city);
	public void SavePage2(Map<String,String> comt,String city);
	public List<String> FinduidFromPage2(String city);
	public void SavePage3(Map<String,String> fangke,String city);
	public void SavePage3_trace(Map<String,String> fangke,String city);
	public List<String> FindfdidFromPage1(String city);
	public void SavePage4_FANGDONG(Map<String,String> fangdong,String city);
	public void SavePage4_ORDER(Map<String,String> order,String city);
	
}
