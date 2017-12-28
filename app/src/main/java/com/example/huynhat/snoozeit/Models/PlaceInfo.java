package com.example.huynhat.snoozeit.Models;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by huynhat on 2017-12-22.
 */

public class PlaceInfo {
    private String name;
    private String address;
    private String phoneNumber;
    private String id;
    private Uri uriWebsite;
    private LatLng latLng;
    private float rating;
    private String attributions;

    public PlaceInfo(String name, String address, String phoneNumber, String id, Uri uriWebsite,
                     LatLng latLng, float rating, String attributions) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.id = id;
        this.uriWebsite = uriWebsite;
        this.latLng = latLng;
        this.rating = rating;
        this.attributions = attributions;
    }

    public PlaceInfo(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Uri getUriWebsite() {
        return uriWebsite;
    }

    public void setUriWebsite(Uri uriWebsite) {
        this.uriWebsite = uriWebsite;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getAttributions() {
        return attributions;
    }

    public void setAttributions(String attributions) {
        this.attributions = attributions;
    }

    @Override
    public String toString() {
        return "PlaceInfo{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", id='" + id + '\'' +
                ", uriWebsite=" + uriWebsite +
                ", latLng=" + latLng +
                ", rating=" + rating +
                ", attributions='" + attributions + '\'' +
                '}';
    }
}
