package com.foxconn.electronics.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
@Deprecated
public class HttpUtil
{
    public static void main(String[] args) throws IOException
    {
        // String result = sendGet("http://10.203.163.43/tc-integrate/pnms/getHHPNInfo", "hhpn=7B175C400-HV7-G");
        // String result = post("http://10.203.163.43/tc-integrate/pnms/getHHPNInfo", "hhpn=7B175C400-HV7-G");
        // String result = httpGet("http://10.203.163.43/tc-integrate/pnms/getHHPNInfo", "hhpn=7B175C400-HV7-G", 10000);
        String result = httpPost("http://10.203.163.43/tc-service/materialInfo/getMaterialGroupAndBaseUnit", "[{\"materialNum\":\"0101012212\"}]", 10000);
        System.out.println();
    }

    public static String post(String actionUrl, String params) throws IOException
    {
        String serverURL = actionUrl;
        StringBuffer sbf = new StringBuffer();
        String strRead = null;
        URL url = new URL(serverURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");// 鐠囬攱鐪皃ost閺傜懓绱�
        connection.setDoInput(true);
        connection.setDoOutput(true);
        // header閸愬懐娈戦惃鍕棘閺佹澘婀潻娆撳櫡set
        // connection.setRequestProperty("key", "value");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.connect();
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
        // body閸欏倹鏆熼弨鎹愮箹闁诧拷
        writer.write(params);
        writer.flush();
        InputStream is = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        while ((strRead = reader.readLine()) != null)
        {
            sbf.append(strRead);
            sbf.append("\r\n");
        }
        reader.close();
        connection.disconnect();
        String results = sbf.toString();
        return results;
    }

    /**
     * post璇锋眰鐨勬柟寮�
     * 
     * @param map
     * @return
     */
    public static String httpPost(HashMap map)
    {
        String content = "";
        try
        {
            String ruleName = map.get("ruleName").toString().trim();
            String requestPath = map.get("requestPath").toString().trim();
            String url = requestPath + ruleName;
            System.out.println(url);
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            // httpPost.setHeader("Content-Type","application/x-www-form-urlencoded");
            Gson gson = new Gson();
            if (map == null)
            {
                System.out.println("null");
                return "";
            }
            System.out.println("map=" + map);
            String params = gson.toJson(map);
            System.out.println("params: " + params);
            StringBody contentBody = new StringBody(params, CharsetUtils.get("UTF-8"));
            // 浠ユ祻瑙堝櫒鍏煎妯″紡璁块棶,鍚﹀垯灏辩畻鎸囧畾缂栫爜鏍煎紡,涓枃鏂囦欢鍚嶄笂浼犱篃浼氫贡鐮�
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addPart("data", contentBody);
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);
            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode())
            {
                HttpEntity entitys = response.getEntity();
                if (entitys != null)
                {
                    content = EntityUtils.toString(entitys);
                }
            }
            httpClient.getConnectionManager().shutdown();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("post request commit error" + e);
            System.out.println("post request to microservice failure, please check microservice");
        }
        return content;
    }

    /**
     * 鍙戦�乬et璇锋眰
     * 
     * @param url 璺緞
     * @return
     * @throws IOException
     * @throws ClientProtocolException
     */
    public static String httpGet(String url, String param, int timeout) throws ClientProtocolException, IOException
    {
        // get璇锋眰杩斿洖缁撴灉
        String strResult = "";
        String realUrl = url + "?" + param;
        CloseableHttpClient client = HttpClients.custom().build();
        HttpGet request = new HttpGet(realUrl);
        request.setConfig(RequestConfig.custom().setConnectTimeout(timeout).setConnectionRequestTimeout(timeout).setSocketTimeout(timeout).build());
        HttpResponse response = client.execute(request);
        // 璇锋眰鍙戦�佹垚鍔燂紝骞跺緱鍒板搷搴�
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
        {
            // 璇诲彇鏈嶅姟鍣ㄨ繑鍥炶繃鏉ョ殑json瀛楃涓叉暟鎹�
            strResult = EntityUtils.toString(response.getEntity());
            // 鎶妀son瀛楃涓茶浆鎹㈡垚json瀵硅薄
            // JSONObject jsonResult = JSONObject.fromObject(strResult);
            url = URLDecoder.decode(url, "UTF-8");
        }
        return strResult;
    }
    
    public static String httpGet(String url,int timeout) throws ClientProtocolException, IOException
    {
        // get璇锋眰杩斿洖缁撴灉
        String strResult = "";
        CloseableHttpClient client = HttpClients.custom().build();
        HttpGet request = new HttpGet(url);
        request.setConfig(RequestConfig.custom().setConnectTimeout(timeout).setConnectionRequestTimeout(timeout).setSocketTimeout(timeout).build());
        HttpResponse response = client.execute(request);
        // 璇锋眰鍙戦�佹垚鍔燂紝骞跺緱鍒板搷搴�
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
        {
            // 璇诲彇鏈嶅姟鍣ㄨ繑鍥炶繃鏉ョ殑json瀛楃涓叉暟鎹�
            strResult = EntityUtils.toString(response.getEntity(),"UTF-8");
            // 鎶妀son瀛楃涓茶浆鎹㈡垚json瀵硅薄
            // JSONObject jsonResult = JSONObject.fromObject(strResult);
            url = URLDecoder.decode(url, "UTF-8");
        }
        return strResult;
    }

    public static String httpPost(String url, String bodyJson, int timeout) throws ClientProtocolException, IOException
    {
        String result = "";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(RequestConfig.custom().setConnectTimeout(timeout).setConnectionRequestTimeout(timeout).setSocketTimeout(timeout).build());
        httpPost.addHeader("Content-Type", "application/json;charset=utf-8");
        httpPost.setEntity(new StringEntity(bodyJson, "utf-8"));
        CloseableHttpResponse response = null;
        try
        {
            response = client.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200)
            {
                HttpEntity resultEntity = response.getEntity();
                if (resultEntity != null)
                {
                    result = EntityUtils.toString(resultEntity, "utf-8");
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("鍙戦�乸ost澶辫触");
        }
        finally
        {
            if (client != null)
            {
                try
                {
                    client.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if (response != null)
            {
                try
                {
                    response.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static String sendGet(String url, String param)
    {
        String result = "";
        BufferedReader in = null;
        try
        {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 鎵撳紑鍜孶RL涔嬮棿鐨勮繛鎺�
            URLConnection connection = realUrl.openConnection();
            // 璁剧疆閫氱敤鐨勮姹傚睘鎬�
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 寤虹珛瀹為檯鐨勮繛鎺� connection.connect();
            // 鑾峰彇鎵�鏈夊搷搴斿ご瀛楁
            // 瀹氫箟 BufferedReader杈撳叆娴佹潵璇诲彇URL鐨勫搷搴�
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null)
            {
                result += line;
            }
        }
        catch (Exception e)
        {
            System.out.println("鍙戦�丟ET璇锋眰鍑虹幇寮傚父锛�" + e);
            e.printStackTrace();
            return "failure";
        }
        // 浣跨敤finally鍧楁潵鍏抽棴杈撳叆娴�
        finally
        {
            try
            {
                if (in != null)
                {
                    in.close();
                }
            }
            catch (Exception e2)
            {
                e2.printStackTrace();
            }
        }
        return result;
    }
}
