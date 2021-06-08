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

    /*주문옵션 추가*/
    public BaseResponse insertOptionOrd(List<Map> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        //주문옵션 추가
        abstractDAO.insert("option.insertOptionOrd",params);

        response.setResult(ResultCode.RC_OK);

        return response;
    }
    /*재고옵션 추가*/
    public BaseResponse insertOptionStock(List<Map> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        //재고옵션 추가
        abstractDAO.insert("option.insertOptionStock",params);

        response.setResult(ResultCode.RC_OK);

        return response;
    }
    /*상품옵션 추가*/
    public BaseResponse insertOptionProd(List<Map> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        //상품옵션 추가
        abstractDAO.insert("option.insertOptionProd",params);

        response.setResult(ResultCode.RC_OK);

        return response;
    }


    /*주문옵션 수정*/
    public BaseResponse updateOptionOrd(List<Map> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        //주문옵션 추가
        for(Map option:params) {
            abstractDAO.update("option.updateOptionOrd", option);
        }
        response.setResult(ResultCode.RC_OK);

        return response;
    }
    /*재고옵션 수정*/
    public BaseResponse updateOptionStock(List<Map> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        //재고옵션 추가
        for(Map option:params) {
            abstractDAO.update("option.updateOptionStock", option);
        }
        response.setResult(ResultCode.RC_OK);

        return response;
    }
    /*상품옵션 수정*/
    public BaseResponse updateOptionProd(List<Map> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        //상품옵션 추가
        for(Map option:params) {
            abstractDAO.update("option.updateOptionProd", option);
        }
        response.setResult(ResultCode.RC_OK);

        return response;
    }
}
