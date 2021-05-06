package kr.co.thkc.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Service
public class LifeCycleConfig implements InitializingBean, DisposableBean {


    @Autowired
    Environment env;

    File file = new File("./eroumapi/", "startup.log");
    File version = new File("./eroumapi/", "version");
    
    //어플리케이션 시작 프로퍼티 세팅 후 작업
    @Override
    public void afterPropertiesSet() throws Exception {

        String strLog = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = format.format( new Date());
        
        
        strLog = strLog + "\n";
        strLog = strLog + "********************************************************************************* \n";
        strLog = strLog + "                              eroum Configuration Info                            \n";
        strLog = strLog + "********************************************************************************* \n";
        strLog = strLog + "# SERVER INFO \n";
        strLog = strLog + String.format("    %-10s : %s \n", "START_TIME", now);
        strLog = strLog + String.format("    %-10s : %s \n", "MODE", env.getProperty("server.mode", ""));
        strLog = strLog + String.format("    %-10s : %s \n", "PORT", env.getProperty("server.port", ""));
        strLog = strLog + String.format("    %-10s : %s \n", "VERSION", env.getProperty("server.version", ""));
        strLog = strLog + "# DATABASE INFO \n";
        strLog = strLog + String.format("    %-10s : %s \n", "DB_TYPE", env.getProperty("spring.datasource.hikari.driver-class-name", ""));
        strLog = strLog + String.format("    %-10s : %s \n", "JDBC_URL", env.getProperty("spring.datasource.hikari.jdbc-url", ""));
        strLog = strLog + String.format("    %-10s : %s \n", "USER_ID", env.getProperty("spring.datasource.hikari.username", ""));
        strLog = strLog + String.format("    %-10s : %s \n", "MIN_POOL", env.getProperty("spring.datasource.hikari.maxPoolSize", ""));
        strLog = strLog + String.format("    %-10s : %s \n", "TIME_OUT", env.getProperty("spring.datasource.hikari.connectionTimeout", ""));
        strLog = strLog + "# FILE PATH INFO\n";
        strLog = strLog + String.format("    %-10s : %s \n", "DB_TYPE", env.getProperty("path.store", ""));
        strLog = strLog + String.format("    %-10s : %s \n", "DB_TYPE", env.getProperty("path.temp", ""));
        strLog = strLog + String.format("    %-10s : %s \n", "DB_TYPE", env.getProperty("path.seal", ""));
        strLog = strLog + "*********************************************************************************\n";
        strLog = strLog + "*********************************************************************************\n";

        // 출력
        log.info(strLog);

      
        // 시작이력 로그 
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file, true));
            bw.write(strLog);
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(bw != null) try {bw.close(); } catch (IOException e) {}
        }

        // 버전정보
        bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(version, false));
            bw.write(String.format("export %s='%s'\n", "START_TIME", now));
            bw.write(String.format("export %s='%s'\n", "MODE", env.getProperty("server.mode", "")));
            bw.write(String.format("export %s='%s'\n", "PORT", env.getProperty("server.port", "")));
            bw.write(String.format("export %s='%s'\n", "VERSION", env.getProperty("server.version", "")));
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(bw != null) try {bw.close(); } catch (IOException e) {}
        }



        
    }

    //Bean 소멸시
    @Override
    public void destroy() throws Exception {
        String strLog = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = format.format( new Date());


        strLog = strLog + "********************************************************************************* \n";
        strLog = strLog + "                                 Server Destory                                   \n";
        strLog = strLog + "********************************************************************************* \n";
        strLog = strLog + "# SERVICE END \n";
        strLog = strLog + String.format("    %-10s : %s \n", "DESTORY_TIME", now);
        strLog = strLog + "********************************************************************************* \n";
        strLog = strLog + "********************************************************************************* \n";


        
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file, true));
            bw.write(strLog);
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(bw != null) try {bw.close(); } catch (IOException e) {}
        }
    }
}
