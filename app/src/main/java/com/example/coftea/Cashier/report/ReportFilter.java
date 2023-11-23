package com.example.coftea.Cashier.report;

import com.example.coftea.Cashier.order.CartItem;

import java.util.ArrayList;
import java.util.Date;

public class ReportFilter {
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Date getFilterStart() {
        return filterStart;
    }

    public void setFilterStart(Date filterStart) {
        this.filterStart = filterStart;
    }

    public Date getFilterEnd() {
        return filterEnd;
    }

    public void setFilterEnd(Date filterEnd) {
        this.filterEnd = filterEnd;
    }
    private String label;
    private Date filterStart, filterEnd;

    public ReportFilter(String label, Date filterStart, Date filterEnd){
        this.label = label;
        this.filterStart = filterStart;
        this.filterEnd = filterEnd;
    }

    public boolean isWithinThisDates(Long createdAt){
        Date createdDate = new Date(createdAt);

        return (createdDate.after(filterStart) || createdDate.equals(filterStart))
                && (createdDate.before(filterEnd) || createdDate.equals(filterEnd));
    }

    public ArrayList<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(ArrayList<CartItem> cartItems) {
        this.cartItems = cartItems;
    }
    public void addCartItems(ArrayList<CartItem> cartItems) {
        if(this.cartItems == null)
            this.cartItems = cartItems;
        else
            this.cartItems.addAll(cartItems);
    }
    public void addTotalPrice(Double totalPrice) {
        if(this.totalPrice == null)
            this.totalPrice = totalPrice;
        else
            this.totalPrice += totalPrice;
    }
    private ArrayList<CartItem> cartItems;

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    private Double totalPrice;
}
