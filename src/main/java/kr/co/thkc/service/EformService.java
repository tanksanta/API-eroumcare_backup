package kr.co.thkc.service;

import kr.co.thkc.dispatch.BaseResponse;
import kr.co.thkc.dispatch.ResultCode;
import kr.co.thkc.mapper.AbstractDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class EformService extends BaseService {

    @Autowired
    private AbstractDAO abstractDAO;


    public BaseResponse selectEform001(Map<String, Object> params) throws Exception {
        BaseResponse response = new BaseResponse();

        List result = abstractDAO.selectList("eform.selectEform001", params);

        response.setResultData(result);
        response.setResult(ResultCode.RC_OK);

        return response;
    }


    public BaseResponse insertEform(Map<String, Object> params) throws Exception {
        BaseResponse response = new BaseResponse();

        abstractDAO.insert("eform.insertEformMaster", params);

        response.setResult(ResultCode.RC_OK);

        return response;
    }

}
