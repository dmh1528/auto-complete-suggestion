package com.example.location_data;

public class City extends Location {

    public City(String name, boolean isAmbiguous) {

        super(name, isAmbiguous);
    }
    private District district;
    public District getDistrict() {

        return district;
    }
    public void setDistrict(District district) {

        this.district = district;
    }

    @Override
    public int hashCode() {

        int result = super.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        City other = (City) obj;
        if (district == null) {
            if (other.district != null)
                return false;
        } else if (!district.equals(other.district))
            return false;
        return true;
    }
    @Override
    public String toString() {

        return "City [name=" + name + ", district=" + district + "]";
    }
}
