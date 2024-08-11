package com.i2i.evrencell.packages;

public class UserDetails {
    private final String name;
    private final String lastName;
    private final String email;
    private final int packageId;

    public UserDetails(String name, String lastName, String email, int packageId) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.packageId = packageId;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public int getPackageId() {
        return packageId;
    }
}
