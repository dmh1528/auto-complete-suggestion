package com.example.location_data;

public class District extends Location {

    public District(String name, boolean isAmbiguous) {

        super(name, isAmbiguous);
    }

    private State state;

    public State getState() {

        return state;
    }

    public void setState(State state) {

        this.state = state;
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
        District other = (District) obj;
        if (state == null) {
            if (other.state != null)
                return false;
        } else if (!state.equals(other.state))
            return false;
        return true;
    }

    @Override
    public String toString() {

        return "District [name=" + name + ", state=" + state + "]";
    }

}
