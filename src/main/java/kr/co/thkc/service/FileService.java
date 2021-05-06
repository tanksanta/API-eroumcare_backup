package kr.co.thkc.service;

import kr.co.thkc.mapper.AbstractDAO;
import kr.co.thkc.utils.ImageUtil;
import kr.co.thkc.utils.SDBCryptUtil;
import kr.co.thkc.utils.WebUtil;
import kr.co.thkc.vo.FileVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@Service
public class FileService extends BaseService {

    @Autowired
    AbstractDAO abstractDAO;

    @Autowired
    Environment env;


    /**
     * 첨부파일에 대한 목록 정보를 취득한다. (직인)
     *
     * @param files
     * @return
     * @throws Exception
     */
    public List<FileVO> parseFileInfSeal(Map<String, MultipartFile> files, int fileKeyParam, String atchFileId, String storePath, String entId) throws Exception {
        int fileKey = fileKeyParam;
        String storePathString = "";
        String atchFileIdString = "";

        if ("".equals(storePath) || storePath == null) {
            storePathString = env.getProperty("path.seal");
        } else {
            storePathString = env.getProperty(storePath);
        }
        //storePath 폴더 분류
        if(!"".equals(entId) && entId != null){

            String y = entId.substring(3, 7);
            String m = entId.substring(7, 9);
            String d = entId.substring(9, 11);

            storePathString += y + "/" + m + "/" + d +"/";
        }

        File saveFolder = new File(WebUtil.filePathBlackList(storePathString));

        if (!saveFolder.exists() || saveFolder.isFile()) {
            saveFolder.mkdirs();
        }

        // 이전 파일이 있으면 삭제
        File temp = new File(storePathString + File.separator + entId + ".png");
        temp.delete();

        Iterator<Map.Entry<String, MultipartFile>> itr = files.entrySet().iterator();
        MultipartFile file;
        String filePath = "";
        String orgFilePath = "";
        List<FileVO> result = new ArrayList<FileVO>();
        FileVO fvo;

        while (itr.hasNext()) {
            Map.Entry<String, MultipartFile> entry = itr.next();

            file = entry.getValue();
            String orginFileName = file.getOriginalFilename();

            //--------------------------------------
            // 원 파일명이 없는 경우 처리
            // (첨부가 되지 않은 input file type)
            //--------------------------------------
            if ("".equals(orginFileName)) {
                continue;
            }
            ////------------------------------------

            int index = orginFileName.lastIndexOf(".");
            //String fileName = orginFileName.substring(0, index);
            String fileExt = orginFileName.substring(index + 1);

            //확장자가 없는 경우
            if(index == -1){
                continue;
            }

            String newName = entId + ".png";
            String orgNewName = entId + "org.png";
            long size = file.getSize();

            if (!"".equals(orginFileName)) {
                // 300x300 8bit png 파일로 인코딩+스케일링
                orgFilePath = storePathString + orgNewName;
                filePath = storePathString + newName;
                temp = new File(WebUtil.filePathBlackList(orgFilePath));
                ImageUtil.sampledImage(file, temp);

                // 직인이미지는 파일암호화 수행
                SDBCryptUtil.encryptFile(orgFilePath, filePath);

                // 리눅스만 수행 폴더/파일 권한 작업
                String[] pArr = filePath.split("/");
                String osName = System.getProperty("os.name").toLowerCase();
                if (osName.indexOf("nix") >= 0 || osName.indexOf("nux") >= 0 || osName.indexOf("aix") > 0) {
                    // 생성된 파일에 권한 부여
                    File targetFile = new File(filePath);
                    // 모든권한을 제거하고 시작
                    targetFile.setReadable(false, false);
                    targetFile.setWritable(false, false);
                    targetFile.setExecutable(false, false);
                    if(targetFile.exists()) {
                        Path path = Paths.get(filePath);
                        Set<PosixFilePermission> posixPermissions = PosixFilePermissions.fromString("rwxrwxrwx");
                        Files.setPosixFilePermissions(path, posixPermissions);
                    }

                    // 해당 파일까지의 폴더에도 권한부여
                    String authPath = new String(storePathString);
                    do {
                        authPath = authPath.substring(0, authPath.lastIndexOf("/"));
                        targetFile = new File(authPath);
                        // 모든권한을 제거하고 시작
                        targetFile.setReadable(false, false);
                        targetFile.setWritable(false, false);
                        targetFile.setExecutable(false, false);
                        if(targetFile.exists()) {
                            Path path = Paths.get(authPath);
                            Set<PosixFilePermission> posixPermissions = PosixFilePermissions.fromString("rwxrwxrwx");
                            Files.setPosixFilePermissions(path, posixPermissions);
                        }
                    }while (!env.getProperty("path.seal").equals(authPath + "/"));
                }
            }

            fvo = new FileVO();
            fvo.setFileExtsn(fileExt);
            fvo.setFileStreCours(storePathString);
            fvo.setFileMg(Long.toString(size));
            fvo.setOrignlFileNm(orginFileName);
            fvo.setStreFileNm(newName);
            fvo.setAtchFileId(atchFileIdString);
            fvo.setFileSn(String.valueOf(fileKey));

            result.add(fvo);

            log.debug("======================"+result);

            fileKey++;
        }

        return result;
    }


    /**
     * 여러 개의 파일에 대한 정보(속성 및 상세)를 등록한다.
     *
     * @param fileList
     * @return
     * @throws Exception
     */
    public String insertFileInfoList(List<?> fileList) throws Exception {
        FileVO vo = (FileVO) fileList.get(0);
        String atchFileId = vo.getAtchFileId();

        abstractDAO.insert("file.insertFileMaster", vo);

        Iterator<?> iter = fileList.iterator();
        while (iter.hasNext()) {
            vo = (FileVO) iter.next();
            abstractDAO.insert("file.insertFileDetail", vo);
        }

        return atchFileId;
    }


    /**
     * <pre>
     * Comment : 파일을 삭제한다.
     * </pre>
     *
     * @param fileDeletePath 삭제하고자 하는파일의 절대경로
     * @return 성공하면 삭제된 파일의 절대경로, 아니면블랭크
     */

    public String deleteFile(String fileDeletePath) {

        // 인자값 유효하지 않은 경우 블랭크 리턴
        if (fileDeletePath == null || fileDeletePath.equals("")) {
            return "";
        }
        String result = "";
        File file = new File(WebUtil.filePathBlackList(fileDeletePath));
        if (file.isFile()) {
            result = deletePath(fileDeletePath);
        } else {
            result = "";
        }
        return result;
    }

    /**
     * <pre>
     * Comment : 디렉토리(파일)를 삭제한다. (파일,디렉토리 구분없이 존재하는 경우 무조건 삭제한다)
     * </pre>
     *
     * @return 성공하면 삭제된 절대경로, 아니면블랭크
     */

    public String deletePath(String filePath) {
        File file = new File(WebUtil.filePathBlackList(filePath));
        String result = "";
        if (file.exists()) {
            result = file.getAbsolutePath();
            if (!file.delete()) {
                result = "";
            }
        }
        return result;
    }


    /**
     * 첨부파일에 대한 목록 정보를 취득한다.
     *
     * @param files
     * @return
     * @throws Exception
     */
    public List<FileVO> parseFileInfo(Map<String, MultipartFile> files, String KeyStr, int fileKeyParam, String atchFileId, String storePath, String addStrPath) throws Exception {
        int fileKey = fileKeyParam;
        String storePathString = "";
        String atchFileIdString = "";

        if ("".equals(storePath) || storePath == null) {
            storePathString = env.getProperty("path.store");
        } else {
            storePathString = env.getProperty(storePath);
        }
        //storePath 폴더 분류
        if(!"".equals(addStrPath) && addStrPath != null){
            storePathString += addStrPath+"/";
        }

        if ("".equals(atchFileId) || atchFileId == null) {
            atchFileIdString = newAtchFileId();
        } else {
            atchFileIdString = atchFileId;
        }

        File saveFolder = new File(WebUtil.filePathBlackList(storePathString));

        if (!saveFolder.exists() || saveFolder.isFile()) {
            saveFolder.mkdirs();
        }

        Iterator<Map.Entry<String, MultipartFile>> itr = files.entrySet().iterator();
        MultipartFile file;
        String filePath = "";
        List<FileVO> result = new ArrayList<FileVO>();
        FileVO fvo;

        while (itr.hasNext()) {
            Map.Entry<String, MultipartFile> entry = itr.next();

            file = entry.getValue();
            String orginFileName = file.getOriginalFilename();

            //--------------------------------------
            // 원 파일명이 없는 경우 처리
            // (첨부가 되지 않은 input file type)
            //--------------------------------------
            if ("".equals(orginFileName)) {
                continue;
            }
            ////------------------------------------

            int index = orginFileName.lastIndexOf(".");
            //String fileName = orginFileName.substring(0, index);
            String fileExt = orginFileName.substring(index + 1);

            //확장자가 없는 경우
            if(index == -1){
                continue;
            }

            /*
             * 이미지 확장자 체크
             * 저장 폴더 명(addStrPath)을 UsrImg로 지정 할 경우 확장자 체크 처리
             */
            if("UsrImg".equals(addStrPath)){
                //확장자 이미지인지 체크
                String imgPattern = "(jpg|jpeg|png|gif)$";
                Pattern p = Pattern.compile(imgPattern);
                Matcher m = p.matcher(fileExt);

                //확장자 체크, 이미지 확장자가 아닐 경우
                if(m.find() == false){
                    continue;
                }
            }


            String newName = KeyStr + getTimeStamp() + fileKey;
            long size = file.getSize();

            if (!"".equals(orginFileName)) {
                filePath = storePathString + File.separator + newName;
                file.transferTo(new File(WebUtil.filePathBlackList(filePath)));
            }

            fvo = new FileVO();
            fvo.setFileExtsn(fileExt);
            fvo.setFileStreCours(storePathString);
            fvo.setFileMg(Long.toString(size));
            fvo.setOrignlFileNm(orginFileName);
            fvo.setStreFileNm(newName);
            fvo.setAtchFileId(atchFileIdString);
            fvo.setFileSn(String.valueOf(fileKey));

            result.add(fvo);

            log.debug("======================"+result);

            fileKey++;
        }

        return result;
    }


    /**
     * 공통 컴포넌트 utl.fcc 패키지와 Dependency제거를 위해 내부 메서드로 추가 정의함
     * 응용어플리케이션에서 고유값을 사용하기 위해 시스템에서17자리의TIMESTAMP값을 구하는 기능
     *
     * @param
     * @return Timestamp 값
     * @see
     */
    private static String getTimeStamp() {

        String rtnStr = null;

        // 문자열로 변환하기 위한 패턴 설정(년도-월-일 시:분:초:초(자정이후 초))
        String pattern = "yyyyMMddhhmmssSSS";

        SimpleDateFormat sdfCurrent = new SimpleDateFormat(pattern, Locale.KOREA);
        Timestamp ts = new Timestamp(System.currentTimeMillis());

        rtnStr = sdfCurrent.format(ts.getTime());

        return rtnStr;
    }
}
