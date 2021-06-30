package kr.co.thkc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.thkc.dispatch.BaseRequest;
import kr.co.thkc.dispatch.BaseResponse;
import kr.co.thkc.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping(value = BaseController.baseUrl + "/stock", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
public class StockController {

    @Autowired
    StockService stockService;


    /**
     * 재고 목록 조회
     */
    @PostMapping(value = "selectList")
    public BaseResponse selectStockList(@RequestBody BaseRequest request) throws Exception {

        // requiredField add
        request.addRequiredField("usrId", request.TYPE_STRING);
        request.addRequiredField("prods", request.TYPE_LIST);

        request.validRequiredField();

        // bussiness logic
        return stockService.selectStockList(request.bindRequest());
    }


    /**
     * 사업소별 재고 목록 조회
     */
    @PostMapping(value = "selectListForEnt")
    public BaseResponse selectStockListForEnt(@RequestBody BaseRequest request) throws Exception {

        // requiredField add
        request.addRequiredField("entId", request.TYPE_STRING);

        request.validRequiredField();

        // bussiness logic
        return stockService.selectStockListForEnt(request.bindRequest());
    }

    /**
     * 상세 재고 목록 조회
     */
    @PostMapping(value = "selectDetailList")
    public BaseResponse selectStockDetailList(@RequestBody BaseRequest request) throws Exception {

        // requiredField add
        request.addRequiredField("entId", request.TYPE_STRING);
        request.addRequiredField("prodId", request.TYPE_STRING);

        request.validRequiredField();

        // bussiness logic
        return stockService.selectStockDetailList(request.bindRequest());
    }


    /**
     * 재고 바코드 조회
     */
    @PostMapping(value = "selectBarNumList")
    public BaseResponse selectBarNumList(@RequestBody BaseRequest request) throws Exception {

        // requiredField add
//        request.addRequiredField("usrId", request.TYPE_STRING);
        request.addRequiredField("prods", request.TYPE_LIST);

        request.validRequiredField();

        // bussiness logic
        return stockService.selectBarNumList(request.bindRequest());
    }


    /**
     * 재고 추가
     */
    @PostMapping(value = "insert")
    public BaseResponse insertStock(@RequestBody BaseRequest request) throws Exception {

        // requiredField add
        request.addRequiredField("entId", request.TYPE_STRING);
        request.addRequiredField("usrId", request.TYPE_STRING);
        request.validRequiredField();

        List<Map> prods = new ArrayList<>();

        if (request.getParams().containsKey("prods")) {
            prods = new ObjectMapper().convertValue(request.getParams().get("prods"), List.class);
            if (prods == null) throw new Exception("Request is null");
            for (Map prod : prods) {
                BaseRequest prodRequest = new BaseRequest(prod);

                prodRequest.addRequiredField("prodId", BaseRequest.TYPE_STRING);
                // requiredField check
                prodRequest.validRequiredField();
            }
        } else {
            throw new Exception("requiredField [prods] is not contain ");
        }


        // bussiness logic
        return stockService.insertStock(request.bindRequest());
    }


    /**
     * 재고 수정
     */
    @PostMapping(value = "update")
    public BaseResponse updateStock(@RequestBody BaseRequest request) throws Exception {

        // requiredField add
        request.addRequiredField("usrId", request.TYPE_STRING);
        request.validRequiredField();

        List<Map> prods = new ArrayList<>();

        if (request.getParams().containsKey("prods")) {
            prods = new ObjectMapper().convertValue(request.getParams().get("prods"), List.class);
            if (prods == null) throw new Exception("[prods] Request is null");
            for (Map prod : prods) {
                BaseRequest prodRequest = new BaseRequest(prod);

                prodRequest.addRequiredField("stoId", BaseRequest.TYPE_STRING);
                // requiredField check
                prodRequest.validRequiredField();
            }
        }

        // bussiness logic
        return stockService.updateStock(request.bindRequest());
    }


    /**
     * 재고 삭제
     */
    @PostMapping(value = "deleteMulti")
    public BaseResponse deleteStockMulti(@RequestBody BaseRequest request) throws Exception {

        // requiredField add
        request.addRequiredField("stoId", request.TYPE_LIST);

        request.validRequiredField();

        // bussiness logic
        return stockService.deleteStockMulti(request.bindRequest());
    }
}
