package com.smartcity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RestaurantsPanel extends SearchableTablePanel {
    public RestaurantsPanel() {
        super(
            "Restaurants",
            "Search restaurants by city or area — cuisine, price range, and contact.",
            new String[]{"Restaurant", "Location", "Cuisine", "Price Range", "Contact"},
            "SELECT restaurant_name, location, cuisine, price_range, contact "
          + "FROM restaurants WHERE location LIKE ?"
        );
    }

    @Override
    protected Object[] map(ResultSet rs) throws SQLException {
        return new Object[]{
            rs.getString("restaurant_name"),
            rs.getString("location"),
            rs.getString("cuisine"),
            rs.getString("price_range"),
            rs.getString("contact")
        };
    }
}
