package kr.co.thkc.service;

import kr.co.thkc.dispatch.BaseResponse;
import kr.co.thkc.dispatch.ResultCode;
import kr.co.thkc.mapper.AbstractDAO;
import kr.co.thkc.utils.SDBCryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional(rollbackFor = {Exception.class})
public class RecipientService extends BaseService{

    @Autowired
    private AbstractDAO abstractDAO;

    /**
     * 수급자 조회
     * */
    public BaseResponse selectRecipient(Map<String,Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        //페이징 처리
        int pageNum = MapUtils.getIntValue(params,"pageNum");
        int pageSize = MapUtils.getIntValue(params,"pageSize");

        int offset = pageSize * (pageNum - 1);

        params.put("pageSize",pageSize);
        params.put("pageNum",pageNum);
        params.put("offset",offset);

        Integer total = (Integer)abstractDAO.selectOne("recipient.selectRecipientCnt",params);
        List result = abstractDAO.selectList("recipient.selectRecipientList",params);

        response.setTotal(total == null ? 0 :total);
        response.setData(result);
        response.setResult(ResultCode.RC_OK);

        return response;
    }
    /**
     * 수급자별 취급상품 조회
     * */
    public BaseResponse selectRecipientItemList(Map<String,Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        List result = abstractDAO.selectList("recipient.selectRecipientItemList",params);

        response.setData(result);
        response.setResult(ResultCode.RC_OK);

        return response;
    }
    /**
     * 예비수급자 조회
     * */
    public BaseResponse selectSpareRecipient(Map<String,Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        //페이징 처리
        int pageNum = MapUtils.getIntValue(params, "pageNum");
        int pageSize = MapUtils.getIntValue(params, "pageSize");

        int offset = pageSize * (pageNum - 1);

        params.put("pageSize", pageSize);
        params.put("pageNum", pageNum);
        params.put("offset", offset);

        Integer total = (Integer)abstractDAO.selectOne("recipient.selectSpareRecipientCnt", params);
        List result = abstractDAO.selectList("recipient.selectSpareRecipientList", params);

        response.setTotal(total == null ? 0 :total);
        response.setData(result);
        response.setResult(ResultCode.RC_OK);

        return response;
    }


    /**
     * 수급자추가
    **/
    public BaseResponse insertRecipient(Map<String,Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();
        //전화번호 암호화
        SDBCryptUtil sdb = new SDBCryptUtil();

        String penConNum = MapUtils.getString(params,"penConNum");
        String penConPnum = MapUtils.getString(params,"penConPnum");
        String penProConNum = MapUtils.getString(params,"penProConNum");
        String penProConPnum = MapUtils.getString(params,"penProConPnum");

        if(penConNum!=null && !penConNum.equals("")) params.put("penConNum",sdb.encrypt(penConNum));
        if(penConPnum!=null && !penConPnum.equals("")) params.put("penConPnum",sdb.encrypt(penConPnum));
        if(penProConNum!=null && !penProConNum.equals("")) params.put("penProConNum",sdb.encrypt(penProConNum));
        if(penProConPnum!=null && !penProConPnum.equals("")) params.put("penProConPnum",sdb.encrypt(penProConPnum));

        //쇼핑몰의 admin은 시스템의 관리자 아이디 (wmdsadm) 으로 대
        if(MapUtils.getString(params,"usrId").equals("admin")) params.put("entUsrId","123456789");
        else params.put("entUsrId",MapUtils.getString(params,"usrId"));

        String penId = newPenId();
        params.put("penId",penId);

        abstractDAO.insert("recipient.insertRecipient",params);
        abstractDAO.insert("recipient.insertEntRecipient",params);


        response.setData(new HashMap(){{put("penId",penId);}});
        response.setResult(ResultCode.RC_OK);

        return response;
    }


    /**
     * 수급자별 품목 추가
    **/
    public BaseResponse setRecipientItem(Map<String,Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        abstractDAO.delete("recipient.deleteRecipientItem",params);

        List<String> itemList = MapUtils.getObject(params,"itemList")!=null?(List<String>)MapUtils.getObject(params,"itemList"):null;

        for(String itemId:itemList){
            params.put("itemId",itemId);
            abstractDAO.insert("recipient.insertRecipientItem",params);
        }


        response.setResult(ResultCode.RC_OK);

        return response;
    }

    /**
     * 수급자수정
    **/
    public BaseResponse updateRecipient(Map<String,Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        //전화번호 암호화
        SDBCryptUtil sdb = new SDBCryptUtil();

        String penConNum = MapUtils.getString(params,"penConNum");
        String penConPnum = MapUtils.getString(params,"penConPnum");
        String penProConNum = MapUtils.getString(params,"penProConNum");
        String penProConPnum = MapUtils.getString(params,"penProConPnum");

        if(penConNum!=null && !penConNum.equals("")) params.put("penConNum",sdb.encrypt(penConNum));
        if(penConPnum!=null && !penConPnum.equals("")) params.put("penConPnum",sdb.encrypt(penConPnum));
        if(penProConNum!=null && !penProConNum.equals("")) params.put("penProConNum",sdb.encrypt(penProConNum));
        if(penProConPnum!=null && !penProConPnum.equals("")) params.put("penProConPnum",sdb.encrypt(penProConPnum));

        abstractDAO.update("recipient.updateRecipient",params);

        response.setResult(ResultCode.RC_OK);

        return response;
    }


    public BaseResponse updateEncrypt(Map<String,Object> params) throws SQLException {
            BaseResponse response = new BaseResponse();

            //전화번호 암호화 모듈
            SDBCryptUtil sdb = new SDBCryptUtil();

            List<Map> mapList = (List<Map>) abstractDAO.selectList("recipient.selectConNum",null);

            for(Map map:mapList){
                String penConNum = MapUtils.getString(map,"penConNum");
                String penConPnum = MapUtils.getString(map,"penConPnum");
                String penProConNum = MapUtils.getString(map,"penProConNum");
                String penProConPnum = MapUtils.getString(map,"penProConPnum");

                if(penConNum!=null && !penConNum.equals("")) map.put("penConNum",sdb.encrypt(penConNum));
                if(penConPnum!=null && !penConPnum.equals("")) map.put("penConPnum",sdb.encrypt(penConPnum));
                if(penProConNum!=null && !penProConNum.equals("")) map.put("penProConNum",sdb.encrypt(penProConNum));
                if(penProConPnum!=null && !penProConPnum.equals("")) map.put("penProConPnum",sdb.encrypt(penProConPnum));

                abstractDAO.update("recipient.updateRecipient",params);
            }
            response.setResult(ResultCode.RC_OK);

            return response;
        }

}