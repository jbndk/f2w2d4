package dtos;

import entities.Person;

/**
 *
 * @author Jonas
 */

public class PersonDTO {
    private Integer id;
    private String fName;
    private String lName;
    private String phone;
    private String street;
    private String zip;
    private String city;
    
    public PersonDTO(Person person) {
        this.fName = person.getfName();
        this.lName = person.getlName();
        this.phone = person.getPhone();
        this.id = person.getId();
        this.street = person.getAddress().getStreet();
        this.zip = person.getAddress().getZip();
        this.city = person.getAddress().getCity();
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public PersonDTO(String fName,String lName, String phone, String street, String zip, String city) {
        this.fName = fName;
        this.lName = lName;
        this.phone = phone;        
        this.street = street;
        this.zip = zip;
        this.city = city;
    }
    
    public PersonDTO() {}

    public Integer getId() {
        return id;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PersonDTO other = (PersonDTO) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    
    
}
