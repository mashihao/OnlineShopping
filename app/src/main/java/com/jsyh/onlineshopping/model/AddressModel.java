package com.jsyh.onlineshopping.model;

import java.util.List;

/**
 * Created by sks on 2015/9/24.
 */
public class AddressModel extends BaseModel {
    private List<Address> data;

    public List<Address> getData() {
        return data;
    }

    public void setData(List<Address> data) {
        this.data = data;
    }
}
