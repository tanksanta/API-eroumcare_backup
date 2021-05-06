package kr.co.thkc.service;

import kr.co.thkc.mapper.AbstractDAO;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public abstract class BaseService {

    @Autowired
    private AbstractDAO abstractDAO;

    public String newAtchFileId(){
        return abstractDAO.selectOne("file.selectNewAtchFileId",null).toString();
    }

    public String newProdId(){
        return abstractDAO.selectOne("prod.selectNewProdId",null).toString();
    }

    public String newPenId(){
        return abstractDAO.selectOne("recipient.selectNewPenId",null).toString();
    }

    public String newPenOrdId(){
        return abstractDAO.selectOne("order.selectNewPenOrdId",null).toString();
    }

    public String newPpcId(){
        return abstractDAO.selectOne("stock.selectNewPpcId",null).toString();
    }

    public String newEntId(){
        return abstractDAO.selectOne("ent.selectNewEntId",null).toString();
    }

    public String newStoId(){
        return abstractDAO.selectOne("stock.selectNewStoId",null).toString();
    }

    public String newSchId(){
        return abstractDAO.selectOne("set.selectNewSchId",null).toString();
    }



    @SuppressWarnings("rawtypes")
    	public Map excuteHttpLocalClient(String url, String apiKey) throws Exception{

    		String GET_URL = url;

    	       //http client 생성
            CloseableHttpClient httpClient = HttpClients.createDefault();

            //get 메서드와 URL 설정
            HttpGet httpGet = new HttpGet(GET_URL);

            //agent 정보 설정
            httpGet.addHeader("Authorization", apiKey);
            httpGet.addHeader("Content-type", "application/json");

            //get 요청
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);

            System.out.println("GET Response Status");
            System.out.println(httpResponse.getStatusLine().getStatusCode());
            String json = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");

            JSONObject spaceListJSON = new JSONObject(json);


            JSONArray jsonArray = spaceListJSON.getJSONArray("documents");

            HashMap<String, Object> localMap = new HashMap<>();

            JSONObject spaceJSON = jsonArray.getJSONObject(0);
            localMap.put("longitude", spaceJSON.getString("x"));
            localMap.put("latitude", spaceJSON.getString("y"));



       		httpClient.close();

    		return localMap;
    	}
}
