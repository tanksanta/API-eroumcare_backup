package kr.co.thkc.controller;

import kr.co.thkc.dispatch.BaseRequest;
import kr.co.thkc.dispatch.BaseResponse;
import kr.co.thkc.service.ProdService;
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
@RequestMapping(value = BaseController.baseUrl + "/prod", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
public class ProdController {

    @Autowired
    ProdService prodService;


    /*
     * ------------------------------------------------------
     *                       상품
     * ------------------------------------------------------
     * */

    /**
     * 상품 목록 조회
     */
    @PostMapping(value = "selectList")
    public BaseResponse selectProdList(@RequestBody BaseRequest request) throws Exception {

        // requiredField add
//        request.addRequiredField("prodId", request.TYPE_STRING);

        request.validRequiredField();

        // bussiness logic
        return prodService.selectProdList(request.bindRequest());
    }

    /**
     * 상품 상세 조회
     */
    @PostMapping(value = "select")
    public BaseResponse selectProd(@RequestBody BaseRequest request) throws Exception {

        // requiredField add
        request.addRequiredField("prodId", request.TYPE_STRING);

        request.validRequiredField();

        // bussiness logic
        return prodService.selectProdDetail(request.bindRequest());
    }

    /**
     * 품목 조회
     */
    @PostMapping(value = "selectItemList")
    public BaseResponse selectItemList(@RequestBody BaseRequest request) throws Exception {

        // requiredField add
//        request.addRequiredField("prodId", request.TYPE_STRING);

        request.validRequiredField();

        // bussiness logic
        return prodService.selectItemList(request.bindRequest());
    }


    /**
     * 상품 목록 추가
     */
    @PostMapping(value = "insert")
    public BaseResponse insertProd(@RequestParam(EROUM_PARAMS_NAME) String params, MultipartHttpServletRequest multipartRequest) throws Exception {
        //파라미터를 가져올때 \\이 String에 포함된다. 이부분 제거해야함
        params = params.replace("\\\"", "\"");

        //file세팅
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap.isEmpty()) throw new Exception("제품이미지는 필수입니다.");


        //파라미터 세팅
        BaseRequest request = new BaseRequest();
        request.setParams(StringUtil.convertJSONstringToMap(params));

        //requiredField add
        request.addRequiredField("usrId", request.TYPE_STRING);
        request.addRequiredField("prodNm", request.TYPE_STRING);
        request.addRequiredField("itemId", request.TYPE_STRING);
//        request.addRequiredField("supId", request.TYPE_STRING);
        request.addRequiredField("gubun", request.TYPE_STRING);
        request.addRequiredField("prodPayCode", request.TYPE_STRING);
        request.addRequiredField("prodSupYn", request.TYPE_STRING);

        request.validRequiredField();

        // bussiness logic
        return prodService.insertProd(request.bindRequest(), fileMap);
    }


    /**
     * 상품 목록 수정
     */
    @PostMapping(value = "update")
    public BaseResponse updateProd(@RequestParam(EROUM_PARAMS_NAME) String params, MultipartHttpServletRequest multipartRequest) throws Exception {
        //파라미터를 가져올때 \\이 String에 포함된다. 이부분 제거해야함
        params = params.replace("\\", "");

        //file세팅
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();

        //파라미터 세팅
        BaseRequest request = new BaseRequest();
        request.setParams(StringUtil.convertJSONstringToMap(params));

        // requiredField add
        request.addRequiredField("prodId", request.TYPE_STRING);

        request.validRequiredField();

        // bussiness logic
        return prodService.updateProd(request.bindRequest(), fileMap);
    }


    /**
     * 상품 목록 삭제
     */
    @PostMapping(value = "delete")
    public BaseResponse deleteProd(@RequestBody BaseRequest request) throws Exception {

        // requiredField add
        request.addRequiredField("prodId", request.TYPE_STRING);

        request.validRequiredField();

        // bussiness logic
        return prodService.deleteProd(request.bindRequest());
    }



    /*
     * ------------------------------------------------------
     *                       취급상품
     * ------------------------------------------------------
     * */

    /**
     * 취급 제품 목록 조회
     */
    @PostMapping(value = "selectPpcList")
    public BaseResponse selectProdPpcList(@RequestBody BaseRequest request) throws Exception {

        // requiredField add
        request.addRequiredField("entId", request.TYPE_STRING);

        request.validRequiredField();

        // bussiness logic
        return prodService.selectProdPpcList(request.bindRequest());
    }

    /**
     * 취급 제품 목록 추가
     */
    @PostMapping(value = "insertPpc")
    public BaseResponse insertPpc(@RequestBody BaseRequest request) throws Exception {

        //requiredField add
        request.addRequiredField("usrId", request.TYPE_STRING);
        request.addRequiredField("entId", request.TYPE_STRING);
        request.addRequiredField("prodId", request.TYPE_STRING);

        request.validRequiredField();

        // bussiness logic
        return prodService.insertPpc(request.bindRequest());
    }

    /**
     * 취급 제품 삭제
     */
    @PostMapping(value = "deletePpc")
    public BaseResponse deletePpc(@RequestBody BaseRequest request) throws Exception {

        //requiredField add
        request.addRequiredField("ppcId", request.TYPE_STRING);

        request.validRequiredField();

        // bussiness logic
        return prodService.deletePpc(request.bindRequest());
    }
}
