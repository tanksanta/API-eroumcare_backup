package kr.co.thkc.service;

import kr.co.thkc.dispatch.BaseResponse;
import kr.co.thkc.dispatch.ResultCode;
import kr.co.thkc.mapper.AbstractDAO;
import kr.co.thkc.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@Transactional(rollbackFor = {Exception.class})
public class StockService extends BaseService{

    @Autowired
    private Environment env;

    @Autowired
    private AbstractDAO abstractDAO;

    /**
     * 바코드 중복 체크
     * */
    public int countBarNum(Map param) {

        if ((MapUtils.getString(param, "prodBarNum") != null && !MapUtils.getString(param, "prodBarNum").equals(""))
                && (MapUtils.getString(param, "stoId") != null && !MapUtils.getString(param, "stoId").equals(""))) {
            List barNumList = abstractDAO.selectList("stock.selectDuplicateBarNum", param);
            return barNumList.size();
        } else {
            return 0;
        }
    }

    
    /**
     * 재고 목록 조회
     * */
    public BaseResponse selectStockList(Map<String,Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        //결과 리스트
        List resultList = new ArrayList<>();
        //제품목록
        List<Map> prodList = (List<Map>) MapUtils.getObject(params,"prods");
        for (Map prod:prodList){
            prod.put("usrId",MapUtils.getString(params,"usrId"));
            prod.put("stateCd",MapUtils.getString(params,"stateCd"));
            List result = abstractDAO.selectList("stock.selectStockList",prod);
            resultList = (List) Stream.concat(resultList.stream(),result.stream())
                                    .collect(Collectors.toList());
        }

        response.setResultData(resultList);
        response.setResult(ResultCode.RC_OK);

        return response;
    }

    /**
     * 사업소별 재고 목록 조회
     * */
    public BaseResponse selectStockListForEnt(Map<String,Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        //페이징 처리
        int pageNum = MapUtils.getIntValue(params,"pageNum");
        int pageSize = MapUtils.getIntValue(params,"pageSize");

        int offset = pageSize * (pageNum - 1);

        params.put("pageSize",pageSize);
        params.put("pageNum",pageNum);
        params.put("offset",offset);

        //제품목록
        Integer total = (Integer)abstractDAO.selectOne("stock.selectStockListForEntCnt",params);
        List result = abstractDAO.selectList("stock.selectStockListForEnt",params);

        response.setTotal(total==null ? 0 : total);
        response.setResultData(result);
        response.setResult(ResultCode.RC_OK);

        return response;
    }

    /**
     * 재고목록 상세 조회
     * */
    public BaseResponse selectStockDetailList(Map<String,Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        //페이징 처리
        int pageNum = MapUtils.getIntValue(params,"pageNum");
        int pageSize = MapUtils.getIntValue(params,"pageSize");
        int offset = pageSize * (pageNum - 1);

        params.put("pageSize",pageSize);
        params.put("pageNum",pageNum);
        params.put("offset",offset);

        params.put("downloadUrl",env.getProperty("download.url"));

        //제품목록
        Integer total = (Integer)abstractDAO.selectOne("stock.selectStockDetailListCnt",params);
        List result = abstractDAO.selectList("stock.selectStockDetailList",params);

        response.setTotal(total==null ? 0 : total);
        response.setResultData(result);
        response.setResult(ResultCode.RC_OK);

        return response;
    }


    
    /**
     * 재고 바코드 조회
     * */
    public BaseResponse selectBarNumList(Map<String,Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        //결과 리스트
        List orderList = new ArrayList<>();
        //제품목록
        List<Map> prodList = (List<Map>) MapUtils.getObject(params,"prods");
        for (Map prod:prodList){
            prod.put("usrId",MapUtils.getString(params,"usrId"));
            List<Map> barNumMapList = abstractDAO.selectList("stock.selectBarNumList",prod);
            List<String> barNumList = new ArrayList<>();
            for(Map barNumMap:barNumMapList){
                String prodBarNum = MapUtils.getString(barNumMap,"prodBarNum");
                barNumList.add(prodBarNum);
            }
            prod.put("prodBarNumList",barNumList);
            orderList.add(prod);
        }

        response.setResultData(orderList);
        response.setResult(ResultCode.RC_OK);

        return response;
    }


    /**
     * 재고 등록
     * */
    public BaseResponse insertStock(Map<String,Object> params) throws Exception {
        BaseResponse response = new BaseResponse();

        //결과를 위한 파라미터 리스트
        List stockList = new ArrayList();

        //취급제품 체크용
        Object ppcId = "";
        //제품 리스트 가져오기
        List<Map> prodList = (List<Map>) MapUtils.getObject(params,"prods");
        //prodId,prodColor,prodSize,prodManuDate,prodBarNum,stoMemo,delYn
        int index = 0;
        for(int i=0;  i<prodList.size(); i++){
            Map prod = new HashMap();
            prod.put("entId",MapUtils.getString(params,"entId"));
            prod.put("usrId",MapUtils.getString(params,"usrId"));
            prod.put("accessIp",MapUtils.getString(params,"accessIp"));
            prod.put("prodId",MapUtils.getString(prodList.get(i),"prodId"));
            prod.put("prodColor",MapUtils.getString(prodList.get(i),"prodColor"));
            prod.put("prodSize",MapUtils.getString(prodList.get(i),"prodSize"));
            prod.put("prodManuDate",MapUtils.getString(prodList.get(i),"prodManuDate"));
            prod.put("prodBarNum",MapUtils.getString(prodList.get(i),"prodBarNum"));
            prod.put("stoMemo",MapUtils.getString(prodList.get(i),"stoMemo"));
            prod.put("ct_id",MapUtils.getString(prodList.get(i),"ct_id"));

            //취급제품인지 체크
            ppcId = abstractDAO.selectOne("stock.selectPpc",prod);
            if(ppcId==null){    //취급제품 없으면 추가
                prod.put("ppcId",newPpcId());
                abstractDAO.insert("prod.insertPpc",prod);
            }else{
                prod.put("ppcId",ppcId.toString());
            }

            // 재고 등록
            String stoId = newStoId();
            String stoNew = stoId.substring(0,stoId.length()-5) +
                    String.format("%05d",Integer.parseInt(stoId.substring(stoId.length()-5))+index);
            log.debug(stoNew);
            prod.put("stoId",stoNew);
            if(countBarNum(prod) > 0) {
                throw new Exception("(prodBarNum=" + MapUtils.getString(prod,"prodBarNum") + ") 중복된 바코드는 사용할 수 없습니다.");
            }else {
                //결과 리턴에 추가
                stockList.add(prod);
                index++;
            }
        }
        if(stockList.size()==0) throw new Exception("추가된 재고가 없습니다.");

        abstractDAO.insert("stock.insertStock_multi",stockList);

        response.setResultData(stockList);
        response.setResult(ResultCode.RC_OK);

        return response;
    }

    
    /**
     * 재고 수정
     * */
    public BaseResponse updateStock(Map<String,Object> params) throws Exception {
        BaseResponse response = new BaseResponse();

        //결과값 리턴을 위한
        List<Map> stockList = new ArrayList<>();

        //수정할 재고 가져오기
        List<Map> prodList = (List<Map>) MapUtils.getObject(params,"prods");
        //stoId,prodColor,prodSize,prodManuDate,prodBarNum,stoMemo,delYn
        if(prodList!=null) {
            for (Map prod : prodList) {
                prod.put("usrId", MapUtils.getString(params, "usrId"));
                prod.put("accessIp", MapUtils.getString(params, "accessIp"));

                // 재고 수정
                abstractDAO.update("stock.updateStock", prod);
                //트랜잭션 때문에 이전에 동일 트랜잭션에서 바코드가 들어갔었을수도있음 그래서 트랜잭션 마지막에 개수체크
                String prodBarNum = MapUtils.getString(prod, "prodBarNum");
                if (countBarNum(prod) > 1) throw new Exception("(prodBarNum=" + prodBarNum + ") 중복된 바코드는 사용할 수 없습니다.");
                //결과 리턴에 추가
                stockList.add(prod);
            }
        }

        response.setResult(ResultCode.RC_OK);
        if(stockList.size()==0) {
            response.setMessage("수정된 재고가 없습니다.");
        }else {
            response.setResultData(stockList);
        }


        return response;
    }


    /**
     * 재고 삭제
     * */
    public BaseResponse deleteStockMulti(Map<String,Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        abstractDAO.delete("stock.deleteStock",params);

        response.setResult(ResultCode.RC_OK);

        return response;
    }


}
