package kr.co.thkc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.thkc.dispatch.BaseRequest;
import kr.co.thkc.dispatch.BaseResponse;
import kr.co.thkc.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = BaseController.baseUrl+"/order", consumes = "application/json", produces = "application/json")
public class OrderController extends BaseController{

    @Autowired
    OrderService orderService;

    /**
     * */
    @PostMapping(value = "selectList")
    public BaseResponse selectOrderList(@RequestBody BaseRequest request) throws Exception {

        // requiredField add
        request.addRequiredField("penOrdId", request.TYPE_STRING);

        request.validRequiredField();

        // bussiness logic
        return orderService.selectOrderList(request.bindRequest());
    }



    /**
     *  주문 추가
     * */
    @PostMapping(value = "insert")
    public BaseResponse insertOrder(@RequestBody BaseRequest request) throws Exception {

        // requiredField add
        request.addRequiredField("usrId", request.TYPE_STRING);
        request.addRequiredField("penId", request.TYPE_STRING);
        request.addRequiredField("ordCont", request.TYPE_STRING);
        request.addRequiredField("ordZip", request.TYPE_STRING);
        request.addRequiredField("ordAddr", request.TYPE_STRING);
        request.addRequiredField("finPayment", request.TYPE_STRING);
        request.addRequiredField("payMehCd", request.TYPE_STRING);
        request.addRequiredField("eformType", request.TYPE_STRING);
        request.addRequiredField("returnUrl", request.TYPE_STRING);
        request.validRequiredField();

        List<Map> prods = new ArrayList<>();

        if(request.getParams().containsKey("prods")) {
            prods = new ObjectMapper().convertValue(request.getParams().get("prods"), List.class);
            if (prods == null) throw new Exception("Request is null");
            for(Map prod:prods){
                BaseRequest prodRequest = new BaseRequest(prod);

                prodRequest.addRequiredField("prodId", BaseRequest.TYPE_STRING);
                // requiredField check
                prodRequest.validRequiredField();
            }
        } else {
            throw new Exception("requiredField [prods] is not contain ");
        }


        // bussiness logic
        return orderService.insertOrder(request.bindRequest());
    }


    /**
     *  주문 수정
     * */
    @PostMapping(value = "update")
    public BaseResponse updateOrder(@RequestBody BaseRequest request) throws Exception {

        // requiredField add
        request.addRequiredField("usrId", request.TYPE_STRING);
        request.addRequiredField("penOrdId", request.TYPE_STRING);
        request.validRequiredField();
        // bussiness logic
        return orderService.updateOrder(request.bindRequest());
    }


    /**
     *  주문 삭제
     * */
    @PostMapping(value = "delete")
    public BaseResponse deleteOrder(@RequestBody BaseRequest request) throws Exception {

        // requiredField add
        request.addRequiredField("usrId", request.TYPE_STRING);
        request.addRequiredField("penOrdId", request.TYPE_STRING);
        request.validRequiredField();
        // bussiness logic
        return orderService.deleteOrder(request.bindRequest());
    }
}
