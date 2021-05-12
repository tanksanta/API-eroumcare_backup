package kr.co.thkc.service;

import kr.co.thkc.dispatch.BaseResponse;
import kr.co.thkc.dispatch.ResultCode;
import kr.co.thkc.mapper.AbstractDAO;
import kr.co.thkc.vo.FileVO;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.util.*;


@Slf4j
@Service
@Transactional(rollbackFor = {Exception.class})
public class ProdService extends BaseService{

    @Autowired
    private AbstractDAO abstractDAO;

    @Autowired
    private FileService fileService;



    /*
    * ------------------------------------------------------
    *                       상품
    * ------------------------------------------------------
    * */
    public BaseResponse selectProdList(Map<String,Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        List prodList = abstractDAO.selectList("prod.selectProdList",params);

        response.setResultData(prodList);
        response.setResult(ResultCode.RC_OK);

        return response;
    }

    public BaseResponse selectItemList(Map<String,Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        List prodList = abstractDAO.selectList("prod.selectItemList",params);

        response.setResultData(prodList);
        response.setResult(ResultCode.RC_OK);

        return response;
    }


    public BaseResponse insertProd(Map<String,Object> params, Map<String,MultipartFile> fileMap) throws Exception {
        BaseResponse response = new BaseResponse();

        String prodDetail = MapUtils.getString(params,"prodDetail");
        params.put("prodDetail",prodDetail);

        String atchFileId = newAtchFileId();
        String prodId = newProdId();

        //무게값 없을떄 0
        String prodWeig = MapUtils.getString(params,"prodWeig");
        if(prodWeig==null || prodWeig.equals("")) prodWeig = "0";
        //상태값 없을떄 승인 요청
        String prodStateCode = MapUtils.getString(params,"prodStateCode");
        if(prodStateCode==null || prodStateCode.equals("")) prodStateCode = "01";

        //실제 저장소에 파일을 등록하고 FileVO 리턴
        List<FileVO> fileVOList = fileService.parseFileInfo(fileMap, "OSL_", 0, atchFileId,"","prodImg");
        //파일 db에 저장
        fileService.insertFileInfoList(fileVOList);


        params.put("prodId",prodId);
        params.put("prodWeig",prodWeig);
        params.put("prodStateCode",prodStateCode);
        params.put("prodImgAttr", atchFileId);


        //제품추가
        abstractDAO.insert("prod.insertProd",params);
        //제품수정정보추가
        abstractDAO.insert("prod.insertProdModify",params);

        response.setData(params);
        response.setResult(ResultCode.RC_OK);

        return response;
    }


    public BaseResponse updateProd(Map<String,Object> params, Map<String,MultipartFile> fileMap) throws Exception {
        BaseResponse response = new BaseResponse();

        String prodDetail = MapUtils.getString(params,"prodDetail");
        params.put("prodDetail",prodDetail);

        String atchFileId = "";

        //변경전 제품정보 조회
        Map prodInfo = (Map) abstractDAO.selectOne("prod.selectProdList",params);

        //첨부된 파일이 존재할 경우
        if(!fileMap.isEmpty()){
            String prodImgAttr = MapUtils.getString(prodInfo,"prodImgAttr");
            //변경전 제품정보에서 이미지가 있는 경우
            if(prodImgAttr != null && !prodImgAttr.equals("")){
                //이전 이미지 조회해서 삭제처리
                List<Map> beforeFileList = abstractDAO.selectList("file.selectFileList",params);
                for(Map beforeFile:beforeFileList){
                    String beforeFilePath = MapUtils.getString(beforeFile,"fileStreCours");
                    String beforeFileName = MapUtils.getString(beforeFile,"streFileNm");
                    fileService.deleteFile(beforeFilePath+beforeFileName);
                }
                //DB에서 파일정보 삭제처리
                abstractDAO.update("file.deleteCOMTNFILE",prodImgAttr);
            }
            //새 파일 아이디
            atchFileId = newAtchFileId();
            //실제 저장소에 파일을 등록하고 FileVO 리턴
            List<FileVO> fileVOList = fileService.parseFileInfo(fileMap, "OSL_", 0, atchFileId,"","prodImg");
            //파일 db에 저장
            fileService.insertFileInfoList(fileVOList);
        }
        if(!atchFileId.equals("")) params.put("prodImgAttr",atchFileId);

        //제품추가
        abstractDAO.update("prod.updateProd",params);
        //제품수정정보추가
        abstractDAO.update("prod.updateProdModify",params);

        response.setResult(ResultCode.RC_OK);

        return response;
    }


    public BaseResponse deleteProd(Map<String,Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        abstractDAO.delete("prod.deleteProd",params);

        response.setResult(ResultCode.RC_OK);

        return response;
    }


    /*
    * ------------------------------------------------------
    *                       취급상품
    * ------------------------------------------------------
    * */
    public BaseResponse selectProdPpcList(Map<String,Object> params) throws SQLException {
        BaseResponse response = new BaseResponse();

        List ppcList = abstractDAO.selectList("prod.selectProdPpcList",params);

        response.setResultData(ppcList);
        response.setResult(ResultCode.RC_OK);

        return response;
    }

    public BaseResponse insertPpc(Map<String,Object> params) throws Exception {
        BaseResponse response = new BaseResponse();

        params.put("ppcId",newPpcId());
        //취급제품 추가
        abstractDAO.insert("prod.insertPpc",params);

        response.setData(params);
        response.setResult(ResultCode.RC_OK);

        return response;
    }

    public BaseResponse deletePpc(Map<String,Object> params) throws Exception {
            BaseResponse response = new BaseResponse();

            //취급제품 삭제
            abstractDAO.update("prod.deletePpc",params);

            response.setResult(ResultCode.RC_OK);

            return response;
        }

}