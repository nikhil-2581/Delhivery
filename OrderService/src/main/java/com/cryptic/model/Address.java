package com.cryptic.model;
//added for testing
//Mirror DTO of Address (User Service) for Order Service testing

public class Address {

    private Long id;
    private String label;
    private String line1;
    private String city;
    private String pincode;
    private boolean isDefault;

    public Address() {}

    public Address(Long id, String label, String line1,
                   String city, String pincode, boolean isDefault) {
        this.id = id;
        this.label = label;
        this.line1 = line1;
        this.city = city;
        this.pincode = pincode;
        this.isDefault = isDefault;
    }

    public Long getId()        { return id; }
    public String getLabel()   { return label; }
    public String getLine1()   { return line1; }
    public String getCity()    { return city; }
    public String getPincode() { return pincode; }
    public boolean isDefault() { return isDefault; }

    public void setId(Long id)            { this.id = id; }
    public void setLabel(String label)    { this.label = label; }
    public void setLine1(String line1)    { this.line1 = line1; }
    public void setCity(String city)      { this.city = city; }
    public void setPincode(String p)      { this.pincode = p; }
    public void setDefault(boolean d)     { this.isDefault = d; }
}