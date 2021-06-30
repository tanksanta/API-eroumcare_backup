package kr.co.thkc.utils;

import com.ksign.securedb.api.SDBCrypto;
import com.ksign.securedb.api.util.SDBException;
import com.ksign.securedb.fileapi.SDBFileAPI;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SDBCryptUtil {
    private static final String ServerIP = "175.125.94.165";
    private static final int ServerPort = 9003;
    private static final String schema = "dbsec";
    private static final String table = "securekey";
    private static final String column = "aria256";
    private static final String DomainName = "THK";

    private static SDBCrypto crypto = null;

    public SDBCryptUtil() {
        if (crypto == null) {
            try {
                crypto = SDBCrypto.getInstanceDomain(DomainName, ServerIP, ServerPort);
            } catch (SDBException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                log.error(e.getMessage());
            }
        }
    }

    public static String encrypt(String param) {
        try {
            return crypto.encryptDP(schema, table, column, param, null, 0);
        } catch (Exception e) {
//        	e.printStackTrace();
            System.out.println("SDB API encrypt fail : " + param);
            log.error(e.getMessage());
            return param;
        }
    }

    public static String decrypt(String param) {
        try {
            return crypto.decryptDP(schema, table, column, param, null, 0);
        } catch (Exception e) {
//        	e.printStackTrace();
            System.out.println("SDB API decrypt fail : " + param);
            log.error(e.getMessage());

            return param;
        }
    }

    public static boolean encryptFile(String sourcePath, String targetPath) {
        if ("".equals(sourcePath) || "".equals(targetPath)) {
            sourcePath = "C:/Temp/lee/File/test_400_dec.txt";
            targetPath = "C:/Temp/lee/File/test_400_enc.txt";
        }

        String policyName = "DBSEC.SECUREKEY.ARIA256";

        try {
            return SDBFileAPI.encryptFileT(policyName, sourcePath, targetPath, true);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());

            return false;
        }
    }

    public static boolean decryptFile(String sourcePath, String targetPath) {
        if ("".equals(sourcePath) || "".equals(targetPath)) {
            sourcePath = "C:/Temp/lee/File/test_400_enc.txt";
            targetPath = "C:/Temp/lee/File/test_400_dec.txt";
        }

        String policyName = "DBSEC.SECUREKEY.ARIA256";

        try {
            return SDBFileAPI.decryptFileT(policyName, sourcePath, targetPath);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());

            return false;
        }
    }
}
