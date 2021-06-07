package kr.co.thkc.service;

import kr.co.thkc.dispatch.BaseResponse;
import kr.co.thkc.dispatch.ResultCode;
import kr.co.thkc.mapper.AbstractDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional(rollbackFor = {Exception.class})
public class OptionService {

    @Autowired
    private AbstractDAO abstractDAO;


    public BaseResponse insertOptionOrd(Map<String,Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        List paramList;

        //주문옵션 추가
        abstractDAO.insert("option.insertOptionOrd",params);

        response.setResult(ResultCode.RC_OK);

        return response;
    }

    public BaseResponse insertOptionStock(Map<String,Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        List paramList;

        //재고옵션 추가
        abstractDAO.insert("option.insertOptionStock",params);

        response.setResult(ResultCode.RC_OK);

        return response;
    }

    public BaseResponse insertOptionProd(Map<String,Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        List paramList;
        Iterator iterator = params.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String)iterator.next();
            if(key.contains("itemName")){

            }
        }
        //상품옵션 추가
        abstractDAO.insert("option.insertOptionProd",params);

        response.setResult(ResultCode.RC_OK);

        return response;
    }
}
