package kr.co.thkc.mapper;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.ParameterMapping;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class AbstractDAO {

    private static final int BATCH_SIZE = 1000;

    @Autowired
    private SqlSessionTemplate sqlSession;


    public int insert(String queryId, Object params) {
        return sqlSession.insert(queryId, params);
    }
    public int insert(String queryId, List params) {
        int result = 0;
        int tempInsertCount = 0;
        while (tempInsertCount < params.size()) {
            ArrayList tempList = new ArrayList();
            for (int i = 0; i < BATCH_SIZE && tempInsertCount < params.size(); i++) {
                tempList.add(params.get(tempInsertCount));
                tempInsertCount++;
            }
            result = sqlSession.insert(queryId, tempList);
        }

        return result;
    }
    
    public int update(String queryId, Object params) {
        return sqlSession.update(queryId, params);
    }
    
    public int delete(String queryId, Object params) {
        return sqlSession.delete(queryId, params);
    }
    
    public Object selectOne(String queryId, Object params) {
        return sqlSession.selectOne(queryId, params);
    }
    
    @SuppressWarnings("rawtypes")
    public List selectList(String queryId, Object params){
        return sqlSession.selectList(queryId, params);
    }

    public void commit(){
        sqlSession.commit();
    }

}
