package org.wso2.appserver.sample.ee.jsf.bval;

import javax.faces.bean.ManagedBean;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@ManagedBean(name = "org.wso2.appserver.sample.ee.jsf.bval.Calculator")
public class Calculator {

    @NotNull
    @Min(0)
    @Max(100)
    private int mark1;

    @NotNull
    @Min(0)
    @Max(100)
    private int mark2;

    private int total;
    private float average;

    public int getMark1() {
        return mark1;
    }

    public void setMark1(int mark1) {
        this.mark1 = mark1;
    }

    public int getMark2() {
        return mark2;
    }

    public void setMark2(int mark2) {
        this.mark2 = mark2;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public float getAverage() {
        return average;
    }

    public void setAverage(float average) {
        this.average = average;
    }

    public String calculate() {
        total = mark1 + mark2;
        average = (float) total / 2;
        return "success";
    }

}
