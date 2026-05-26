package com.smartcity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HotelsPanel extends SearchableTablePanel {
    public HotelsPanel() {
        super(
            "Hotels",
            "Find hotels by city or area — see contact, price range, and rating.",
            new String[]{"Hotel Name", "Location", "Contact", "Price Range", "Rating"},
            "SELECT hotel_name, location, contact, price_range, rating "
          + "FROM hotels WHERE location LIKE ?"
        );
    }

    @Override
    protected Object[] map(ResultSet rs) throws SQLException {
        return new Object[]{
            rs.getString("hotel_name"),
            rs.getString("location"),
            rs.getString("contact"),
            rs.getString("price_range"),
            rs.getFloat("rating")
        };
    }
}
