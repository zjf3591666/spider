

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;



public class test {
	
	
	
	public static void main(String args[]) {  
        //����HttpClientBuilder  
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();  
        //HttpClient  
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();  
  
        HttpGet httpGet = new HttpGet("http://bj.xiaozhu.com/");  
        System.out.println(httpGet.getRequestLine());  
        try {  
            //ִ��get����  
            HttpResponse httpResponse = closeableHttpClient.execute(httpGet);  
            //��ȡ��Ӧ��Ϣʵ��  
            HttpEntity entity = httpResponse.getEntity();  
            //��Ӧ״̬  
            System.out.println("status:" + httpResponse.getStatusLine());  
            //�ж���Ӧʵ���Ƿ�Ϊ��  
            if (entity != null) { 
            	String html=EntityUtils.toString(entity);
                System.out.println("contentEncoding:" + entity.getContentEncoding());  
                System.out.println("response content:" + html);
                File file = new File("test.html");
                try{
                	file.createNewFile();
                }
                catch (IOException e){
                	e.printStackTrace();
                }
                FileWriter fw =new FileWriter("test.html");
                fw.write(html);
                fw.close();
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            try {  
            	//�ر������ͷ���Դ  
                closeableHttpClient.close();  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
     }
}