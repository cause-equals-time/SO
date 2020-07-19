package sample;

public class CertAppKeyData {

    private String certificate;
    private String password;
    private String appKey;

    public boolean isComplete(){
        return (certificate!=null && password!=null && appKey!=null);
    }

    public CertAppKeyData() {
    }

    public CertAppKeyData(String certificate, String password, String appKey) {
        this.certificate = certificate;
        this.password = password;
        this.appKey = appKey;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }
}
