package kr.co.thkc.service;

import kr.co.thkc.dispatch.BaseResponse;
import kr.co.thkc.dispatch.ResultCode;
import kr.co.thkc.mapper.AbstractDAO;
import kr.co.thkc.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.*;


@Slf4j
@Service
@Transactional(rollbackFor = {Exception.class})
public class OrderService extends BaseService {

    @Autowired
    private Environment env;

    @Autowired
    private AbstractDAO abstractDAO;

    @Autowired
    private StockService stockService;

    @Autowired
    private EformService eformService;

    @Autowired
    private ProdService prodService;


    /**
     * 주문 조회
     * */
    public BaseResponse selectOrderList(Map<String,Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        params.put("downloadUrl",env.getProperty("download.url"));
        List orderList = abstractDAO.selectList("order.selectOrderList",params);

        response.setResultData(orderList);
        response.setResult(ResultCode.RC_OK);

        return response;
    }


    /**
     * 주문 등록
     * */
    public BaseResponse insertOrder(Map<String,Object> params) throws Exception {
        BaseResponse response = new BaseResponse();

        //EntId 조회
        Map account = (Map)abstractDAO.selectOne("ent.selectEntAccount",params);
        String entId = MapUtils.getString(account,"entId");
        String accessIp = MapUtils.getString(account,"accessIp");
        String usrId = MapUtils.getString(params,"usrId");
        String penId = MapUtils.getString(params,"penId");
        String delGbnCd = MapUtils.getString(params,"delGbnCd");
        String ordWayNum = MapUtils.getString(params,"ordWayNum");
        String ordNm = MapUtils.getString(params,"ordNm");
        String ordCont = MapUtils.getString(params,"ordCont");
        String ordMeno = MapUtils.getString(params,"ordMeno");
        String ordZip = MapUtils.getString(params,"ordZip");
        String ordAddr = MapUtils.getString(params,"ordAddr");
        String ordAddrDtl = MapUtils.getString(params,"ordAddrDtl");
        String payMehCd = MapUtils.getString(params,"payMehCd");
        String uuid = UUID.randomUUID().toString();         //전자계약서 UUID 생성
        String extnYn = MapUtils.getString(params,"extnYn");
        String prodDetail = MapUtils.getString(params,"prodDetail");
        String returnUrl = MapUtils.getString(params,"returnUrl");
        String penOrdId = newPenOrdId();         //주문 PenOrdId 생성

        //수급자별 할인율 계산 후 최종결제금액 저장
        Integer discount = (Integer) abstractDAO.selectOne("order.selectDiscount",params);

        prodDetail = StringEscapeUtils.unescapeHtml4(prodDetail);  //escape문자 변형
        returnUrl = StringEscapeUtils.unescapeHtml4(returnUrl);

        if(extnYn==null || extnYn.equals("")) extnYn="N";           //연장대여 기본값

        //파라미터 세팅
        params.put("uuid",uuid);
        params.put("penOrdId",penOrdId);
        params.put("entId",entId);
        params.put("extnYn",extnYn);
        params.put("prodDetail",prodDetail);
        params.put("returnUrl",returnUrl);


        //추가해야할 재고가 있을때 사용할 신규재고 목록
        List<Map> newStockList = new ArrayList();
        List<Map> orderList = new ArrayList();

        int penStaSeq=1;    //주문등록 시퀀스
        List<Map> prodList = (List<Map>) MapUtils.getObject(params,"prods");
        for(int i=0;  i<prodList.size(); i++){
            Map prod = new HashMap(prodList.get(i));
            //상품정보 가져오기 gubun,prodNm,itemId,subItem,prodSupPrice,prodOflPrice,rentalPrice
            Map prodInfo = (Map) abstractDAO.selectOne("prod.selectProdNonOptionInfo",prod);
            prod.putAll(prodInfo);
            prod.put("itemNm",MapUtils.getString(prodInfo,"itemNm"));
            prod.remove("penStaSeq");

            //주문 등록 파람설정
            Map newOrderMap = new HashMap();
            newOrderMap.put("entId",entId);
            newOrderMap.put("accessIp",accessIp);
            newOrderMap.put("usrId",usrId);
            newOrderMap.put("penId",penId);
            newOrderMap.put("delGbnCd",delGbnCd);
            newOrderMap.put("ordWayNum",ordWayNum);
            newOrderMap.put("ordNm",ordNm);
            newOrderMap.put("ordCont",ordCont);
            newOrderMap.put("ordMeno",ordMeno);
            newOrderMap.put("ordZip",ordZip);
            newOrderMap.put("ordAddr",ordAddr);
            newOrderMap.put("ordAddrDtl",ordAddrDtl);
            newOrderMap.put("payMehCd",payMehCd);
            newOrderMap.put("uuid",uuid);
            newOrderMap.put("penOrdId",penOrdId);
            newOrderMap.put("extnYn",extnYn);
            newOrderMap.put("prodDetail",prodDetail);
            newOrderMap.put("ordStatus",MapUtils.getString(prod,"gubun"));
            //쇼핑몰의 재고값 매핑을 위한 id
            newOrderMap.put("ct_id",MapUtils.getString(prod,"ct_id"));
            newOrderMap.putAll(prod);

            Map stock = new HashMap();
            //바코드가 없을때 재고추가
            String prodBarNum = MapUtils.getString(prod,"prodBarNum");
            if(prodBarNum == null || prodBarNum.equals("")){
                newStockList.add(prod);
            }else {
                //재고 조회
                List<Map> selectStockList = abstractDAO.selectList("stock.selectStockInfoList", prod);
                if (selectStockList.size() == 1) {
                    stock = selectStockList.get(0);
                    newOrderMap.putAll(stock);
                } else {
                    //바코드로 조회되는 재고가 없을때 재고추가
                    newStockList.add(prod);
                }
            }

            orderList.add(newOrderMap);
        }
        //추가할 재고가 있으면 추가
        List<Map> insertStockList = new ArrayList();
        if(newStockList!=null && newStockList.size() > 0) {
            //재고 변경, 등록 파라미터 세팅
            Map insertStockParam = new HashMap();
            insertStockParam.put("accessIp", accessIp);
            insertStockParam.put("usrId", usrId);
            insertStockParam.put("entId", entId);
            insertStockParam.put("prods", newStockList);
            insertStockList = (List<Map>) stockService.insertStock(insertStockParam).getData();
        }

        //금액추가 및 시퀀스 등록
        List<Map> resultStock = new ArrayList();
        for(Map order:orderList){

            //재고 등록하고 리턴값으로 재고정보를 주므로 그대로 업데이트할 재고목록에 추가
            if(MapUtils.getString(order,"stoId")==null || MapUtils.getString(order,"stoId").equals("")){
                order.putAll(newStockList.remove(0));
                order.putAll(insertStockList.remove(0));
            }

            int finPayment = MapUtils.getIntValue(order, "prodOflPrice"); //판매가
            int price = 0; //판매가 (수급자에 부과되는 가격)
            double rentalCnt = 1;
            if(MapUtils.getString(order,"ordStatus").equals("00")) {
                //판매일때
                price = MapUtils.getIntValue(order, "prodSupPrice"); //급여가
            }else{
                //대여일때
                String ordLendStrDtm = MapUtils.getString(order,"ordLendStrDtm");       //수정 하려는 대여일자
                String ordLendEndDtm = MapUtils.getString(order,"ordLendEndDtm");       //수정 하려는 대여일자
                price = MapUtils.getIntValue(order, "rentalPrice");
                //대여일자 계산해서 결제금액 계산
                if(ordLendStrDtm!=null && ordLendEndDtm!=null) {
                    rentalCnt = DateUtil.getDateDiffOrder(ordLendStrDtm, ordLendEndDtm);
                }
                price = (int)(price * rentalCnt);
                finPayment = (int)(finPayment * rentalCnt);
            }
            order.put("prodOflPrice", price);
            order.put("finPayment", finPayment);

            //주문시퀀스 등록
            order.put("penStaSeq",penStaSeq++);

            resultStock.add(new HashMap(){{
                put("stoId",order.get("stoId"));
                put("ct_id",order.get("ct_id"));
            }});
        }

        //주문 추가
        abstractDAO.insert("order.insertOrder_multi", orderList);

        if(discount!=null && discount==0){
            abstractDAO.insert("recipient.insertRecipientReq",params);
        }
        //기준년도, 기준월
        Calendar cal = Calendar.getInstance();
        params.put("baseYear",String.valueOf(cal.get(Calendar.YEAR)));
        params.put("baseMonth",String.format("%02d",cal.get(Calendar.MONTH)+1));
        //전자계약서
        eformService.insertEform(params);

        Map resultData = new HashMap(){{
            put("uuid",uuid);
            put("penOrdId",penOrdId);
            put("stockList",resultStock);
        }};
        response.setData(resultData);
        response.setResult(ResultCode.RC_OK);

        return response;
    }


    /**
     * 주문 수정
     * */
    public BaseResponse updateOrder(Map<String,Object> params) throws Exception {
        BaseResponse response = new BaseResponse();

        String penOrdId = MapUtils.getString(params,"penOrdId");        //수정 하려는 주문아이디
        String staOrdCd = MapUtils.getString(params,"staOrdCd");        //수정 하려는 주문상태
        String accessIp = MapUtils.getString(params,"accessIp");       //수정 하려는 주문상태
        String usrId = MapUtils.getString(params,"usrId");       //수정 하려는 주문상태

        //현재 주문 품목
        List<Map> ordList = abstractDAO.selectList("order.selectOrderList",params);
        if(ordList.size() < 1) throw new Exception("penOrdId("+penOrdId+") 주문이 존재하지 않습니다.");
        //주문 수정
        abstractDAO.update("order.updateOrder", params);
        //수정할 주문 품목
        List<Map> resultStock = new ArrayList();
        List<Map> prodList = MapUtils.getObject(params,"prods")!=null?(List<Map>)MapUtils.getObject(params,"prods"):null;
        if(prodList!=null) {
            //수정 하려는 재고
            for (Map prod : prodList) {
                prod.put("accessIp",accessIp);
                prod.put("usrId",usrId);
                prod.put("penOrdId", penOrdId);
                //주문 수정
                abstractDAO.update("order.updateOrder", prod);

                String stoId = MapUtils.getString(prod, "stoId"); //재고 아이디
                String prodBarNum = MapUtils.getString(prod,"prodBarNum"); //바코드
                //주문상태를 완료로 변경시에 재고상태를 주문완료로 변경함
                String stateCd = (staOrdCd!=null && staOrdCd.equals("03")) ? "02" : MapUtils.getString(prod,"stateCd");
                Map stoParam = new HashMap(){{
                    put("accessIp",accessIp);
                    put("usrId",usrId);
                    put("stoId", stoId);
                    put("stateCd", stateCd);
                    put("prodBarNum",prodBarNum);
                }};
                //재고 수정
                abstractDAO.update("stock.updateStock", stoParam);
                //트랜잭션 때문에 이전에 동일 트랜잭션에서 바코드가 들어갔었을수도있음 그래서 트랜잭션 마지막에 개수체크
                if(stockService.countBarNum(prod) > 1) throw new Exception("(prodBarNum=" + prodBarNum + ") 중복된 바코드는 사용할 수 없습니다.");
                else{
                    resultStock.add(new HashMap(){{
                        put("stoId",stoId);
                        put("ct_id",prod.get("ct_id"));
                    }});
                }
            }
        }

        Map resultData = new HashMap(){{
            put("penOrdId",penOrdId);
            put("stockList",resultStock);
        }};
        response.setData(resultData);
        response.setResult(ResultCode.RC_OK);

        return response;
    }

    /**
     * 주문 삭제
     * */
    public BaseResponse deleteOrder(Map<String,Object> params) throws Exception {
        BaseResponse response = new BaseResponse();

        //현재 주문 품목
        List<Map> ordList = abstractDAO.selectList("order.selectOrderList",params);
        if(ordList.size() < 1) throw new Exception("주문이 존재하지 않습니다.");

        for(Map ord:ordList) {
            Map stoParam = new HashMap();
            stoParam.put("accessIp", MapUtils.getString(params, "accessIp"));
            stoParam.put("usrId", MapUtils.getString(params, "usrId"));
            stoParam.put("stoId", MapUtils.getString(ord, "stoId"));

            String stateCd = "01";
            //바코드가 업으면 재고대기 상태로 돌아간다.
            String prodBarNum = MapUtils.getString(ord, "prodBarNum");
            if (prodBarNum == null || prodBarNum.equals("")) stateCd = "06";
            stoParam.put("stateCd", stateCd);
            //재고 수정
            abstractDAO.update("stock.updateStock", stoParam);
        }
        abstractDAO.delete("order.deleteOrder", params);

        response.setResult(ResultCode.RC_OK);

        return response;
    }
}
