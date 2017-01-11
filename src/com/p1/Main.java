package com.p1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;

public class Main {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://192.168.99.100:3306/moviedb";

    //  Database credentials
    static final String USER = "root";
    static final String PASS = "rootpassword";


    public static Connection connect() {
        Connection conn = null;
        try {
            //Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            //Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connection successful!\n");

        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        }

        return conn;
    }

    public static void cleanUp(ResultSet rs, Statement stmt, Connection conn) {
        try {
            //Clean-up environment
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    public static void main(String[] args){
        Tasks t = new Tasks(connect());
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int choice = 0;
        boolean loggedIn = false;
        try {
            while (true) {
                System.out.println("\n------------------");
                System.out.println("Login ");
                System.out.println("------------------");
                System.out.println("Please enter email:");
                String login_email = br.readLine();
                System.out.println("Please enter password:");
                String login_password = br.readLine();
                loggedIn = t.login(login_email, login_password);
                while (loggedIn) {
                    System.out.println("\n------------------");
                    System.out.println("MAIN MENU: ");
                    System.out.println("------------------");
                    System.out.println("Please enter a choice: ");
                    System.out.println("1. Print movies of a given star.");
                    System.out.println("2. Insert a new star into the database.");
                    System.out.println("3. Insert a new customer the database.");
                    System.out.println("4. Delete a customer from the database.");
                    System.out.println("5. Print the metadata of the database.");
                    System.out.println("6. Enter custom query.");
                    System.out.println("7. Exit menu.");
                    System.out.println("8. Exit program.");
                    System.out.println("------------------: ");

                    choice = Integer.parseInt(br.readLine());
                    System.out.println("You Entered: " + choice);

                    if (choice == 1) { //1
                        int method = 0;
                        while (method < 1 || method > 2) {
                            System.out.print("Would you like to search using name(1) or ID(2).");
                            System.out.print("Please enter 1 for name and 2 for ID: ");
                            method = Integer.parseInt(br.readLine());
                        }

                        if (method == 1) { //Insert Using names
                            String firstname = "";
                            String lastname = "";
                            while (lastname.length() <= 0 && firstname.length() <= 0) {
                                System.out.println("Please enter FIRST name. Press enter to leave it blank:");
                                firstname = br.readLine();
                                System.out.println("Please enter LAST name name. Press enter to leave it blank:");
                                lastname = br.readLine();
                            }

                            ArrayList<String> movies = t.getMovies(firstname, lastname);
                            for (String movie : movies) {
                                System.out.println(movie);
                            }

                        } else if (method == 2) { //Insert Using ID
                            int id = 0;
                            while (id <= 0) {
                                System.out.println("Please enter id:");
                                id = Integer.parseInt(br.readLine());
                            }

                            ArrayList<String> movies = t.getMovies(id);
                            for (String movie : movies) {
                                System.out.println(movie);
                            }
                        } else {
                            System.out.println("Invalid. Please pick 1 or 2.");
                        }


                    } else if (choice == 2) { //2
                        String firstname = "";
                        String lastname = "";
                        while (lastname.length() <= 0 && firstname.length() <= 0) {
                            System.out.println("Please enter FIRST name.");
                            firstname = br.readLine();
                            System.out.println("Please enter LAST name name.");
                            lastname = br.readLine();
                        }

                        //If the customer has a single name, add it as his last_name and assign an empty string ("") to first_name
                        if (firstname.length() > 0 && lastname.length() == 0) {
                            lastname = firstname;
                            firstname = "";
                        }

                        //Perform task
                        t.addStar(firstname, lastname);
                    } else if (choice == 3) { //3
                        String firstname, lastname, address, email, password;
                        int cc_id;
                        System.out.println("Please enter FIRST name.");
                        firstname = br.readLine();
                        System.out.println("Please enter LAST name.");
                        lastname = br.readLine();
                        System.out.println("Please enter credit card id.");
                        cc_id = Integer.parseInt(br.readLine());
                        System.out.println("Please enter address.");
                        address = br.readLine();
                        System.out.println("Please enter email.");
                        email = br.readLine();
                        System.out.println("Please enter password.");
                        password = br.readLine();

                        //If the customer has a single name, add it as his last_name and assign an empty string ("") to first_name
                        if (firstname.length() > 0 && lastname.length() == 0) {
                            lastname = firstname;
                            firstname = "";
                        }

                        //Perform task
                        t.addCustomer(firstname, lastname, address, email, password, cc_id);

                    } else if (choice == 4) { //4
                        String firstname, lastname;
                        System.out.println("Please enter FIRST name.");
                        firstname = br.readLine();
                        System.out.println("Please enter LAST name.");
                        lastname = br.readLine();

                        //Perform task
                        t.deleteCustomer(firstname, lastname);
                    } else if (choice == 5) { //5
                        //Perform task
                        t.displayMetadata();
                    } else if (choice == 6) { //6
                        System.out.println("Please enter query.");
                        String query = br.readLine();
                        //Perform task
                        t.executeQuery(query);
                    } else if (choice == 7) { //6
                        loggedIn = false;
                    } else if (choice == 8) {
                        System.out.println("Goodbye.");
                        System.exit(0);
                    }


                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


//        System.out.println("Goodbye!");
    }//end main
}//end


