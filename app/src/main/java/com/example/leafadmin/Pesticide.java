package com.example.leafadmin;

import java.math.BigDecimal;

//Class to store Pesticide data
public class Pesticide {
    String pesticideName, pesticideID;
    //BigDecimal rParam1, rParam2;
    BigDecimal referenceVapourPressure, referenceTemp, molarMass;


    public Pesticide(String id, String name, BigDecimal rVapourPressure, BigDecimal rTemp, BigDecimal mMass){
        pesticideID = id;
        pesticideName = name;
        //rParam1 = rp1;
        //rParam2 = rp2;
        //Don't add until the main app is ready to read these.
        referenceVapourPressure = rVapourPressure;
        referenceTemp = rTemp;
        molarMass = mMass;
    }

    public String getName(){return pesticideName;}
    public String getPesticideID(){return pesticideID;}
    //public BigDecimal getRParam1(){return rParam1;}
    //public BigDecimal getRParam2(){return rParam2;}
    public BigDecimal getReferenceVapourPressure(){return referenceVapourPressure;}
    public BigDecimal getReferenceTemp(){return referenceTemp;}
    public BigDecimal getMolarMass(){return molarMass;}
}