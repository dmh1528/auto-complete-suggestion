package com.example.location_data;

public class Location {

    protected String id;
    protected String name;
    protected boolean isAmbiguous;

    public Location() {

    }

    public Location(String name) {
        this.name = name;
    }

    public Location(String name, boolean isAmbiguous) {

        this.name = name;
        this.isAmbiguous = isAmbiguous;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public boolean isAmbiguous() {

        return isAmbiguous;
    }

    public void setAmbiguous(boolean isAmbiguous) {

        this.isAmbiguous = isAmbiguous;
    }


    public String getId() {

        return id;
    }


    public void setId(String id) {

        this.id = id;
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        Location other = (Location) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
