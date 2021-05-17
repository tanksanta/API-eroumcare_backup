package kr.co.thkc.controller;

import kr.co.thkc.dispatch.BaseRequest;
import kr.co.thkc.dispatch.BaseResponse;
import kr.co.thkc.service.EntService;
import kr.co.thkc.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.Map;

import static kr.co.thkc.filter.ModifyRequestWrapper.EROUM_PARAMS_NAME;

@Slf4j
@RestController
@RequestMapping(value = BaseController.baseUrl+"/ent", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
public class EntController {

    @Autowired
    EntService entService;



    /**
     *  사업소 회원 조회
     * */
    @PostMapping(value = "account")
    public BaseResponse entAccountInfo(@RequestBody BaseRequest request) throws Exception {

        // requiredField add
        request.addRequiredField("usrId", request.TYPE_STRING);

        request.validRequiredField();

        // bussiness logic
        return entService.selectEntAccount(request.bindRequest());
    }

    /**
     *  사업소 관리자 중복 조회
     * */
    @PostMapping(value = "crnDupCheck")
    public BaseResponse entIdDuplicationCheck(@RequestBody BaseRequest request) throws Exception {

        // requiredField add
        request.addRequiredField("usrId", request.TYPE_STRING);

        request.validRequiredField();

        // bussiness logic
        return entService.selectDuplicatedEntIdCnt(request.bindRequest());
    }

    /**
     *  사업소 등록번호 중복 조회
     * */
    @PostMapping(value = "idDupCheck")
    public BaseResponse entCrnDuplicationCheck(@RequestBody BaseRequest request) throws Exception {

        // requiredField add
        request.addRequiredField("entCrn", request.TYPE_STRING);

        request.validRequiredField();

        // bussiness logic
        return entService.selectDuplicatedEntCrnCnt(request.bindRequest());
    }


    /**
     *  사업소 회원가입
     * */
    @PostMapping(value = "insert")
    public BaseResponse insertEnt(@RequestParam(EROUM_PARAMS_NAME) String params , MultipartHttpServletRequest multipartRequest) throws Exception {
        //파라미터를 가져올때 \\이 String에 포함된다. 이부분 제거해야함
        params = params.replace("\\\"","\"");

        //file세팅
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        log.debug("fileMap:"+fileMap);

        //파라미터 세팅
        BaseRequest request = new BaseRequest();
        request.setParams(StringUtil.convertJSONstringToMap(params));

        // requiredField add
        request.addRequiredField("usrId", request.TYPE_STRING);
        request.addRequiredField("usrPw", request.TYPE_STRING);

        request.validRequiredField();

        // bussiness logic
        return entService.insertEnt(request.bindRequest(), fileMap);
    }

    /**
     *  사업소 수정
     * */
    @PostMapping(value = "update")
    public BaseResponse updateEnt(@RequestParam(EROUM_PARAMS_NAME) String params , MultipartHttpServletRequest multipartRequest) throws Exception {
        //파라미터를 가져올때 \\이 String에 포함된다. 이부분 제거해야함
        params = params.replace("\\","");

        //file세팅
        Map<String,MultipartFile> fileMap = multipartRequest.getFileMap();
        log.debug("fileMap:"+fileMap);

        //파라미터 세팅
        BaseRequest request = new BaseRequest();
        request.setParams(StringUtil.convertJSONstringToMap(params));
        // requiredField add
        request.addRequiredField("entId", request.TYPE_STRING);

        request.validRequiredField();

        // bussiness logic
        return entService.updateEnt(request.bindRequest(),fileMap);
    }

    /**
     *  사업소 회원수정
     * */
    @PostMapping(value = "updateAccount")
    public BaseResponse updateEntAccount(@RequestBody BaseRequest request) throws Exception {

        // requiredField add
        request.addRequiredField("entId", request.TYPE_STRING);
        request.addRequiredField("usrId", request.TYPE_STRING);

        request.validRequiredField();

        // bussiness logic
        return entService.updateEntAccount(request.bindRequest());
    }
}
