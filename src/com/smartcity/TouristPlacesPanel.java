package com.smartcity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TouristPlacesPanel extends SearchableTablePanel {
    public TouristPlacesPanel() {
        super(
            "Places to Visit",
            "Discover tourist spots — descriptions, entry fees, and opening hours.",
            new String[]{"Place", "Location", "Description", "Entry Fee", "Opening Hours"},
            "SELECT place_name, location, description, entry_fee, opening_hours "
          + "FROM tourism WHERE location LIKE ?"
        );
    }

    @Override
    protected Object[] map(ResultSet rs) throws SQLException {
        return new Object[]{
            rs.getString("place_name"),
            rs.getString("location"),
            rs.getString("description"),
            rs.getDouble("entry_fee"),
            rs.getString("opening_hours")
        };
    }
}
