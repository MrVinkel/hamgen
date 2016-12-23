package dk.martinvinkel.hamgen.builder;

import dk.martinvinkel.hamgen.integrationtest.schemapackage.Address;

public class AddressBuilder {
    protected String cityName;
    protected String streetName;
    protected String streetNumber;
    protected Integer floor;
    protected String postCode;

    private AddressBuilder() {
    }

    public static AddressBuilder anAddress() {
        return new AddressBuilder();
    }

    public static AddressBuilder aDefaultAddress() {
        return new AddressBuilder()
                .withCityName("New York")
                .withFloor(42)
                .withPostCode("8453")
                .withStreetName("Peace Road")
                .withStreetNumber("101010");
    }

    public AddressBuilder withCityName(String cityName) {
        this.cityName = cityName;
        return this;
    }

    public AddressBuilder withStreetName(String streetName) {
        this.streetName = streetName;
        return this;
    }

    public AddressBuilder withStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
        return this;
    }

    public AddressBuilder withFloor(Integer floor) {
        this.floor = floor;
        return this;
    }

    public AddressBuilder withPostCode(String postCode) {
        this.postCode = postCode;
        return this;
    }

    public Address build() {
        Address address = new Address();
        address.setCityName(cityName);
        address.setStreetName(streetName);
        address.setStreetNumber(streetNumber);
        address.setFloor(floor);
        address.setPostCode(postCode);
        return address;
    }
}
