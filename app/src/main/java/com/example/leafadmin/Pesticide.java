package com.example.leafadmin;

import java.math.BigDecimal;

public class Pesticide {
    String pesticideName, pesticideID;
    BigDecimal rParam1, rParam2;

    public Pesticide(String id, String name, BigDecimal rp1, BigDecimal rp2){
        pesticideID = id;
        pesticideName = name;
        rParam1 = rp1;
        rParam2 = rp2;
    }

    public String getName(){
        return pesticideName;
    }

    public String getPesticideID(){return pesticideID;}

    public BigDecimal getRParam1(){
        return rParam1;
    }

    public BigDecimal getRParam2(){
        return rParam2;
    }
}