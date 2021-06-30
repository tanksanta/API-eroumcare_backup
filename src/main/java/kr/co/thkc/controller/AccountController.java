package kr.co.thkc.controller;

import kr.co.thkc.dispatch.BaseRequest;
import kr.co.thkc.dispatch.BaseResponse;
import kr.co.thkc.service.EntService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping(value = BaseController.baseUrl + "/account", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
public class AccountController {

    @Autowired
    EntService entService;


    /**
     * 사업소 로그인
     */
    @PostMapping(value = "entLogin")
    public BaseResponse entLogin(@RequestBody BaseRequest request) throws Exception {

        // requiredField add
        request.addRequiredField("usrId", request.TYPE_STRING);
        request.addRequiredField("pw", request.TYPE_STRING);

        request.validRequiredField();

        // bussiness logic
        return entService.selectEntLogin(request.bindRequest());
    }

}
