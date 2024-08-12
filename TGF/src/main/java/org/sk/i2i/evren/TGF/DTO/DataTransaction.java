package org.sk.i2i.evren.TGF.DTO;

public class DataTransaction {

    private final String msisdn;
    private final int location;
    private final int dataUsage;
    private final int ratingGroup;


    public DataTransaction(String msisdn, int location, int dataUsage, int ratingGroup) {
        this.msisdn = msisdn;
        this.location = location;
        this.dataUsage = dataUsage;
        this.ratingGroup = ratingGroup;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public int getLocation() {
        return location;
    }

    public int getDataUsage() {
        return dataUsage;
    }

    public int getRatingGroup() {
        return ratingGroup;
    }

    @Override
    public String toString() {
        return "DataTransaction{" +
                "msisdn='" + msisdn + '\'' +
                ", location=" + location +
                ", dataUsage=" + dataUsage +
                ", ratingGroup=" + ratingGroup +
                '}';
    }

}
