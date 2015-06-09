package com.example.michaelh.fridgeapp;

import java.util.Date;


/**
 * Created by MichaelH on 04.06.2015.
 */
public class Product {

    private long _id;
    private String _title;
    private String _description;
    private String _image;
    private String _url;
    private String _expiry;






    public Product(String title) {
     this._title = title;
    }

    public Product(long id, String title, String description, String image, String url, String expiry) {
        this._id = id;
        this._title = title;
        this._description = description;
        this._image = image;
        this._url = url;
        this._expiry = expiry;
    }

    public Product(String title, String description) {
        this._title = title;
        this._description = description;
    }

    public void setID(long id) {
        this._id = id;
    }

    public long getID() {
        return this._id;
    }

    public void setTitle(String title) {
        this._title = title;
    }

    public String getTitle() {
        return this._title;
    }

    public void setDescription(String description) {
        this._description = description;
    }

    public String getDescription() {
        return this._description;
    }

    public String get_image() {
        return _image;
    }

    public void set_image(String _image) {
        this._image = _image;
    }

    public String get_url() { return _url;  }

    public void set_url(String _url) { this._url = _url; }

    public String get_expiry() { return _expiry; }

    public void set_expiry(String _expiry) { this._expiry = _expiry; }
}

