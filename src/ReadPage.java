
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.dom4j.io.SAXReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
//import java.util.Hashtable;
import java.util.List;
import java.util.regex.*;


import dao.SaveDAO;
import dao.impl.SaveDAOImpl;



public class ReadPage extends Thread {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParserException 
	 */
	private static String URL="";
	private static String Min="";
	private static String Max="";
	private static boolean isSave;
	private static String filePath;
	private String City="";
	private SaveDAO DBFunc = new SaveDAOImpl();
	
	
	public static String getFilePath() {
		return filePath;
	}
	public static void setFilePath(String filePath) {
		ReadPage.filePath = filePath;
	}
	public static String getURL() {
		return URL;
	}
	public static void setURL(String uRL) {
		URL = uRL;
	}
	public static String getMin() {
		return Min;
	}
	public static void setMin(String min) {
		Min = min;
	}
	public static String getMax() {
		return Max;
	}
	public static void setMax(String max) {
		Max = max;
	}
	public static boolean isSave() {
		return isSave;
	}
	public static void setSave(boolean isSave) {
		ReadPage.isSave = isSave;
	}
   
	public ReadPage(String city,String min,String max,boolean issave){
		City=city;
		Min=min;
		Max=max;
		isSave=issave;
   }
	public ReadPage() {
		// TODO Auto-generated constructor stub
	}
	public ReadPage(String city , SaveDAO dbFunc) {
		this.City=city;
		this.DBFunc = dbFunc;
	}
	public static String findNum(String line){
//	   Matcher m = Pattern.compile("//d+").matcher(line);
	   String num = "";
	   Matcher m = Pattern.compile("\\d+").matcher(line);
	   if (m.find()){
		   num = m.group();
//		   System.out.println(m.group());
	   }
	   return num;
   }
   
	
   
    
	public static String getWeb(String url,boolean isSave){
		
		String html = "";
		 //创建HttpClientBuilder  
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();  
        //HttpClient  
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();  
        HttpEntity entity = null;
        HttpGet httpGet = new HttpGet(url);  
        System.out.println(httpGet.getRequestLine());  
        try{
        	int i = 0;
        	while(true){
        		i++;
        		System.out.println("try connection to "+url+" for "+i+" time(s)...");
	            //执行get请求  
	            HttpResponse httpResponse = closeableHttpClient.execute(httpGet);  
	            //获取响应消息实体  
	            entity = httpResponse.getEntity();  
	            //响应状态  
	            String status = httpResponse.getStatusLine().toString();
	            System.out.println("status:" + status); 
	            if (status.contains("200")){
	            	break;
	            }
        	}   
            //判断响应实体是否为空  
            if (entity != null) { 
            	html=EntityUtils.toString(entity);
//                System.out.println("contentEncoding:" + entity.getContentEncoding());  
//                System.out.println("response content:" + html);
                
                
                
                if(isSave){
                	
                	
	                File file = new File(filePath);
	                try{
	                	if (!file.exists()){
	                		
	                		file.createNewFile();
	                	}
	                }
	                catch (IOException e){
	                	e.printStackTrace();
	                }
	                FileWriter fw =new FileWriter(filePath);
	                fw.write(html);
	                fw.close();
	            } 
            }  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        } finally {  
	            try {  
	            	//关闭流并释放资源  
	                closeableHttpClient.close();  
	            } catch (Exception e) {  
	                e.printStackTrace();  
	            }
	            
	        }
        return html;
        
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List ReadPage1(String html,boolean isReadLocal) throws IOException{
		/*
		 * example: http://bj.xiaozhu.com/165-9999yuan-duanzufang-p2-1/
		 */
		File input;
		Document doc;
		List houselist = new ArrayList();
		if (isReadLocal) {
			input = new File(filePath);
			doc = Jsoup.parse(input, "GBK");
		}else{
			doc = Jsoup.parse(html);
		}
//		Document doc = Jsoup.parse(input, "GBK");
		Elements house = doc.select("li[lodgeunitid]");
		Element nextLink = doc.select("a.font_st").last();
		String nextpage = "0";
		try{
			nextpage = nextLink.attr("href");
		}catch (Exception e){
			e.printStackTrace();
		}
//		Elements body = doc.select("ul.pic_list* >li");
		for (Element i : house){
			String type ;
			String comNum ="0";
			String houseName = "";
			Map<String,String> mp = Collections.synchronizedMap(new HashMap<String, String>());
			System.out.println(i.html());
//			String hid = i.attr("lodgeunitid");
//			hid = findNum(hid);
			String hid = "";
			Element fangdong = i.getElementsByAttributeValueMatching("href", "http://www.xiaozhu.com/fangdong/*").first();
			String fdlink = fangdong.attr("href");
			String fdid = findNum(fdlink);
			String location = i.attr("latlng");
			Element i_price = i.select("i").first();
			String price = i_price.ownText();
			String link = i.select("a").first().attr("href");
			hid = findNum(link);
			
			/*
			 * house_info: http://bj.xiaozhu.com/fangzi/1466098635.html
			 */
			String area ="";
			String rooms = "";
			String guestsnum = "";
			String living_condition = "";
			String beds = "";
			try{	
				String house_info = getWeb(link,false);
				Document doc1 = Jsoup.parse(house_info);
				Element hsinfo = doc1.select("ul.house_info").first();
				Elements tags = hsinfo.getElementsByTag("li");
				String[] info1 = tags.get(0).getElementsByTag("p").text().split(" ");
	//			BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(info1.getBytes(Charset.forName("utf8"))), Charset.forName("utf8"))); 
				for(String line : info1){
					if (line.contains("房间面积：")){
				    	area = line.replace("房间面积：", "");
				    }
					// excepted encoding
					if (line.contains("房屋面积：")){
				    	area = line.replace("房屋面积：", "");
				    }
				    if (line.contains("房屋户型：")){
				    	rooms = line.replace("房屋户型：", "");
				    }
				}
				living_condition = tags.get(1).getElementsByTag("p").text().replace(" ", "");
				guestsnum = tags.get(1).select("h6.h_ico2").first().text().replace("宜住", "").replace("人", "");
				beds = tags.get(2).select("h6.h_ico3").first().text().replace("共", "").replace("张", "");
			}catch (Exception e){
				e.printStackTrace();
			}
			
			String typeset = i.select("em.hiddenTxt").first().ownText();
			String[] temp;
			try{
				temp=typeset.replaceAll(" ", "").split("--");
				type =temp[0];
				houseName=temp[1];
			}catch(Exception e){
				try{
					temp=typeset.replaceAll(" ", "").split("-");
					type =temp[0];
					houseName=temp[1];
				}catch (Exception e1){
					temp=typeset.replaceAll(" ", "").split("-");
					type =temp[0];
				}
			}
			try{
				comNum = i.select("span.commenthref").first().text();
				comNum = findNum(comNum);
			}catch (Exception e){
				e.printStackTrace();
			}
			
			System.out.println(hid+"|"+location+"|"+price+"|"+link+"|"+type+"|"+comNum+"|"+houseName+"|"+fdid+"|"+fdlink+"|"+nextpage);
			System.out.println("---------------------------------------------------------");
			
			mp.put("hid",hid );
			mp.put("location",location );
			mp.put("price",price );
			mp.put("link",link );
			mp.put("type",type );
			mp.put("comNum",comNum );
			mp.put("houseName",houseName );
			mp.put("fdid",fdid );
			mp.put("fdlink",fdlink );
			mp.put("area",area );
			mp.put("rooms",rooms );
			mp.put("guestsnum",guestsnum );
			mp.put("living_condition",living_condition );
			mp.put("beds",beds );
			houselist.add(mp);
		
	}
		List result = new ArrayList();
		result.add(houselist);
		result.add(nextpage);
		return result;

	}
	
	public static List ReadPage2(String url,boolean isReadLocal) throws IOException{
		/*
		 * example: http://bj.xiaozhu.com/ajax.php?op=Ajax_GetDetailComment&lodgeId=525041101&cityDomain=undefined&p=4
		 */
		
		
		String hid = "";
	    Matcher m = Pattern.compile("lodgeId=\\d+").matcher(url);
	    if (m.find()){
	    	hid = m.group();
	    	hid = hid.replace("lodgeId=", "");
	    	System.out.println(hid);
	    }
		//house_info
		
		String html = getWeb(url,false);
		File input;
		Document doc;
		List comlist = new ArrayList();
		if (isReadLocal) {
			input = new File(filePath);
			doc = Jsoup.parse(input, "GBK");
		}else{
			doc = Jsoup.parse(html);
		}
		Elements comts = doc.select("div.dp_con");
		for (Element i : comts) {
			String did = "";
			String uid = "";
			String uname = "";
			String ulink = "";
			String checkindate="";
			String content = "";
			String reply = "";
			did = i.select("h6").first().attr("data-id");
			ulink = i.select("a").first().attr("href");
			Matcher mm = Pattern.compile("\\d+").matcher(ulink);
		    if (mm.find()){
		    	uid = mm.group();
		    }
			uname = i.select("span.col_pink").text();
			checkindate = i.select("i").first().text();
			checkindate = checkindate.replace("年", "-");
			checkindate = checkindate.replace("月", "");
			content = i.ownText();
			if (!i.select("div.reply_box").isEmpty()){
				Element replyele = i.select("div.reply_box").first();
				reply = replyele.select("p").first().ownText();
			}
			
			
			
			Map<String,String> comp = Collections.synchronizedMap(new HashMap<String, String>());
			comp.put("hid", hid);
			comp.put("did", did);
			comp.put("uid", uid);
			comp.put("uname", uname);
			comp.put("ulink", ulink);
			comp.put("checkindate", checkindate);
			comp.put("content", content);
			comp.put("reply", reply);
			comlist.add(comp);
		}
		return comlist;
	}
	
	
	public static Map<String ,String> ReadPage3(String url,boolean isReadLocal) throws IOException{
		/*
		 * example: http://www.xiaozhu.com/fangke/2533853763/
		 */
		String uid = "";
	    Matcher m = Pattern.compile("\\d+").matcher(url);
	    if (m.find()){
	    	uid = m.group();
	    	System.out.println(uid);
	    }
		String uname = "";
		String registertime = "";
		String age ="";
		String star = "";
		String sx = "";
		String livingplace="";
		String hometown="";
		String education="";
		String job="";
		String sjyz="0";
		String yxyz="0";
		String socialyz="0";
		String html = getWeb(url,false);
		File input;
		Document doc;
		
		if (isReadLocal) {
			input = new File(filePath);
			doc = Jsoup.parse(input, "GBK");
		}else{
			doc = Jsoup.parse(html);
		}
		Elements infos  = doc.select("ul.fk_person").select("li");
		List<String>  ilist = new ArrayList<String>();
		ilist.add("昵   称：");
		ilist.add("注册于：");
		ilist.add("年   龄：");
		ilist.add("星   座：");
		ilist.add("生   肖：");
		ilist.add("所在地：");
		ilist.add("故   乡：");
		ilist.add("学   历：");
		ilist.add("工   作：");
		for (Element info : infos ){
			String msg = info.text();
			for (String ss :ilist){
				String p = ss+".*";
				Matcher pattern = Pattern.compile(p).matcher(msg);
				boolean isFind = pattern.find();
//				if(pattern.find()){
//					System.out.println("find");
//				}
				
			    if (isFind&&ss.equals("昵   称：")){
			    	uname = pattern.group();
			    	uname = uname.replace("昵   称：","" );
			    	break;
			    }
			    if (isFind&&ss.equals("注册于：")){
			    	registertime = pattern.group();
			    	registertime = registertime.replace(ss,"" );
			    	break;
			    }
			    if (isFind&&ss.equals("年   龄：")){
			    	age = pattern.group();
			    	age = age.replace(ss,"" );
			    	break;
			    }
			    if (isFind&&ss.equals("星   座：")){
			    	star = pattern.group();
			    	star = star.replace(ss,"" );
			    	break;
			    }
			    if (isFind&&ss.equals("生   肖：")){
			    	sx = pattern.group();
			    	sx = sx.replace(ss,"" );
			    	break;
			    }
			    if (isFind&&ss.equals("所在地：")){
			    	livingplace = pattern.group();
			    	livingplace = livingplace.replace(ss,"" );
			    	break;
			    }
			    if (isFind&&ss.equals("故   乡：")){
			    	hometown = pattern.group();
			    	hometown = hometown.replace(ss,"" );
			    	break;
			    }
			    if (isFind&&ss.equals("学   历：")){
			    	education = pattern.group();
			    	education = education.replace(ss,"" );
			    	break;
			    }
			    if (isFind&&ss.equals("工   作：")){
			    	job = pattern.group();
			    	job = job.replace(ss,"" );
			    	break;
			    }
			}
		}
		try{
			String yzinfo = doc.select("ul.fk_yz_ul").text();
			if (yzinfo.contains("手机验证")){
				sjyz="1";
			}
			if (yzinfo.contains("邮箱验证")){
				yxyz="1";
			}
			if (yzinfo.contains("社交验证")){
				socialyz="1";
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		Map<String,String> fangke = Collections.synchronizedMap(new HashMap<String,String>());
		fangke.put("uid",uid );
		fangke.put("uname",uname );
		fangke.put("registertime",registertime );
		fangke.put("age",age);
		fangke.put("star",star );
		fangke.put("sx", sx);
		fangke.put("livingplace",livingplace );
		fangke.put("hometown", hometown);
		fangke.put("education", education);
		fangke.put("job", job);
		fangke.put("sjyz",sjyz );
		fangke.put("yxyz",yxyz );
		fangke.put("socialyz",socialyz );
		return fangke;
	}
	
	public static List<Map<String ,String>> ReadPage3_trace(String url,boolean isReadLocal) throws IOException{
		/*
		 * example: http://www.xiaozhu.com/fangke/2533853763/
		 * 
		 * action:	1 means check in
		 * 		 	2 means give a comment
		 *			3 means get a reply
		 *			4 means register
		 * 			
		 */
		String uid = "";
	    Matcher m = Pattern.compile("\\d+").matcher(url);
	    if (m.find()){
	    	uid = m.group();
	    	System.out.println(uid);
	    }
		String uname = "";
		

		String html = getWeb(url,false);
		File input;
		Document doc;
		
		if (isReadLocal) {
			input = new File(filePath);
			doc = Jsoup.parse(input, "GBK");
		}else{
			doc = Jsoup.parse(html);
		}
		
		uname = doc.select("h1.fk_name").first().text();
		Elements acts = doc.select("div.fk_trend_con");
		String year = "";
		List<Map<String,String>> tracelist = new ArrayList<Map<String,String>>();
		for (Element a : acts){
			String date = "";
			String action ="";
			String fdid = "";
			String hid = "";
			String info ="";
			try{
				String restore = year;
				year = a.select("div.fk_trend_year").text();
				if (year.equals("")){
					year = restore;
				}
			}catch (Exception e){
				e.printStackTrace();
			}
			try{
				date = a.select("div.fk_trend_date").text().replace(".", "-");
				date = year+"-"+date;
			}catch (Exception e){
				e.printStackTrace();
			}
			try{
				String strs = a.select("div.fk_trend_T").text();
				if (strs.contains(uname+"入住了")){
					action = "1";
				}
				else if (strs.contains(uname+"点评了")){
					action = "2";
					info = a.select("div.fk_trend_C").select("p.fk_p").text();
				}
				else if (strs.contains(uname+"被")){
					action = "3";
					info = a.select("div.fk_trend_C").select("p.fk_p").text();
				}
				else if (strs.contains(uname+"注册了小猪短租账号")){
					action = "4";
				}
				
			}catch (Exception e){
				e.printStackTrace();
			}
			
			Elements links = a.select("div.fk_trend_T").select("a");
			for (Element link : links){
				String l = link.attr("href");
				if (l.contains("fangzi")){
//					hid = l.replace("http://bj.xiaozhu.com/fangzi/", "").replace(".html", "");
					hid = findNum(l);
				}
				if (l.contains("fangdong")){
//					fdid = l.replace("http://www.xiaozhu.com/fangdong/", "").replace("/", "");
					fdid = findNum(l);
				}
			}
			Map<String,String> fangke_trace = Collections.synchronizedMap(new HashMap<String,String>());
			fangke_trace.put("uid",uid );
			fangke_trace.put("uname",uname );
			fangke_trace.put("date",date );
			fangke_trace.put("action",action );
			fangke_trace.put("fdid",fdid );
			fangke_trace.put("hid",hid );
			fangke_trace.put("info",info );

			tracelist.add(fangke_trace);
		}
		return tracelist;
	}
	public static Map<String ,String> ReadPage4(String url,boolean isReadLocal) throws IOException{
		/*
		 * example: http://www.xiaozhu.com/fangdong/439278800/
		 */
		String fdid = "";
	    Matcher m = Pattern.compile("\\d+").matcher(url);
	    if (m.find()){
	    	fdid = m.group();
	    	System.out.println(fdid);
	    }
		String fdname = "";
		String smrz = "0";
		String zstxrz ="0";
		String zmxy = "0";
		String zmxy_credits = "";
		String gender="";
		String age="";
		String star="";
		String sx="";
		String livingplace="";
		String hometown="";
		String bloodtype="";
		String job="";
		String zw = "";
		String education = "";
		String reply_rate="";
		String comfirm_time="";
		String accept_rate="";
		String dairy_total = "";
		String houses_total = "";
		String comments_total = "";
		String order_total = "";
		
		
		String html = getWeb(url,false);
		File input;
		Document doc;
		
		if (isReadLocal) {
			input = new File(filePath);
			doc = Jsoup.parse(input, "GBK");
		}else{
			doc = Jsoup.parse(html);
		}
		try{
			Elements asd = doc.select("ul.fd_navUl").first().select("span");
			dairy_total = asd.get(0).text();
			houses_total = asd.get(1).text();
			comments_total = asd.get(2).text();
			order_total = asd.get(3).text();
			Matcher aa = Pattern.compile("\\d+").matcher(dairy_total);
			if (aa.find()){
				dairy_total=aa.group();
			}
			aa = Pattern.compile("\\d+").matcher(houses_total);
			if (aa.find()){
				houses_total=aa.group();
			}
			aa = Pattern.compile("\\d+").matcher(comments_total);
			if (aa.find()){
				comments_total=aa.group();
			}
			aa = Pattern.compile("\\d+").matcher(order_total);
			if (aa.find()){
				order_total=aa.group();
			}
			fdname = doc.select("div.fd_name").first().text();
			fdname = fdname.replace(" ", "");
			try{
				Elements rz = doc.select("ul.rz_ul");
				String rz_text = rz.text();
				if (rz_text.contains("实名认证")){
					smrz="1";
				}
				if(rz_text.contains("真实头像认证")){
					zstxrz="1";
				}
				if(rz_text.contains("芝麻信用")){
					zmxy="1";
					Matcher num = Pattern.compile("\\d+").matcher(rz_text);
					if(num.find()){
						zmxy_credits=num.group();
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
			Elements infos  = doc.select("ul.fd_person").select("li");
			
			
			List<String>  ilist = new ArrayList<String>();
			ilist.add("性别：");
			ilist.add("年龄：");
			ilist.add("星座：");
			ilist.add("生肖：");
			ilist.add("所在地：");
			ilist.add("故乡：");
			ilist.add("血型：");
			ilist.add("职业：");
			ilist.add("职务：");
			ilist.add("学历：");
			for (Element info : infos ){
				String msg = info.text();
				for (String ss :ilist){
					String p = ss+".*";
					Matcher pattern = Pattern.compile(p).matcher(msg);
					boolean isFind = pattern.find();
	//				if(pattern.find()){
	//					System.out.println("find");
	//				}
					
				    if (isFind&&ss.equals("性别：")){
				    	gender = pattern.group();
				    	gender = gender.replace(ss,"" );
				    	break;
				    }
				    if (isFind&&ss.equals("年龄：")){
				    	age = pattern.group();
				    	age = age.replace(ss,"" );
				    	break;
				    }
				    if (isFind&&ss.equals("星座：")){
				    	star = pattern.group();
				    	star = star.replace(ss,"" );
				    	break;
				    }
				    if (isFind&&ss.equals("生肖：")){
				    	sx = pattern.group();
				    	sx = sx.replace(ss,"" );
				    	break;
				    }
				    if (isFind&&ss.equals("所在地：")){
				    	livingplace = pattern.group();
				    	livingplace = livingplace.replace(ss,"" );
				    	break;
				    }
				    if (isFind&&ss.equals("故乡：")){
				    	hometown = pattern.group();
				    	hometown = hometown.replace(ss,"" );
				    	break;
				    }
				    if (isFind&&ss.equals("血型：")){
				    	bloodtype = pattern.group();
				    	bloodtype = bloodtype.replace(ss,"" );
				    	break;
				    }
				    if (isFind&&ss.equals("职业：")){
				    	job = pattern.group();
				    	job = job.replace(ss,"" );
				    	break;
				    }
				    if (isFind&&ss.equals("职务：")){
				    	zw = pattern.group();
				    	zw = zw.replace(ss,"" );
				    	break;
				    }
				    if (isFind&&ss.equals("学历：")){
				    	education = pattern.group();
				    	education = education.replace(ss,"" );
				    	break;
				    }
				}
			}
			ilist.clear();
			ilist.add("在线回复率");
			ilist.add("平均确认");
			ilist.add("订单接受率");
			Elements infos_extra = doc.select("ul.infor_ul").select("li");
			for (Element info_extra : infos_extra ){
				String msg = info_extra.text();
				for (String ss :ilist){
					String p = ".*"+ss;
					Matcher pattern = Pattern.compile(p).matcher(msg);
					boolean isFind = pattern.find();
	//				if(pattern.find()){
	//					System.out.println("find");
	//				}
					
				    if (isFind&&ss.equals("在线回复率")){
				    	reply_rate = pattern.group();
				    	reply_rate = reply_rate.replace(ss,"" );
				    	break;
				    }
				    if (isFind&&ss.equals("平均确认")){
				    	comfirm_time = pattern.group();
				    	comfirm_time = comfirm_time.replace(ss,"" );
				    	break;
				    }
				    if (isFind&&ss.equals("订单接受率")){
				    	accept_rate = pattern.group();
				    	accept_rate = accept_rate.replace(ss,"" );
				    	break;
				    }
				}
			}
		}catch (Exception e){
			try{
				Element info_extra = doc.select("div.fd_infor").first();
				fdname = info_extra.getElementsByTag("h1").first().text();
				Elements sss = info_extra.select("li");
				reply_rate = sss.get(1).select("strong").text().replace("在线的回复率：", "");
				comfirm_time = sss.get(3).select("strong").text().replace("平均确认时间： ", "");
				accept_rate = sss.get(5).select("strong").text().replace("订单的接受率： ", "");
				order_total = sss.get(4).select("strong").text().replace("预订： ", "");
			}catch (Exception e1){
				e1.printStackTrace();
			}
			try{
				String genderText = doc.select("p.fd_p").first().text();
				if (genderText.contains("催他一下")){
					gender = "男";
				}else if(genderText.contains("催她一下")){
					gender = "女";
				}
			}catch(Exception e2){
				e2.printStackTrace();
			}
		}
		
			
		
		
		Map<String,String> fangdong =Collections.synchronizedMap( new HashMap<String,String>());
		fangdong.put("fdid",fdid );
		fangdong.put("fdname",fdname );
		fangdong.put("smrz",smrz );
		fangdong.put("zstxrz",zstxrz);
		fangdong.put("zmxy",zmxy );
		fangdong.put("zmxy_credits", zmxy_credits);
		fangdong.put("gender",gender );
		fangdong.put("age", age);
		fangdong.put("star", star);
		fangdong.put("sx", sx);
		fangdong.put("livingplace",livingplace );
		fangdong.put("hometown",hometown );
		fangdong.put("bloodtype",bloodtype );
		fangdong.put("job",job );
		fangdong.put("zw",zw );
		fangdong.put("education",education );
		fangdong.put("reply_rate",reply_rate );
		fangdong.put("comfirm_time",comfirm_time );
		fangdong.put("accept_rate",accept_rate );
		fangdong.put("dairy_total",dairy_total );
		fangdong.put("houses_total",houses_total );
		fangdong.put("comments_total",comments_total );
		fangdong.put("order_total",order_total );
		return fangdong;
	}
	
	public static List ReadPage4_yuding(String url,boolean isReadLocal) throws IOException{
		/*
		 * example: http://www.xiaozhu.com/fangdong/439278800/yuding.html
		 */
		String fdid = "";
	    Matcher m = Pattern.compile("\\d+").matcher(url);
	    if (m.find()){
	    	fdid = m.group();
	    	System.out.println(fdid);
	    }
		String order_no = "";
		String hlink = "";
		String hid = "";
		String house_info = "";
		String days = "";
		String checkin_date="";
		String checkout_date = "";
		String fkname = "";
		String fkphone = "";
		
		String html = getWeb(url,false);
		File input;
		Document doc;
		List result = new ArrayList();
		List<Map <String , String>> o = new ArrayList<Map <String , String>>();
		if (isReadLocal) {
			input = new File(filePath);
			doc = Jsoup.parse(input, "GBK");
		}else{
			doc = Jsoup.parse(html);
		}
		Element main_table = doc.select("div.main_con").first();
		
		if(main_table == null){
			return result;
		}
		
		Elements odlist = main_table.getElementsByTag("tr");
		for (Element od : odlist) {
			try{
				Elements temp = od.getElementsByTag("td");
				order_no=temp.get(0).text();
				hlink = temp.get(1).select("a").attr("href");
				Matcher nm = Pattern.compile("\\d+").matcher(hlink);
				if (nm.find()){
					hid = nm.group();
				}
				house_info = temp.get(1).text();
				Elements inandout = temp.get(2).getElementsByTag("span");
				days = inandout.get(0).text();
				String date = inandout.get(1).ownText();
				String[] datelist = date.split("~");
				checkin_date = datelist[0];
				checkout_date = datelist[1].replace(" ", "");
				String tempstr = temp.get(3).text();
				String[] tempstrlist = tempstr.split(" 手机：");
				fkname = tempstrlist[0].replace("房客：", "");
				fkphone = tempstrlist[1];
				Map <String ,String> ormp = Collections.synchronizedMap(new HashMap<String ,String>());
				ormp.put("fdid", fdid);
				ormp.put("order_no", order_no);
				ormp.put("hlink", hlink);
				ormp.put("hid", hid);
				ormp.put("house_info", house_info);
				ormp.put("days", days);
				ormp.put("checkin_date", checkin_date);
				ormp.put("checkout_date", checkout_date);
				ormp.put("fkname", fkname);
				ormp.put("fkphone", fkphone);
				o.add(ormp);
			}catch(Exception e){
				
			}
			String nextpage = "";
			try{
				Element next = main_table.select("a.font_st").first();
				nextpage = next.attr("href");
			}catch (Exception e){
				e.printStackTrace();
			}
			result.add(o);
			result.add(nextpage);
			
		}
		return result;
	}
	public static boolean CrawlPageData(String ct , SaveDAO db){
		/*
		 * connect to db
		 */
		String xml = System.getProperty("user.dir")+"\\src\\dao\\database_"+ct+".xml";
		db.CreateDB(ct);
		db.ConnectToDB(xml);
		/*
		 *  Page1
		 */
		int i = 0;
		int j = 1;
		try{
			while(true){
				
				int flag=0;
				String url="http://"+ct+".xiaozhu.com/"+i+"-"+(i+j)+"yuan-duanzufang-p1-1/";
			    String html=getWeb(url, false);
			    Document isNull = Jsoup.parse(html);
			    Elements house = isNull.select("li[lodgeunitid]");
			    if (house.isEmpty()){
			    	if(flag==20||(i+j)>9999){
			    		break;
			    	}
			    	flag++;
			    	j=j*2;
			    }
			   
				List <Map<String,String>> data1 = new ArrayList<Map<String, String>>();
				int pageNum = 1;
				while (true){
					data1.addAll((List<Map<String, String>>) ReadPage1(html,false ).get(0));
					String next = (String) ReadPage1(html,false).get(1);
					String num = "";
					String page = "0";
				    Matcher m = Pattern.compile("-p\\d+-").matcher(next);
				    if (m.find()){
					    num = m.group();
				    }
				    Matcher mm = Pattern.compile("\\d+").matcher(num);
				    if (mm.find()){
					    page = mm.group();
				    }
				    
					System.out.println(pageNum);
					if (pageNum>Integer.parseInt(page)){
						break;
					}else{
						pageNum = Integer.parseInt(page);
						html=getWeb(next, false);
					}
				}
				
		//	    List <Map<String,String>> temp = a.ReadPage1(html,false );
				System.out.println(data1.size());
				if ((data1.size() < 300&&data1.size() > 0)||(data1.size()==300&&j==1)) { 
					i=i+j+1;
					j=1;
			        for (Map<String,String> hs :data1){
			        	System.out.println(hs.get("link"));
					    /*
					     * saving to database
					     */
			        	db.SavePage1(hs, ct);
			        }
				}
			}
			/*
			 *  Page2
			 */
			 List<String> listHouse = new ArrayList<String>(); 
			 listHouse = db.FindhidFromPage1(ct);
			 //test
//			 listHouse.add("525041101");
//			 listHouse.add("541769701");
			 for (String hid :listHouse){
				 int page = 1;
				 while (true){
				
					 String hslink = "http://"+ct+".xiaozhu.com/ajax.php?op=Ajax_GetDetailComment&lodgeId="+hid+"&cityDomain=undefined&p="+String.valueOf(page);
					 List <Map<String,String>> temp = ReadPage2(hslink,false );
					 if (temp.isEmpty()){
						 break;
					 }
					 page++;
					 for (Map<String,String> comt :temp){
				        	System.out.println(comt.get("hid")+"|"+comt.get("did")+"|"+comt.get("uid")+"|"+comt.get("uname")+"|"+comt.get("ulink")+"|"+comt.get("checkindate")+"|"+comt.get("content"));
				        	/*
				        	 * saving to database
				        	 */
				        	db.SavePage2(comt, ct);
					 }
				 }
			 }
			 
			 /*
			  * Page3(fangke)
			  */
			 List<String> listfangke = new ArrayList<String>();
			 listfangke = db.FinduidFromPage2(ct);
//			 listfangke.add("2223165445");
			 for (String uid : listfangke){
				 String url = "http://www.xiaozhu.com/fangke/"+uid+"/";
				 Map<String, String> temp = ReadPage3(url,false);
				 /*
				  * saving to database
				  */
				 db.SavePage3(temp, ct);
				 List<Map<String,String>> temp1 = ReadPage3_trace(url,false);
				 for (Map track : temp1){
					 /*
					  * saving to database
					  */
					 db.SavePage3_trace(track, ct);
				 }
			 }
			 
			 /*
			  * Page4(fangdong)
			  */
			 List<String> listfangdong = new ArrayList<String>();
//			 listfangdong.add("439278800");
//			 listfangdong.add("50019800");
			 listfangdong = db.FindfdidFromPage1(ct);
			 for (String fdid : listfangdong){
				 String url = "http://www.xiaozhu.com/fangdong/"+fdid+"/";
				 Map<String, String> temp = ReadPage4(url,false);
				 /*
				  * saving to database
				  */
				 db.SavePage4_FANGDONG(temp, ct);
				 /*
				  * Page4(fangdong dingdan)
				  */
				 int page = 1;
				 while (true){
					 url = "http://www.xiaozhu.com/fangdong/"+fdid+ "/yuding/p"+page+".html";
					 List odlist = ReadPage4_yuding(url,false);
					 if (odlist.isEmpty()){
						 break;
					 }
					 page++;
					 List<Map<String, String>> odmap =(List<Map<String, String>>) odlist.get(0);
					 if(odmap.isEmpty()){
						 break;
					 }
					 for(Map<String, String> order : odmap){
						 /*
						  * saving to database
						  */
						 db.SavePage4_ORDER(order, ct);
					 }
				 }
			 }
		}
        catch (Exception e){
        	e.printStackTrace();
        	return false;
        }
		return true;
	}
	public  static void test(String ct,SaveDAO db) throws IOException{
		/*
		 * connect to DataBase
		 */
		String xml = System.getProperty("user.dir")+"\\src\\dao\\database_"+ct+".xml";
		db.CreateDB(ct);
		db.ConnectToDB(xml);
		int i = 0;
		int j = 1;
		try{
			/*
			 * house_info: http://bj.xiaozhu.com/fangzi/1466098635.html
			 */
			String link = "http://cd.xiaozhu.com/fangzi/2334025663.html";
			String house_info = getWeb(link,false);
			Document doc1 = Jsoup.parse(house_info);
			Element hsinfo = doc1.select("ul.house_info").first();
			Elements tags = hsinfo.getElementsByTag("li");
			String area ="";
			String rooms = "";
			String guestsnum = "";
			String living_condition = "";
			String beds = "";
			String[] info1 = tags.get(0).getElementsByTag("p").text().split(" ");
//			BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(info1.getBytes(Charset.forName("utf8"))), Charset.forName("utf8"))); 
			for(String line : info1){
				if (line.contains("房间面积：")){
			    	area = line.replace("房间面积：", "");
			    }
				// excepted encoding
				if (line.contains("房屋面积：")){
			    	area = line.replace("房屋面积：", "");
			    }
			    if (line.contains("房屋户型：")){
			    	rooms = line.replace("房屋户型：", "");
			    }
			}
			living_condition = tags.get(1).getElementsByTag("p").text().replace(" ", "");
			guestsnum = tags.get(1).select("h6.h_ico2").first().text().replace("宜住", "").replace("人", "");
			beds = tags.get(2).select("h6.h_ico3").first().text().replace("共", "").replace("张", "");
				 
			 
			 
		}catch (Exception e){
			e.printStackTrace();
		}
		finally{
			db.DisconnectToDB();
		}
	}
	
	public void run() {
		System.out.println("crawling :"+City);
		try {
//			test(City,DBFunc);
			CrawlPageData(City,DBFunc);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("#########################################################################################");
    	System.out.println("######################################    DONE    #######################################");
    	System.out.println("#########################################################################################");
	}
	
	public static void main(String[] args) throws IOException  {
		ReadPage bj = new ReadPage("bj",new SaveDAOImpl());
//		ReadPage sh = new ReadPage("sh",new SaveDAOImpl());
//		ReadPage gz = new ReadPage("gz",new SaveDAOImpl());
//		ReadPage cd = new ReadPage("cd",new SaveDAOImpl());
//		ReadPage sz = new ReadPage("sz",new SaveDAOImpl());
//		ReadPage xa = new ReadPage("xa",new SaveDAOImpl());
		bj.start();
//		sh.start();
//		gz.start();
//		cd.start();
//		sz.start();
//		xa.start();
//		ReadPage test =new ReadPage();
//		List a= test.ReadPage4_yuding("http://www.xiaozhu.com/fangdong/439278800/yuding.html", false);
		
	
	}
}
