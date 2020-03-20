package org.rick.auth;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.kudu.client.KuduClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.PrivilegedExceptionAction;

public class KuduKerberosAuth {

    private static final Logger log = LoggerFactory.getLogger(KuduKerberosAuth.class);


    /**
     * 初始化访问Kerberos访问
     *
     * @param debug 是否启用Kerberos的Debug模式
     */
    public static void initKerberosENV(String principal, String keytab, String krb5ConfPath, Boolean debug) {
        try {
//            System.setProperty("java.security.krb5.conf","/etc/krb5.conf");
            System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
//             UserGroupInformation.loginUserFromKeytab("hive", "/var/lib/hive/hive.keytab");
//             System.out.println("current user is: " + UserGroupInformation.getCurrentUser());


            System.setProperty("java.security.krb5.conf", krb5ConfPath);
            if (debug) {
                System.setProperty("sun.security.krb5.debug", "true");
            }
            Configuration conf = new Configuration();
            conf.set("hadoop.security.authentication", "kerberos");
            UserGroupInformation.setConfiguration(conf);
            UserGroupInformation.loginUserFromKeytab(principal, keytab);
            log.warn("getting connection from kudu with kerberos");
            log.warn("----------current user: " + UserGroupInformation.getCurrentUser() + "----------");
            log.warn("----------login user: " + UserGroupInformation.getLoginUser() + "----------");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static KuduClient getKuduClient(String kudu_masters) {
        KuduClient client = null;
        try {
            client = UserGroupInformation.getLoginUser().doAs(
                    new PrivilegedExceptionAction<KuduClient>() {
                        @Override
                        public KuduClient run() throws Exception {
                            return new KuduClient.KuduClientBuilder(kudu_masters).build();
                        }
                    }
            );

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return client;
    }
}