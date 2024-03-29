package kr.co.thkc.service;

import kr.co.thkc.dispatch.BaseResponse;
import kr.co.thkc.dispatch.ResultCode;
import kr.co.thkc.mapper.AbstractDAO;
import kr.co.thkc.utils.SDBCryptUtil;
import kr.co.thkc.utils.SHA256Util;
import kr.co.thkc.vo.FileVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional(rollbackFor = {Exception.class})
public class EntService extends BaseService {

    @Autowired
    Environment env;

    @Autowired
    private AbstractDAO abstractDAO;

    @Autowired
    private FileService fileService;

    /**
     * 사업소 로그인
     */
    public BaseResponse selectEntLogin(Map<String, Object> params) throws Exception {
        BaseResponse response = new BaseResponse();

        String usrId = MapUtils.getString(params, "usrId");
        String plainPw = MapUtils.getString(params, "pw");
        String pw = SHA256Util.encryptPassword(plainPw, usrId);
        params.put("pw", pw);

        Map result = (Map) abstractDAO.selectOne("ent.selectEntLogin", params);

        if (result == null) throw new Exception("계정 정보가 잘못되었습니다.");
        else {
            Map entInfo = (Map) abstractDAO.selectOne("ent.selectEntAccount", params);
            if (!MapUtils.getString(entInfo, "entConfirmCd").equals("01"))
                throw new Exception("미승인된 사업소 입니다. 관리자에게 문의하세요.");
        }


        response.setResultData(result);
        response.setResult(ResultCode.RC_OK);

        return response;
    }

    /**
     * 사업소 계정 조회
     */
    public BaseResponse selectEntAccount(Map<String, Object> params) throws Exception {
        BaseResponse response = new BaseResponse();

        Map result = (Map) abstractDAO.selectOne("ent.selectEntAccount", params);

        response.setResultData(result);
        response.setResult(ResultCode.RC_OK);

        return response;
    }

    /**
     * 사업소 아이디 중복 조회
     */
    public BaseResponse selectDuplicatedEntCrnCnt(Map<String, Object> params) throws Exception {
        BaseResponse response = new BaseResponse();

        int duplicateEntCrnCnt = (int) abstractDAO.selectOne("ent.selectDuplicatedEntCrnCnt", params);
        if (duplicateEntCrnCnt > 0) {
            throw new Exception("이미 등록된 사업자 번호입니다.");
        }

        response.setMessage("등록가능한 사업자 번호입니다.");
        response.setResult(ResultCode.RC_OK);

        return response;
    }

    /**
     * 관리자 아이디 중복 조회
     */
    public BaseResponse selectDuplicatedEntIdCnt(Map<String, Object> params) throws Exception {
        BaseResponse response = new BaseResponse();

        int duplicateEntCrnCnt = (int) abstractDAO.selectOne("ent.selectDuplicatedEntIdCnt", params);
        if (duplicateEntCrnCnt > 0) {
            throw new Exception("이미 등록된 아이디 번호입니다.");
        }

        response.setMessage("등록가능한 아이디 입니다.");
        response.setResult(ResultCode.RC_OK);

        return response;
    }


    /**
     * 사업소 회원가입
     * *
     */
    public BaseResponse insertEnt(Map<String, Object> params, Map<String, MultipartFile> fileMap) throws Exception {
        BaseResponse response = new BaseResponse();

        int duplicateEntCrnCnt = (int) abstractDAO.selectOne("ent.selectDuplicatedEntCrnCnt", params);
        int duplicateEntIdCnt = (int) abstractDAO.selectOne("ent.selectDuplicatedEntIdCnt", params);
        if (duplicateEntCrnCnt > 0) {
            throw new Exception("이미 등록된 사업자 번호입니다.");
        }
        if (duplicateEntIdCnt > 0) {
            throw new Exception("이미 등록된 아이디 입니다.");
        }

        //카카오맵으로 위도 경도 구하기
        String entAddr = MapUtils.getString(params, "entAddr");
        if (entAddr != null && !entAddr.equals("")) {
            String apiKey = env.getProperty("kakao.apiKey");
            String url = env.getProperty("kakao.localUrl");
            String getUrl = (url + entAddr).replaceAll(" ", "%20");

            Map content = excuteHttpLocalClient(getUrl, apiKey);

            params.put("entLatitude", content.get("latitude"));
            params.put("entLongitude", content.get("longitude"));
        }


        String entId = newEntId();
        String usrId = MapUtils.getString(params, "usrId");
        String plainPw = MapUtils.getString(params, "usrPw");
        String pw = SHA256Util.encryptPassword(plainPw, usrId);

        params.put("usrPw", pw);
        params.put("entId", entId);
        params.put("entConfirmCd", "02");
        params.put("editCd", "02");
        params.put("authCd", "00");
        params.put("type", "01");


        //전화번호 암호화
        SDBCryptUtil sdb = new SDBCryptUtil();

        String usrPnum = MapUtils.getString(params, "usrPnum");
        if (usrPnum != null && !usrPnum.equals("")) params.put("usrPnum", sdb.encrypt(usrPnum));


        //첨부된 파일이 존재할 경우
        if (!fileMap.isEmpty()) {
            if (fileMap.get("sealFile") != null) {
                List<FileVO> fileVoList = new ArrayList<FileVO>();

                String atchFileId = newAtchFileId();
                fileVoList = fileService.parseFileInfSeal(fileMap, 0, atchFileId, "", entId);
                fileService.insertFileInfoList(fileVoList);
                params.put("entSealAttr", atchFileId);
            }
            if (fileMap.get("crnFile") != null) {
                List<FileVO> fileVoList = new ArrayList<FileVO>();

                String atchFileId = newAtchFileId();
                fileVoList = fileService.parseFileInfo(fileMap, "OSL_", 0, atchFileId, "", "crn");
                fileService.insertFileInfoList(fileVoList);
                params.put("entCrnAttr", atchFileId);
            }
        }
        //사업소 추가
        abstractDAO.insert("ent.insertEnt", params);
        //사업소 관리자 추가
        abstractDAO.insert("ent.insertEntAccount", params);
        //알림데이터 추가
        abstractDAO.insert("ent.insertSetAlm", params);

        //메뉴 추가
        abstractDAO.insert("set.insertSetMenuAll", params);
        //시스템 이용신청 추가
        abstractDAO.insert("set.insertReq", params);

        response.setData(new HashMap() {{
            put("entId", entId);
        }});
        response.setResult(ResultCode.RC_OK);

        return response;
    }

    /**
     * 사업소 수정
     * *
     */
    public BaseResponse updateEnt(Map<String, Object> params, Map<String, MultipartFile> fileMap) throws Exception {
        BaseResponse response = new BaseResponse();

        //전화번호 암호화
        SDBCryptUtil sdb = new SDBCryptUtil();

        String usrPnum = MapUtils.getString(params, "usrPnum");
        if (usrPnum != null && !usrPnum.equals("")) params.put("usrPnum", sdb.encrypt(usrPnum));

        //비밀번호 암호화
        String usrId = MapUtils.getString(params, "entUsrId");
        String plainPw = MapUtils.getString(params, "usrPw");
        if (plainPw != null && !plainPw.equals("")) {
            if (usrId == null || usrId.equals("")) throw new Exception("비밀번호 변경시 ID도 입력바랍니다.");
            String pw = SHA256Util.encryptPassword(plainPw, usrId);
            params.put("usrPw", pw);
        }

        //카카오맵으로 위도 경도 구하기
        String entAddr = MapUtils.getString(params, "entAddr");
        if (entAddr != null && !entAddr.equals("")) {
            String apiKey = env.getProperty("kakao.apiKey");
            String url = env.getProperty("kakao.localUrl");
            String getUrl = (url + entAddr).replaceAll(" ", "%20");

            Map content = excuteHttpLocalClient(getUrl, apiKey);

            params.put("entLatitude", content.get("latitude"));
            params.put("entLongitude", content.get("longitude"));
        }
        //변경전 사업소정보 조회
        Map entInfo = (Map) abstractDAO.selectOne("ent.selectEntInfo", params);

        //첨부된 파일이 존재할 경우
        if (!fileMap.isEmpty()) {
            if (fileMap.get("sealFile") != null) {
                List<FileVO> fileVoList = new ArrayList<FileVO>();

                String entSealAttr = MapUtils.getString(entInfo, "entSealAttr");
                if (entSealAttr != null && !entSealAttr.equals("")) {
                    //이전 이미지 조회해서 삭제처리
                    List<Map> beforeFileList = abstractDAO.selectList("file.selectFileList", new HashMap() {{
                        put("atchFileId", entSealAttr);
                    }});
                    for (Map beforeFile : beforeFileList) {
                        String beforeFilePath = MapUtils.getString(beforeFile, "fileStreCours");
                        String beforeFileName = MapUtils.getString(beforeFile, "streFileNm");
                        fileService.deleteFile(beforeFilePath + beforeFileName);
                    }
                    //DB에서 파일정보 삭제처리
                    abstractDAO.update("file.deleteCOMTNFILE", entSealAttr);
                }
                String atchFileId = newAtchFileId();
                fileVoList = fileService.parseFileInfSeal(fileMap, 0, atchFileId, "", params.get("entId").toString());
                fileService.insertFileInfoList(fileVoList);
                params.put("entSealAttr", atchFileId);
            }
            if (fileMap.get("crnFile") != null) {
                List<FileVO> fileVoList = new ArrayList<FileVO>();

                String entCrnAttr = MapUtils.getString(entInfo, "entCrnAttr");
                if (entCrnAttr != null && !entCrnAttr.equals("")) {
                    //이전 이미지 조회해서 삭제처리
                    List<Map> beforeFileList = abstractDAO.selectList("file.selectFileList", new HashMap() {{
                        put("atchFileId", entCrnAttr);
                    }});
                    for (Map beforeFile : beforeFileList) {
                        String beforeFilePath = MapUtils.getString(beforeFile, "fileStreCours");
                        String beforeFileName = MapUtils.getString(beforeFile, "streFileNm");
                        fileService.deleteFile(beforeFilePath + beforeFileName);
                    }
                    //DB에서 파일정보 삭제처리
                    abstractDAO.update("file.deleteCOMTNFILE", entCrnAttr);
                }
                String atchFileId = newAtchFileId();
                fileVoList = fileService.parseFileInfo(fileMap, "OSL_", 0, atchFileId, "", "crn");
                fileService.insertFileInfoList(fileVoList);
                params.put("entCrnAttr", atchFileId);
            }
        }
        //사업소 수정
        abstractDAO.update("ent.updateEnt", params);
        //사업소 회원 수정
        abstractDAO.update("ent.updateEntAccount", params);

        response.setResult(ResultCode.RC_OK);

        return response;
    }

    /**
     * 사업소 계정 수정
     * *
     */
    public BaseResponse updateEntAccount(Map<String, Object> params) throws Exception {
        BaseResponse response = new BaseResponse();

        //전화번호 암호화
        SDBCryptUtil sdb = new SDBCryptUtil();

        String usrPnum = MapUtils.getString(params, "usrPnum");
        if (usrPnum != null && !usrPnum.equals("")) params.put("usrPnum", sdb.encrypt(usrPnum));

        //비밀번호 암호화
        String usrId = MapUtils.getString(params, "usrId");
        String plainPw = MapUtils.getString(params, "usrPw");
        if (plainPw != null && !plainPw.equals("")) {
            String pw = SHA256Util.encryptPassword(plainPw, usrId);
            params.put("usrPw", pw);
        }

        //사업소 회원 수정
        abstractDAO.update("ent.updateEntAccount", params);


        response.setResult(ResultCode.RC_OK);

        return response;
    }


    /**
     * 사업소 userId 수정
     */
    public BaseResponse updateUsrId(Map<String, Object> params) throws Exception {
        BaseResponse response = new BaseResponse();

        int entUsrCnt = (int) abstractDAO.selectOne("ent.selectDuplicatedEntIdCnt", params);

        if (entUsrCnt == 0) {
            throw new Exception("존재하지 않는 아이디입니다.");
        }

        Map<String, Object> tempData = new HashMap<>();
        tempData.put("usrId", params.get("toUsrId"));

        int duplicateEntUsrIdCnt = (int) abstractDAO.selectOne("ent.selectDuplicatedEntIdCnt", tempData);

        if (duplicateEntUsrIdCnt > 0) {
            throw new Exception("변경하려는 아이디는 이미 존재하는 아이디입니다.");
        }

        // 사업소 회원아이디 수정
        abstractDAO.update("ent.updateUsrId", params);

        // 수급자 피벗테이블 entUsrId 수정
        abstractDAO.update("ent.updateEntUsrIdForEntPen1000", params);

        // 로그 추가
        abstractDAO.insert("ent.insertUsrIdChangeLog", params);

        response.setResult(ResultCode.RC_OK);

        return response;
    }
}
