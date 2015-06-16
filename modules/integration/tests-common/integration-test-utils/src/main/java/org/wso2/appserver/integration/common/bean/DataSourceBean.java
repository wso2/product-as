package org.wso2.appserver.integration.common.bean;

/**
 * Class to store data source details
 */
public class DataSourceBean {
    private String URL;
    private String userName;
    private char[] password;
    private String driverClassName;

    public DataSourceBean(String URL, String userName, char[] password, String driverClassName) {
        this.URL = URL;
        this.userName = userName;
        this.password = password;
        this.driverClassName = driverClassName;
    }

    public String getUrl() {
        return URL;
    }

    public void setUrl(String URL) {
        this.URL = URL;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public char[] getPassWord() {
        return password;
    }

    public void setPassWord(char[] passWord) {
        this.password = passWord;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }
}
