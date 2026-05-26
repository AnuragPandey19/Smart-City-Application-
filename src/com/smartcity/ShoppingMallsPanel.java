package com.smartcity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ShoppingMallsPanel extends SearchableTablePanel {
    public ShoppingMallsPanel() {
        super(
            "Shopping Malls",
            "Browse malls by location with opening and closing hours.",
            new String[]{"Mall", "Location", "Opens", "Closes"},
            "SELECT mall_name, location, opening_time, closing_time "
          + "FROM shopping_mall WHERE location LIKE ?"
        );
    }

    @Override
    protected Object[] map(ResultSet rs) throws SQLException {
        return new Object[]{
            rs.getString("mall_name"),
            rs.getString("location"),
            rs.getString("opening_time"),
            rs.getString("closing_time")
        };
    }
}
