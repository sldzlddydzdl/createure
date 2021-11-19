package example.com.temp_humi_graphs.Weather;

import java.util.List;

// weather api 에서 받은 값들을 저장하기위한 클래스
public class WeatherApi {

    String resultCode;
    String resultMsg;
    String dataType;
    List<WeatherItem> weatherItems;
    String pageNo;
    String numOfRows;
    String totalCount;

    public WeatherApi() {

    }

    public WeatherApi(String resultCode, String resultMsg, String dataType, List<WeatherItem> weatherItems, String pageNo, String numOfRows, String totalCount) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
        this.dataType = dataType;
        this.weatherItems = weatherItems;
        this.pageNo = pageNo;
        this.numOfRows = numOfRows;
        this.totalCount = totalCount;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public List<WeatherItem> getWeatherItems() {
        return weatherItems;
    }

    public void setWeatherItems(List<WeatherItem> weatherItems) {
        this.weatherItems = weatherItems;
    }

    public String getPageNo() {
        return pageNo;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
    }

    public String getNumOfRows() {
        return numOfRows;
    }

    public void setNumOfRows(String numOfRows) {
        this.numOfRows = numOfRows;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    @Override
    public String toString() {
        return "WeatherApi{" +
                "resultCode='" + resultCode + '\'' +
                ", resultMsg='" + resultMsg + '\'' +
                ", dataType='" + dataType + '\'' +
                ", weatherItems=" + weatherItems +
                ", pageNo='" + pageNo + '\'' +
                ", numOfRows='" + numOfRows + '\'' +
                ", totalCount='" + totalCount + '\'' +
                '}';
    }
}
