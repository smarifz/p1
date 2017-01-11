package com.p1;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by arifzaidi on 9/17/16.
 */
public class Tasks {

    Connection conn = null;
    Statement stmt = null;

    public Tasks(Connection conn) {
        this.conn = conn;
    }

    public boolean login(String email, String password) {
        ResultSet rs = null;
        ArrayList<String> result = new ArrayList<String>();

        try {
            stmt = conn.createStatement();
            String sql = "SELECT * from customers where email='"+email+"' and password='"+password+"';";
            rs = stmt.executeQuery(sql);
            //Extract data
            while (rs.next()) {
                //Retrieve by column name
                String fname = rs.getString("first_name");
                if (!fname.isEmpty()) {
                    System.out.println("Login successful. Welcome!");
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("Incorrect login credentials.");
        return false;
    }


    public ArrayList<String> getMovies(String firstname, String lastname) {
        ResultSet rs = null;
        ArrayList<String> result = new ArrayList<String>();

        try {
            stmt = conn.createStatement();
            String whereCondition = getWhereCondition(firstname, lastname);
            String sql = "SELECT * from movies where id IN (SELECT movie_id FROM stars_in_movies where star_id IN( SELECT id FROM moviedb.stars where " + whereCondition;
            rs = stmt.executeQuery(sql);
            //Extract data
            while (rs.next()) {
                String fullString = "";
                //Retrieve by column name
                int id = rs.getInt("id");
                String title = rs.getString("title");
                int year = rs.getInt("year");
                String director = rs.getString("director");
                String banner_url = rs.getString("banner_url");
                String trailer_url = rs.getString("trailer_url");
                //Display values
                fullString += "ID: " + id;
                fullString += "| Title: " + title;
                fullString += "| Year: " + year;
                fullString += "| Director: " + director;
                fullString += "| Banner URL: " + banner_url;
                fullString += "| Trailer URL: " + trailer_url;
                result.add(fullString);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return result;

    }

    public ArrayList<String> getMovies(int starId) {
        ResultSet rs = null;
        ArrayList<String> result = new ArrayList<String>();

        try {
            stmt = conn.createStatement();
            String sql = "SELECT * from movies where id IN (SELECT movie_id FROM stars_in_movies where star_id IN( SELECT id FROM moviedb.stars where id = " + starId + "))";
            rs = stmt.executeQuery(sql);
            //Extract data
            while (rs.next()) {
                String fullString = "";
                //Retrieve by column name
                int id = rs.getInt("id");
                String title = rs.getString("title");
                int year;
                year = rs.getInt("year");
                String director = rs.getString("director");
                String banner_url = rs.getString("banner_url");
                String trailer_url = rs.getString("trailer_url");
                //Display values
                fullString += "ID: " + id;
                fullString += "| Title: " + title;
                fullString += "| Year: " + year;
                fullString += "| Director: " + director;
                fullString += "| Banner URL: " + banner_url;
                fullString += "| Trailer URL: " + trailer_url;
                result.add(fullString);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return result;

    }

    public void addStar(String firstname, String lastname) {
        ArrayList<String> result = new ArrayList<String>();
        int i = new Random().nextInt(10000);

        try {
            stmt = conn.createStatement();
            String sql = "INSERT INTO stars VALUES(" + i + ",'" + firstname + "','" + lastname + "', NULL, NULL)";
            stmt.executeUpdate(sql);
            System.out.println("Inserted records into the table...");

        } catch (Exception e) {
            System.out.println(e);
        }

        return;
    }

    public void addCustomer(String firstname, String lastname, String address, String email, String password, int cc_id) {
        int i = new Random().nextInt(10000);

        if (!checkIfCCExist(cc_id, firstname, lastname)) {
            System.out.println("Error adding customer: Credit card does not exist.");
            return;
        }

        try {
            stmt = conn.createStatement();
            String sql = "INSERT INTO customers VALUES(" + i + ",'" + firstname + "','" + lastname + "'," + cc_id + ",'" + address + "', '" + email + "', '" + password + "')";
            stmt.executeUpdate(sql);
            System.out.println("Inserted records into the table...");

        } catch (Exception e) {
            System.out.println(e);
        }

        System.out.println("Customer added.");
        return;
    }

    public void displayMetadata() {
        ResultSet rs = null;
        ResultSet rs_inner = null;
        Statement stmt_inner = null;

        try {
            stmt = conn.createStatement();
            String sql = "SELECT * FROM information_schema.tables WHERE table_schema='moviedb'";
            rs = stmt.executeQuery(sql);
            //Extract data
            while (rs.next()) {
                stmt_inner = conn.createStatement();
                String tableName = rs.getString("TABLE_NAME");
                System.out.println("Table Name: " + tableName);
                System.out.println("Attributes and Type:");

                //Retrieve by column name
                rs_inner = stmt_inner.executeQuery("DESCRIBE " + tableName);
                while (rs_inner.next()) {
                    System.out.println("   " + rs_inner.getString("Field") + " | " + rs_inner.getString("Type"));
                }
                System.out.println("");
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        System.out.println("");
        return;
    }

    public boolean checkIfCCExist(int cc_id, String firstname, String lastname) {
        ResultSet rs = null;
        int rowCount = 0;

        try {
            stmt = conn.createStatement();
            String sql = "SELECT count(*) from creditcards where id =" + cc_id + " and first_name ='" + firstname + "' and last_name='" + lastname + "'";
            rs = stmt.executeQuery(sql);
            //Extract data
            while (rs.next()) {
                rowCount = Integer.parseInt(rs.getString("count(*)"));
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        if (rowCount > 0)
            return true;
        else
            return false;

    }

    public void deleteCustomer(String firstname, String lastname) {
        ResultSet rs = null;
        int id = 0;
        try {
            stmt = conn.createStatement();
            String sql_getId = "DELETE from customers where cc_id IN (SELECT id from creditcards where first_name='" + firstname + "' and last_name='" + lastname + "')";
            stmt.executeUpdate(sql_getId);
            System.out.println("Customer " + id + " deleted from database.");
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public String getWhereCondition(String firstname, String lastname) {
        String whereConditionStr = null;

        if (firstname.length() > 0 && lastname.length() > 0) { //if both exist
            whereConditionStr = "first_name = '" + firstname + "' and last_name='" + lastname + "'))";
        } else if (firstname.length() <= 0 && lastname.length() > 0) { //if ONLY lastname exist
            whereConditionStr = "last_name='" + lastname + "'))";
        } else if (firstname.length() > 0 && lastname.length() <= 0) { //if ONLY firstname exist
            whereConditionStr = "first_name='" + firstname + "'))";
        } else
            whereConditionStr = "";

        return whereConditionStr;
    }



    public void executeQuery(String query) {
        ResultSet rs = null;
        int id = 0;
        try {
            stmt = conn.createStatement();
            String arr[] = query.split(" ", 2);
            String queryTypeRaw = arr[0];
            String queryType = queryTypeRaw.toUpperCase();

            if (queryType.toUpperCase().equals("SELECT")) {
                rs = stmt.executeQuery(query);
                customerQuery_Select(rs);

            } else if (queryType.toUpperCase().equals("UPDATE")) {
                int updatedRows = stmt.executeUpdate(query);
                System.out.println("Result:");
                System.out.println("Updated "+updatedRows+ " rows.");

            } else if (queryType.toUpperCase().equals("INSERT")) {
                int updatedRows = stmt.executeUpdate(query);
                System.out.println("Result:");
                System.out.println("Updated "+updatedRows+ " rows.");

            } else if (queryType.toUpperCase().equals("DELETE")) {
                int updatedRows = stmt.executeUpdate(query);
                System.out.println("Result:");
                System.out.println("Updated "+updatedRows+ " rows.");
            }

        } catch (Exception e) {
            System.out.println("Error: "+e.getMessage());
        }
    }

    public void customerQuery_Select(ResultSet rs) throws Exception {
        int numColumns = rs.getMetaData().getColumnCount();

        //Get column names
        for (int i = 1; i <= numColumns; i++) {
            System.out.print(rs.getMetaData().getColumnName(i) + " | ");
        }

        System.out.println("\n-----------------------");

        //Extract data
        while (rs.next()) {
            System.out.println("");

            //Get column data
            for (int i = 1; i <= numColumns; i++) {
                System.out.print(rs.getObject(i) + " | ");
            }
        }
    }

//    public void customerQuery_Update(ResultSet rs) throws Exception {
//        stmt.executeUpdate(sql);
//    }
}