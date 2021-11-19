package example.com.temp_humi_graphs.Weather;

// weather api 에서 세부세항을 저장하기위한 클래스
public class WeatherItem {

    String accountTime;
    String numEf;
    String regId;
    String rnSt;
    String rnYn;
    String ta;
    String wd1;
    String wd2;
    String wdTnd;
    String wf;
    String wfCd;
    String wsIt;

    public WeatherItem() {
    }

    public WeatherItem(String accountTime, String numEf, String regId, String rnSt, String rnYn, String ta, String wd1, String wd2, String wdTnd, String wf, String wfCd, String wsIt) {
        this.accountTime = accountTime;
        this.numEf = numEf;
        this.regId = regId;
        this.rnSt = rnSt;
        this.rnYn = rnYn;
        this.ta = ta;
        this.wd1 = wd1;
        this.wd2 = wd2;
        this.wdTnd = wdTnd;
        this.wf = wf;
        this.wfCd = wfCd;
        this.wsIt = wsIt;
    }

    public String getAccountTime() {
        return accountTime;
    }

    public void setAccountTime(String accountTime) {
        this.accountTime = accountTime;
    }

    public String getNumEf() {
        return numEf;
    }

    public void setNumEf(String numEf) {
        this.numEf = numEf;
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public String getRnSt() {
        return rnSt;
    }

    public void setRnSt(String rnSt) {
        this.rnSt = rnSt;
    }

    public String getRnYn() {
        return rnYn;
    }

    public void setRnYn(String rnYn) {
        this.rnYn = rnYn;
    }

    public String getTa() {
        return ta;
    }

    public void setTa(String ta) {
        this.ta = ta;
    }

    public String getWd1() {
        return wd1;
    }

    public void setWd1(String wd1) {
        this.wd1 = wd1;
    }

    public String getWd2() {
        return wd2;
    }

    public void setWd2(String wd2) {
        this.wd2 = wd2;
    }

    public String getWdTnd() {
        return wdTnd;
    }

    public void setWdTnd(String wdTnd) {
        this.wdTnd = wdTnd;
    }

    public String getWf() {
        return wf;
    }

    public void setWf(String wf) {
        this.wf = wf;
    }

    public String getWfCd() {
        return wfCd;
    }

    public void setWfCd(String wfCd) {
        this.wfCd = wfCd;
    }

    public String getWsIt() {
        return wsIt;
    }

    public void setWsIt(String wsIt) {
        this.wsIt = wsIt;
    }

    @Override
    public String toString() {
        return "WeatherItem{" +
                "accountTime='" + accountTime + '\'' +
                ", numEf='" + numEf + '\'' +
                ", regId='" + regId + '\'' +
                ", rnSt='" + rnSt + '\'' +
                ", rnYn='" + rnYn + '\'' +
                ", ta='" + ta + '\'' +
                ", wd1='" + wd1 + '\'' +
                ", wd2='" + wd2 + '\'' +
                ", wdTnd='" + wdTnd + '\'' +
                ", wf='" + wf + '\'' +
                ", wfCd='" + wfCd + '\'' +
                ", wsIt='" + wsIt + '\'' +
                '}';
    }
}
