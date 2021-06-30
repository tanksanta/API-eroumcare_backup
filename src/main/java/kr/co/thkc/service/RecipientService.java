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
public class RecipientService extends BaseService {

    @Autowired
    private AbstractDAO abstractDAO;

    /**
     * 수급자 조회
     */
    public BaseResponse selectRecipient(Map<String, Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        //페이징 처리
        int pageNum = MapUtils.getIntValue(params, "pageNum");
        int pageSize = MapUtils.getIntValue(params, "pageSize");

        int offset = pageSize * (pageNum - 1);

        params.put("pageSize", pageSize);
        params.put("pageNum", pageNum);
        params.put("offset", offset);

        Integer total = (Integer) abstractDAO.selectOne("recipient.selectRecipientCnt", params);
        List result = abstractDAO.selectList("recipient.selectRecipientList", params);

        response.setTotal(total == null ? 0 : total);
        response.setData(result);
        response.setResult(ResultCode.RC_OK);

        return response;
    }

    /**
     * 수급자별 취급상품 조회
     */
    public BaseResponse selectRecipientItemList(Map<String, Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        List result = abstractDAO.selectList("recipient.selectRecipientItemList", params);

        response.setData(result);
        response.setResult(ResultCode.RC_OK);

        return response;
    }

    /**
     * 예비수급자 조회
     */
    public BaseResponse selectSpareRecipient(Map<String, Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        //페이징 처리
        int pageNum = MapUtils.getIntValue(params, "pageNum");
        int pageSize = MapUtils.getIntValue(params, "pageSize");

        int offset = pageSize * (pageNum - 1);

        params.put("pageSize", pageSize);
        params.put("pageNum", pageNum);
        params.put("offset", offset);

        Integer total = (Integer) abstractDAO.selectOne("recipient.selectSpareRecipientCnt", params);
        List result = abstractDAO.selectList("recipient.selectSpareRecipientList", params);

        response.setTotal(total == null ? 0 : total);
        response.setData(result);
        response.setResult(ResultCode.RC_OK);

        return response;
    }

    /**
     * 욕구사정기록지 조회
     */
    public BaseResponse selectRecipientRec(Map<String, Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        //페이징 처리
        int pageNum = MapUtils.getIntValue(params, "pageNum");
        int pageSize = MapUtils.getIntValue(params, "pageSize");

        int offset = pageSize * (pageNum - 1);

        params.put("pageSize", pageSize);
        params.put("pageNum", pageNum);
        params.put("offset", offset);

        Integer total = (Integer) abstractDAO.selectOne("recipient.selectRecipientRecCnt", params);
        List result = abstractDAO.selectList("recipient.selectRecipientRecList", params);

        response.setTotal(total == null ? 0 : total);
        response.setData(result);
        response.setResult(ResultCode.RC_OK);

        return response;
    }


    /**
     * 수급자추가
     **/
    public BaseResponse insertRecipient(Map<String, Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();
        //전화번호 암호화
        SDBCryptUtil sdb = new SDBCryptUtil();

        String penConNum = MapUtils.getString(params, "penConNum");
        String penConPnum = MapUtils.getString(params, "penConPnum");
        String penProConNum = MapUtils.getString(params, "penProConNum");
        String penProConPnum = MapUtils.getString(params, "penProConPnum");
        String penExpiStDtm = MapUtils.getString(params, "penExpiStDtm");
        String penExpiEdDtm = MapUtils.getString(params, "penExpiEdDtm");

        if (penConNum != null && !penConNum.equals("")) params.put("penConNum", sdb.encrypt(penConNum));
        if (penConPnum != null && !penConPnum.equals("")) params.put("penConPnum", sdb.encrypt(penConPnum));
        if (penProConNum != null && !penProConNum.equals("")) params.put("penProConNum", sdb.encrypt(penProConNum));
        if (penProConPnum != null && !penProConPnum.equals("")) params.put("penProConPnum", sdb.encrypt(penProConPnum));

        // penExpiStDtm, penExpiEdDtm 빈 값 처리
        if (penExpiStDtm != null && penExpiStDtm.equals("")) params.put("penExpiStDtm", null);
        if (penExpiEdDtm != null && penExpiEdDtm.equals("")) params.put("penExpiEdDtm", null);

        //쇼핑몰의 admin은 시스템의 관리자 아이디 (wmdsadm) 으로 대체
        if (MapUtils.getString(params, "usrId").equals("admin")) params.put("entUsrId", "123456789");
        else params.put("entUsrId", MapUtils.getString(params, "usrId"));

        String penId = newPenId();
        params.put("penId", penId);

        abstractDAO.insert("recipient.insertRecipient", params);
        abstractDAO.insert("recipient.insertEntRecipient", params);

        response.setData(new HashMap() {{
            put("penId", penId);
        }});
        response.setResult(ResultCode.RC_OK);

        return response;
    }

    /**
     * 예비 수급자추가
     **/
    public BaseResponse insertSpareRecipient(Map<String, Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();
        //전화번호 암호화
        SDBCryptUtil sdb = new SDBCryptUtil();

        String penConNum = MapUtils.getString(params, "penConNum");
        String penConPnum = MapUtils.getString(params, "penConPnum");
        String penProConNum = MapUtils.getString(params, "penProConNum");
        String penProConPnum = MapUtils.getString(params, "penProConPnum");
        String penExpiStDtm = MapUtils.getString(params, "penExpiStDtm");
        String penExpiEdDtm = MapUtils.getString(params, "penExpiEdDtm");
        String penAppStDtm1 = MapUtils.getString(params, "penAppStDtm1");
        String penAppEdDtm1 = MapUtils.getString(params, "penAppEdDtm1");
        String penAppStDtm2 = MapUtils.getString(params, "penAppStDtm2");
        String penAppEdDtm2 = MapUtils.getString(params, "penAppEdDtm2");
        String penAppStDtm3 = MapUtils.getString(params, "penAppStDtm3");
        String penAppEdDtm3 = MapUtils.getString(params, "penAppEdDtm3");
        String penRecDtm = MapUtils.getString(params, "penRecDtm");
        String penAppDtm = MapUtils.getString(params, "penAppDtm");

        if (penConNum != null && !penConNum.equals("")) params.put("penConNum", sdb.encrypt(penConNum));
        if (penConPnum != null && !penConPnum.equals("")) params.put("penConPnum", sdb.encrypt(penConPnum));
        if (penProConNum != null && !penProConNum.equals("")) params.put("penProConNum", sdb.encrypt(penProConNum));
        if (penProConPnum != null && !penProConPnum.equals("")) params.put("penProConPnum", sdb.encrypt(penProConPnum));

        // date형식 데이터 빈 값 처리
        if (penExpiStDtm != null && penExpiStDtm.equals("")) params.put("penExpiStDtm", null);
        if (penExpiEdDtm != null && penExpiEdDtm.equals("")) params.put("penExpiEdDtm", null);
        if (penAppStDtm1 != null && penAppStDtm1.equals("")) params.put("penAppStDtm1", null);
        if (penAppEdDtm1 != null && penAppEdDtm1.equals("")) params.put("penAppEdDtm1", null);
        if (penAppStDtm2 != null && penAppStDtm2.equals("")) params.put("penAppStDtm2", null);
        if (penAppEdDtm2 != null && penAppEdDtm2.equals("")) params.put("penAppEdDtm2", null);
        if (penAppStDtm3 != null && penAppStDtm3.equals("")) params.put("penAppStDtm3", null);
        if (penAppEdDtm3 != null && penAppEdDtm3.equals("")) params.put("penAppEdDtm3", null);
        if (penRecDtm != null && penRecDtm.equals("")) params.put("penRecDtm", null);
        if (penAppDtm != null && penAppDtm.equals("")) params.put("penAppDtm", null);


        //쇼핑몰의 admin은 시스템의 관리자 아이디 (wmdsadm) 으로 대체
        if (MapUtils.getString(params, "usrId").equals("admin")) params.put("entUsrId", "123456789");
        else params.put("entUsrId", MapUtils.getString(params, "usrId"));

        String penId = newPenId();
        params.put("penId", penId);

        abstractDAO.insert("recipient.insertSpareRecipient", params);

        response.setData(new HashMap() {{
            put("penId", penId);
        }});
        response.setResult(ResultCode.RC_OK);

        return response;
    }


    /**
     * 수급자별 품목 추가
     **/
    public BaseResponse setRecipientItem(Map<String, Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        abstractDAO.delete("recipient.deleteRecipientItem", params);

        List<String> itemList = MapUtils.getObject(params, "itemList") != null ? (List<String>) MapUtils.getObject(params, "itemList") : null;

        for (String itemId : itemList) {
            params.put("itemId", itemId);
            abstractDAO.insert("recipient.insertRecipientItem", params);
        }


        response.setResult(ResultCode.RC_OK);

        return response;
    }

    /**
     * 수급자수정
     **/
    public BaseResponse updateRecipient(Map<String, Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        //전화번호 암호화
        SDBCryptUtil sdb = new SDBCryptUtil();

        String penConNum = MapUtils.getString(params, "penConNum");
        String penConPnum = MapUtils.getString(params, "penConPnum");
        String penProConNum = MapUtils.getString(params, "penProConNum");
        String penProConPnum = MapUtils.getString(params, "penProConPnum");

        if (penConNum != null && !penConNum.equals("")) params.put("penConNum", sdb.encrypt(penConNum));
        if (penConPnum != null && !penConPnum.equals("")) params.put("penConPnum", sdb.encrypt(penConPnum));
        if (penProConNum != null && !penProConNum.equals("")) params.put("penProConNum", sdb.encrypt(penProConNum));
        if (penProConPnum != null && !penProConPnum.equals("")) params.put("penProConPnum", sdb.encrypt(penProConPnum));

        abstractDAO.update("recipient.updateRecipient", params);

        response.setResult(ResultCode.RC_OK);

        return response;
    }

    /**
     * 수급자수정
     **/
    public BaseResponse updateSpareRecipient(Map<String, Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        //전화번호 암호화
        SDBCryptUtil sdb = new SDBCryptUtil();

        String penConNum = MapUtils.getString(params, "penConNum");
        String penConPnum = MapUtils.getString(params, "penConPnum");
        String penProConNum = MapUtils.getString(params, "penProConNum");
        String penProConPnum = MapUtils.getString(params, "penProConPnum");

        if (penConNum != null && !penConNum.equals("")) params.put("penConNum", sdb.encrypt(penConNum));
        if (penConPnum != null && !penConPnum.equals("")) params.put("penConPnum", sdb.encrypt(penConPnum));
        if (penProConNum != null && !penProConNum.equals("")) params.put("penProConNum", sdb.encrypt(penProConNum));
        if (penProConPnum != null && !penProConPnum.equals("")) params.put("penProConPnum", sdb.encrypt(penProConPnum));

        abstractDAO.update("recipient.updateSpareRecipient", params);

        response.setResult(ResultCode.RC_OK);

        return response;
    }


    /**
     * 욕구사정기록지 추가
     **/
    public BaseResponse insertRecipientRec(Map<String, Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        String recId = newRecId();
        params.put("recId", recId);

        abstractDAO.insert("recipient.insertRecipientRec", params);

        response.setResult(ResultCode.RC_OK);

        return response;
    }

    /**
     * 욕구사정기록지 수정
     **/
    public BaseResponse updateRecipientRec(Map<String, Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        abstractDAO.update("recipient.updateRecipientRec", params);

        response.setResult(ResultCode.RC_OK);

        return response;
    }

    /**
     * 욕구사정기록지 삭제
     **/
    public BaseResponse deleteRecipientRec(Map<String, Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        int result = abstractDAO.delete("recipient.deleteRecipientRec", params);

        if (result > 0) {
            response.setResult(ResultCode.RC_OK);
        } else {
            response.setResult(ResultCode.RC_FAIL);
        }

        return response;
    }


    /*수급자의 전화번호가 암호화 되지 않은 것들을 뽑아내기 위해 임시로 생성*/
    @Deprecated
    public BaseResponse updateEncrypt(Map<String, Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        //전화번호 암호화 모듈
        SDBCryptUtil sdb = new SDBCryptUtil();

        List<Map> mapList = (List<Map>) abstractDAO.selectList("recipient.selectConNum", null);

        for (Map map : mapList) {
            String penConNum = MapUtils.getString(map, "penConNum");
            String penConPnum = MapUtils.getString(map, "penConPnum");
            String penProConNum = MapUtils.getString(map, "penProConNum");
            String penProConPnum = MapUtils.getString(map, "penProConPnum");

            if (penConNum != null && !penConNum.equals("")) map.put("penConNum", sdb.encrypt(penConNum));
            if (penConPnum != null && !penConPnum.equals("")) map.put("penConPnum", sdb.encrypt(penConPnum));
            if (penProConNum != null && !penProConNum.equals("")) map.put("penProConNum", sdb.encrypt(penProConNum));
            if (penProConPnum != null && !penProConPnum.equals(""))
                map.put("penProConPnum", sdb.encrypt(penProConPnum));

            abstractDAO.update("recipient.updateRecipient", params);
        }
        response.setResult(ResultCode.RC_OK);

        return response;
    }

}
