package com.example.location_data;

public class State extends Location {

    public State(String name, boolean isAmbiguous) {

        super(name, isAmbiguous);
    }

    @Override
    public String toString() {

        return "State [name=" + name + "]";
    }
}
