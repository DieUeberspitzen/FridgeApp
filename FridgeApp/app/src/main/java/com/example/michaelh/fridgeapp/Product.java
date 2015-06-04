package com.example.michaelh.fridgeapp;

/**
 * Created by MichaelH on 04.06.2015.
 */
public class Product {

    private int _id;
    private String _name;
    private String _code;

    public Product() {

    }

    public Product(int id, String name, String code) {
        this._id = id;
        this._name = name;
        this._code = code;
    }

    public Product(String name, String code) {
        this._name = name;
        this._code = code;
    }

    public void setID(int id) {
        this._id = id;
    }

    public int getID() {
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
