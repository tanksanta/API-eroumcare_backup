package kr.co.thkc.controller;

import kr.co.thkc.dispatch.BaseRequest;
import kr.co.thkc.dispatch.BaseResponse;
import kr.co.thkc.service.RecipientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping(value = BaseController.baseUrl+"/recipient", consumes = "application/json", produces = "application/json")
public class RecipientController {

    @Autowired
    RecipientService recipientService;

    /**
     *  수급자 조회
     * */
    @PostMapping(value = "selectList")
    public BaseResponse selectRecipient(@RequestBody BaseRequest request) throws Exception {

        // requiredField add
        request.addRequiredField("usrId", request.TYPE_STRING);

        request.validRequiredField();

        // bussiness logic
        return recipientService.selectRecipient(request.bindRequest());
    }
    /**
     *  예비 수급자 조회
     * */
    @PostMapping(value = "selectSpareList")
    public BaseResponse selectSpareRecipient(@RequestBody BaseRequest request) throws Exception {

        // requiredField add
        request.addRequiredField("usrId", request.TYPE_STRING);

        request.validRequiredField();

        // bussiness logic
        return recipientService.selectSpareRecipient(request.bindRequest());
    }

    /**
     *  수급자별 취급상품 조회
     * */
    @PostMapping(value = "selectItemList")
    public BaseResponse selectRecipientItemList(@RequestBody BaseRequest request) throws Exception {

        // requiredField add
        request.addRequiredField("penId", request.TYPE_STRING);
//        request.addRequiredField("gubun", request.TYPE_STRING);

        request.validRequiredField();

        // bussiness logic
        return recipientService.selectRecipientItemList(request.bindRequest());
    }

    /**
     *  수급자 추가
     * */
    @PostMapping(value = "insert")
    public BaseResponse insertRecipient(@RequestBody BaseRequest request) throws Exception {

        // requiredField add
        request.addRequiredField("usrId", request.TYPE_STRING);

        request.validRequiredField();

        // bussiness logic
        return recipientService.insertRecipient(request.bindRequest());
    }

    /**
     *  수급자별 취급상품 추가
     * */
    @PostMapping(value = "setItem")
    public BaseResponse setRecipientItem(@RequestBody BaseRequest request) throws Exception {

        // requiredField add
        request.addRequiredField("penId", request.TYPE_STRING);

        request.validRequiredField();

        // bussiness logic
        return recipientService.setRecipientItem(request.bindRequest());
    }

    /**
     *  수급자 수정
     * */
    @PostMapping(value = "update")
    public BaseResponse updateRecipient(@RequestBody BaseRequest request) throws Exception {

        // requiredField add
        request.addRequiredField("usrId", request.TYPE_STRING);
        request.addRequiredField("penId", request.TYPE_STRING);

        request.validRequiredField();

        // bussiness logic
        return recipientService.updateRecipient(request.bindRequest());
    }

    /**
     *  데이터 암호화
     * */
    @PostMapping(value = "encrpyt")
    public BaseResponse updateEncrypt(@RequestBody BaseRequest request) throws Exception {
        // bussiness logic
        return recipientService.updateEncrypt(request.bindRequest());
    }
}
