package com.dab.medireminder.data.model;

public class Medicine {
    private String name;
    private String dose;
    private String image;

    public Medicine() {

    }

    public Medicine(String name, String dose, String image) {
        this.name = name;
        this.dose = dose;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
