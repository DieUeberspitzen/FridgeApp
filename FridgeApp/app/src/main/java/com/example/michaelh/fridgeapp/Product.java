package com.example.michaelh.fridgeapp;

/**
 * Created by MichaelH on 04.06.2015.
 */
public class Product {

    private long _id;
    private String _name;
    private String _code;
    private String _image;

    public String get_image() {
        return _image;
    }

    public void set_image(String _image) {
        this._image = _image;
    }

    public Product(String name) {
     this._name = name;
    }

    public Product(long id, String name, String code) {
        this._id = id;
        this._name = name;
        this._code = code;
    }

    public Product(String name, String code) {
        this._name = name;
        this._code = code;
    }

    public void setID(long id) {
        this._id = id;
    }

    public long getID() {
        return this._id;
    }

    public void setName(String name) {
        this._name = name;
    }

    public String getProductName() {
        return this._name;
    }

    public void setCode(String code) {
        this._code = code;
    }

    public String getCode() {
        return this._code;
    }
}
