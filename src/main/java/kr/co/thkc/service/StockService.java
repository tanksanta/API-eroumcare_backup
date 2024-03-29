package kr.co.thkc.service;

import kr.co.thkc.dispatch.BaseResponse;
import kr.co.thkc.dispatch.ResultCode;
import kr.co.thkc.mapper.AbstractDAO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@Transactional(rollbackFor = {Exception.class})
public class StockService extends BaseService {

    @Autowired
    private Environment env;

    @Autowired
    private AbstractDAO abstractDAO;

    @Autowired
    private ProdService prodService;


    /**
     * 바코드 중복 체크
     */
    public int countBarNum(Map param) throws Exception {

        if ((MapUtils.getString(param, "prodBarNum") != null && !MapUtils.getString(param, "prodBarNum").equals(""))
                && (MapUtils.getString(param, "prodId") != null && !MapUtils.getString(param, "prodId").equals(""))
                && (MapUtils.getString(param, "entId") != null && !MapUtils.getString(param, "entId").equals(""))) {
            List barNumList = abstractDAO.selectList("stock.selectDuplicateBarNum", param);
            return barNumList.size();
        } else if (MapUtils.getString(param, "prodBarNum") == null || MapUtils.getString(param, "prodBarNum").equals("")) {
            return 0;
        } else {
            throw new Exception("바코드 중복 조회 필수 값이 누락되었습니다. (필수 : 바코드, 제품 고유 아이디, 사업소 고유 아이디)");
        }
    }


    /**
     * 재고 목록 조회
     */
    public BaseResponse selectStockList(Map<String, Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        //결과 리스트
        List resultList = new ArrayList<>();
        //제품목록
        List<Map> prodList = (List<Map>) MapUtils.getObject(params, "prods");
        for (Map prod : prodList) {
            prod.put("usrId", MapUtils.getString(params, "usrId"));
            prod.put("stateCd", MapUtils.getString(params, "stateCd"));
            List result = abstractDAO.selectList("stock.selectStockList", prod);
            resultList = (List) Stream.concat(resultList.stream(), result.stream())
                    .collect(Collectors.toList());
        }

        response.setResultData(resultList);
        response.setResult(ResultCode.RC_OK);

        return response;
    }

    /**
     * 사업소별 재고 목록 조회
     */
    public BaseResponse selectStockListForEnt(Map<String, Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        //페이징 처리
        int pageNum = MapUtils.getIntValue(params, "pageNum");
        int pageSize = MapUtils.getIntValue(params, "pageSize");

        int offset = pageSize * (pageNum - 1);

        params.put("pageSize", pageSize);
        params.put("pageNum", pageNum);
        params.put("offset", offset);

        //제품목록
        Integer total = (Integer) abstractDAO.selectOne("stock.selectStockListForEntCnt", params);
        List result = abstractDAO.selectList("stock.selectStockListForEnt", params);

        response.setTotal(total == null ? 0 : total);
        response.setResultData(result);
        response.setResult(ResultCode.RC_OK);

        return response;
    }

    /**
     * 사업소별 재고 목록 조회
     */
    public BaseResponse selectStockNotEmptyListForEnt(Map<String, Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        //페이징 처리
        int pageNum = MapUtils.getIntValue(params, "pageNum");
        int pageSize = MapUtils.getIntValue(params, "pageSize");

        int offset = pageSize * (pageNum - 1);

        params.put("pageSize", pageSize);
        params.put("pageNum", pageNum);
        params.put("offset", offset);

        //제품목록
        Integer total = (Integer) abstractDAO.selectOne("stock.selectStockNotEmptyListForEntCnt", params);
        List result = abstractDAO.selectList("stock.selectStockNotEmptyListForEnt", params);

        response.setTotal(total == null ? 0 : total);
        response.setResultData(result);
        response.setResult(ResultCode.RC_OK);

        return response;
    }

    /**
     * 재고목록 상세 조회
     */
    public BaseResponse selectStockDetailList(Map<String, Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        //페이징 처리
        int pageNum = MapUtils.getIntValue(params, "pageNum");
        int pageSize = MapUtils.getIntValue(params, "pageSize");
        int offset = pageSize * (pageNum - 1);

        params.put("pageSize", pageSize);
        params.put("pageNum", pageNum);
        params.put("offset", offset);

        params.put("downloadUrl", env.getProperty("download.url"));

        //제품목록
        Integer total = (Integer) abstractDAO.selectOne("stock.selectStockDetailListCnt", params);
        List result = abstractDAO.selectList("stock.selectStockDetailList", params);

        response.setTotal(total == null ? 0 : total);
        response.setResultData(result);
        response.setResult(ResultCode.RC_OK);

        return response;
    }


    /**
     * 재고 바코드 조회
     */
    public BaseResponse selectBarNumList(Map<String, Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        //결과 리스트
        List orderList = new ArrayList<>();
        //제품목록
        List<Map> prodList = (List<Map>) MapUtils.getObject(params, "prods");
        for (Map prod : prodList) {
            prod.put("usrId", MapUtils.getString(params, "usrId"));
            List<Map> barNumMapList = abstractDAO.selectList("stock.selectBarNumList", prod);
            List<String> barNumList = new ArrayList<>();
            for (Map barNumMap : barNumMapList) {
                String prodBarNum = MapUtils.getString(barNumMap, "prodBarNum");
                barNumList.add(prodBarNum);
            }
            prod.put("prodBarNumList", barNumList);
            orderList.add(prod);
        }

        response.setResultData(orderList);
        response.setResult(ResultCode.RC_OK);

        return response;
    }


    /**
     * 재고 등록
     */
    public BaseResponse insertStock(Map<String, Object> params) throws Exception {
        BaseResponse response = new BaseResponse();

        //결과를 위한 파라미터 리스트
        List stockList = new ArrayList();

        String accessIp = MapUtils.getString(params, "accessIp");
        String usrId = MapUtils.getString(params, "usrId");
        String entId = MapUtils.getString(params, "entId");

        //취급제품 체크용
        Object ppcId = "";
        //제품 리스트 가져오기
        List<Map> prodList = (List<Map>) MapUtils.getObject(params, "prods");
        //prodId,prodColor,prodSize,prodManuDate,prodBarNum,stoMemo,delYn
        int index = 0;
        for (int i = 0; i < prodList.size(); i++) {
            Map prod = new HashMap();
            prod.put("entId", entId);
            prod.put("usrId", usrId);
            prod.put("accessIp", accessIp);
            prod.put("prodId", MapUtils.getString(prodList.get(i), "prodId"));
            prod.put("prodManuDate", MapUtils.getString(prodList.get(i), "prodManuDate"));
            prod.put("prodBarNum", MapUtils.getString(prodList.get(i), "prodBarNum"));
            prod.put("stoMemo", MapUtils.getString(prodList.get(i), "stoMemo"));
            prod.put("ct_id", MapUtils.getString(prodList.get(i), "ct_id"));

            // 옵션
            prod.put("prodColor", MapUtils.getString(prodList.get(i), "prodColor"));
            prod.put("prodSize", MapUtils.getString(prodList.get(i), "prodSize"));
            prod.put("prodOption", MapUtils.getString(prodList.get(i), "prodOption"));

            // 상태
            String stateCd = MapUtils.getString(prodList.get(i), "stateCd");
            if (stateCd == null || stateCd.equals("")) {
                prod.put("stateCd", "06");
            } else {
                prod.put("stateCd", stateCd);
            }

            //취급제품인지 체크
            ppcId = abstractDAO.selectOne("stock.selectPpc", prod);
            if (ppcId == null) {    //취급제품 없으면 추가
                prod.put("ppcId", newPpcId());
                abstractDAO.insert("prod.insertPpc", prod);
            } else {
                prod.put("ppcId", ppcId.toString());
            }

            // 재고 등록
            String stoId = newStoId();
            String stoNew = stoId.substring(0, stoId.length() - 5) +
                    String.format("%05d", Integer.parseInt(stoId.substring(stoId.length() - 5)) + index);
            log.debug(stoNew);
            prod.put("stoId", stoNew);
            if (countBarNum(prod) > 0) {
                throw new Exception("(prodBarNum=" + MapUtils.getString(prod, "prodBarNum") + ") 중복된 바코드는 사용할 수 없습니다.");
            } else {
                //결과 리턴에 추가
                stockList.add(prod);
                index++;
            }
        }
        if (stockList.size() == 0) throw new Exception("추가된 재고가 없습니다.");

        try {
          abstractDAO.insert("stock.insertStock_multi", stockList);
        } catch(Exception e) {
          throw new Exception("재고 추가 도중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        }

        response.setResultData(stockList);
        response.setResult(ResultCode.RC_OK);

        return response;
    }


    /**
     * 재고 수정
     */
    public BaseResponse updateStock(Map<String, Object> params) throws Exception {
        BaseResponse response = new BaseResponse();

        String usrId = MapUtils.getString(params, "usrId");
        String accessIp = MapUtils.getString(params, "accessIp");
        String stoId = MapUtils.getString(params, "stoId");
        String entId = MapUtils.getString(params, "entId");
        String ppcId; // 취급제품 ID

        //결과값 리턴을 위한
        List<Map> stockList = new ArrayList<>();
        //수정할 재고 가져오기
        List<Map> prodList = (List<Map>) MapUtils.getObject(params, "prods");
        //stoId,prodColor,prodSize,prodManuDate,prodBarNum,stoMemo,delYn
        if (prodList != null) {
            for (Map prod : prodList) {
                prod.put("usrId", usrId);
                prod.put("accessIp", accessIp);
                prod.put("entId", entId);

                // 상품 아이디 있을 경우 취급제품 ID 검색하여 PPC까지 업데이트 처리 (= 상품 변경 시)
                if (prod.get("prodId") != null && prod.get("prodId").toString().startsWith("PRO")) {
                  ppcId = (String) abstractDAO.selectOne("stock.selectPpc", prod);
                  if (ppcId == null) {    //취급제품 없으면 추가
                    prod.put("ppcId", newPpcId());
                    abstractDAO.insert("prod.insertPpc", prod);
                  } else {
                    prod.put("ppcId", ppcId);
                  }
                }

                // 재고 수정
                abstractDAO.update("stock.updateStock", prod);
                //트랜잭션 때문에 이전에 동일 트랜잭션에서 바코드가 들어갔었을수도있음 그래서 트랜잭션 마지막에 개수체크
                String prodBarNum = MapUtils.getString(prod, "prodBarNum");
                //if (countBarNum(prod) > 1) throw new Exception("(prodBarNum=" + prodBarNum + ") 중복된 바코드는 사용할 수 없습니다.");
                //주문의 재고 수정
                abstractDAO.update("order.updateOrder", new HashMap() {{
                    put("stoId", MapUtils.getString(prod, "stoId"));
                    put("prodBarNum", prodBarNum);
                }});

                //결과 리턴에 추가
                stockList.add(prod);
            }
        }

        response.setResult(ResultCode.RC_OK);
        if (stockList.size() == 0) {
            response.setMessage("수정된 재고가 없습니다.");
        } else {
            response.setResultData(stockList);
        }


        return response;
    }


    /**
     * 재고 삭제
     */
    public BaseResponse deleteStockMulti(Map<String, Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        abstractDAO.delete("stock.deleteStock", params);

        response.setResult(ResultCode.RC_OK);

        return response;
    }

    /**
     * 재고 최초 계약일 현재날짜로 업데이트
     */
    public void updateStockInitialContractDateNow(String stoId) {
        abstractDAO.update("stock.updateStockInitialContractDateNow", stoId);
    }
}
