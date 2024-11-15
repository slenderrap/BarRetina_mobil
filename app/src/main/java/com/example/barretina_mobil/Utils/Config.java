package com.example.barretina_mobil.Utils;

import java.net.URI;

public class Config {
    private URI serverUrl;
    private String place;

    public URI getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(URI serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}