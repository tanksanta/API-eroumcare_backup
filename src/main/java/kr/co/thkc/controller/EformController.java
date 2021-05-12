package kr.co.thkc.controller;

import kr.co.thkc.dispatch.BaseRequest;
import kr.co.thkc.dispatch.BaseResponse;
import kr.co.thkc.service.EformService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = BaseController.baseUrl+"/eform", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
public class EformController {

    @Autowired
    EformService eformService;


    /**
     *  전자계약서 데이터 호출
     * */
    @PostMapping(value = "selectEform001")
    public BaseResponse entAccountInfo(@RequestBody BaseRequest request) throws Exception {

        // requiredField add
        request.addRequiredField("penOrdId", request.TYPE_STRING);

        request.validRequiredField();

        // bussiness logic
        return eformService.selectEform001(request.bindRequest());
    }
}
