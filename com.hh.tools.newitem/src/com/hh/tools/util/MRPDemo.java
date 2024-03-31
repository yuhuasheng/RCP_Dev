package com.hh.tools.util;


public class MRPDemo {
    private String plant;
    private String factory;
    private String[] mrpArray;


    public MRPDemo() {
        super();
    }

    public MRPDemo(String plant, String factory, String[] mrpArray) {
        super();
        this.plant = plant;
        this.factory = factory;
        this.mrpArray = mrpArray;
    }

    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String[] getMrpArray() {
        return mrpArray;
    }

    public void setMrpArray(String[] mrpArray) {
        this.mrpArray = mrpArray;
    }


}
