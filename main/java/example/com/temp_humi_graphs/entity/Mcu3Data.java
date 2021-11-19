package example.com.temp_humi_graphs.entity;

import java.time.LocalDateTime;

// Mcu3 의 데이터를 담기위한 객체
public class Mcu3Data {
    int cnt;
    float temp1;
    float humidity1;
    float temp2;
    float humidity2;
    float temp3;
    float humidity3;
    String createdAt;

    public Mcu3Data(){

    }

    public Mcu3Data(int cnt, float temp1, float humidity1, float temp2, float humidity2, float temp3, float humidity3, String createdAt) {
        this.cnt = cnt;
        this.temp1 = temp1;
        this.humidity1 = humidity1;
        this.temp2 = temp2;
        this.humidity2 = humidity2;
        this.temp3 = temp3;
        this.humidity3 = humidity3;
        this.createdAt = createdAt;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public float getTemp1() {
        return temp1;
    }

    public void setTemp1(float temp1) {
        this.temp1 = temp1;
    }

    public float getHumidity1() {
        return humidity1;
    }

    public void setHumidity1(float humidity1) {
        this.humidity1 = humidity1;
    }

    public float getTemp2() {
        return temp2;
    }

    public void setTemp2(float temp2) {
        this.temp2 = temp2;
    }

    public float getHumidity2() {
        return humidity2;
    }

    public void setHumidity2(float humidity2) {
        this.humidity2 = humidity2;
    }

    public float getTemp3() {
        return temp3;
    }

    public void setTemp3(float temp3) {
        this.temp3 = temp3;
    }

    public float getHumidity3() {
        return humidity3;
    }

    public void setHumidity3(float humidity3) {
        this.humidity3 = humidity3;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
