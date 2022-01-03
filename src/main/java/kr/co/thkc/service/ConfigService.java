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
public class ConfigService extends BaseService {

  @Autowired
  private AbstractDAO abstractDAO;

  public List<Map<String, String>> selectWhiteList() {
    List<Map<String, String>> list = abstractDAO.selectList("config.selectWhiteList", null);

    return list;
  }

}
