package example.com.temp_humi_graphs.entity;

// 온도값만 담기위한 객체
public class Mcu1Temp {
    float temp1;
    float temp2;
    float temp3;

    public Mcu1Temp(){

    }

    public Mcu1Temp(float temp1, float temp2, float temp3) {
        this.temp1 = temp1;
        this.temp2 = temp2;
        this.temp3 = temp3;
    }

    public float getTemp1() {
        return temp1;
    }

    public void setTemp1(float temp1) {
        this.temp1 = temp1;
    }

    public float getTemp2() {
        return temp2;
    }

    public void setTemp2(float temp2) {
        this.temp2 = temp2;
    }

    public float getTemp3() {
        return temp3;
    }

    public void setTemp3(float temp3) {
        this.temp3 = temp3;
    }

}
