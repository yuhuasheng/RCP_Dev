package com.hh.tools.environmental.dialog;

public class EnvirProDemo {
    private String customer;
    private String reachStatus;
    private String mcdROHSStatus;
    private String hfStatus;
    private String mddROHSStatus;


    public EnvirProDemo() {
        super();
    }


    public EnvirProDemo(String customer, String reachStatus,
                        String mcdROHSStatus, String hfStatus, String mddROHSStatus) {
        super();
        this.customer = customer;
        this.reachStatus = reachStatus;
        this.mcdROHSStatus = mcdROHSStatus;
        this.hfStatus = hfStatus;
        this.mddROHSStatus = mddROHSStatus;
    }


    public String getCustomer() {
        return customer;
    }


    public void setCustomer(String customer) {
        this.customer = customer;
    }


    public String getReachStatus() {
        return reachStatus;
    }


    public void setReachStatus(String reachStatus) {
        this.reachStatus = reachStatus;
    }


    public String getMcdROHSStatus() {
        return mcdROHSStatus;
    }


    public void setMcdROHSStatus(String mcdROHSStatus) {
        this.mcdROHSStatus = mcdROHSStatus;
    }


    public String getHfStatus() {
        return hfStatus;
    }


    public void setHfStatus(String hfStatus) {
        this.hfStatus = hfStatus;
    }


    public String getMddROHSStatus() {
        return mddROHSStatus;
    }


    public void setMddROHSStatus(String mddROHSStatus) {
        this.mddROHSStatus = mddROHSStatus;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((customer == null) ? 0 : customer.hashCode());
        result = prime * result
                + ((hfStatus == null) ? 0 : hfStatus.hashCode());
        result = prime * result
                + ((mcdROHSStatus == null) ? 0 : mcdROHSStatus.hashCode());
        result = prime * result
                + ((mddROHSStatus == null) ? 0 : mddROHSStatus.hashCode());
        result = prime * result
                + ((reachStatus == null) ? 0 : reachStatus.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EnvirProDemo other = (EnvirProDemo) obj;
        if (customer == null) {
            if (other.customer != null)
                return false;
        } else if (!customer.equals(other.customer))
            return false;
        if (hfStatus == null) {
            if (other.hfStatus != null)
                return false;
        } else if (!hfStatus.equals(other.hfStatus))
            return false;
        if (mcdROHSStatus == null) {
            if (other.mcdROHSStatus != null)
                return false;
        } else if (!mcdROHSStatus.equals(other.mcdROHSStatus))
            return false;
        if (mddROHSStatus == null) {
            if (other.mddROHSStatus != null)
                return false;
        } else if (!mddROHSStatus.equals(other.mddROHSStatus))
            return false;
        if (reachStatus == null) {
            if (other.reachStatus != null)
                return false;
        } else if (!reachStatus.equals(other.reachStatus))
            return false;
        return true;
    }

}
