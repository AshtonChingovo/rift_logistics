package com.logistics.riftvalley.data.model.Entity;

public class SerialNumbersPatchObject {

    String Details;
    String Location;

    // additional fields
    String LotNumber;

    public SerialNumbersPatchObject(String location, String details) {
        Location = location;
        Details = details;
    }

    public String getDetails() {
        return Details;
    }

    public void setDetails(String details) {
        Details = details;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getLotNumber() {
        return LotNumber;
    }

    public void setLotNumber(String lotNumber) {
        LotNumber = lotNumber;
    }


}
