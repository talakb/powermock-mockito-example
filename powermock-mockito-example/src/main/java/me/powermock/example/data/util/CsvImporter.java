package me.powermock.example.data.util;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.postgresql.Driver;

import me.powermock.example.data.model.entity.Customer;

public class CsvImporter {
  public static final String JDBC_DRIVER = "org.postgresql.Driver";
  public static final String JDBC_URL = "jdbc:postgresql://localhost:5432/jcpenny";
  public static final String USERNAME = "****";
  public static final String PASS = "***";

  public static void main(String... args) throws Exception {
    // params: csvFileLocation
    // table name
    // columns
    // String filePath = args[0];
    // String tableName = args[1];
    // List<String> columns = getColumnsList(args);


    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    try {

      DriverManager.registerDriver(new Driver());
      conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASS);
      conn.setAutoCommit(false);
      String sql =
          "insert into public.customer  (first_name,mid_name,last_name,email,phone_no) values (?,?,?,?,?)";



      // Create the CSVFormat object
      CSVFormat format = CSVFormat.RFC4180.withHeader().withDelimiter(',');

      // initialize the CSVParser object
      CSVParser parser = new CSVParser(
          new FileReader("C:\\Users\\AdminPC\\Downloads\\us-500\\us-500.csv"), format);

      List<Customer> customers = new ArrayList<Customer>();

      for (CSVRecord record : parser) {
        Customer customer = new Customer();
        customer.setFirstName(record.get("first_name"));
        customer.setLastName(record.get("last_name"));
        customer.setMidInit(null);
        customer.setEmail(record.get("email"));
        customer.setPhoneNo(record.get("phone1"));
        customers.add(customer);
      }
      parser.close();

      stmt = conn.prepareStatement(sql);

      for (Customer cust : customers) {
        stmt.setString(1, cust.getFirstName());
        stmt.setString(2, cust.getMidInit());
        stmt.setString(3, cust.getLastName());
        stmt.setString(4, cust.getEmail());
        stmt.setString(5, cust.getPhoneNo());

        // System.err.println(stmt.executeUpdate());
      }
      // close the parser


      //
      // while (rs.next()) {
      // System.out.println(rs.getString("first_name") + "\t" + rs.getString("last_name") + "\t"
      // + rs.getString("phone_no"));
      // }

      conn.commit();
      conn.close();
      stmt.close();
      // rs.close();

    } catch (SQLException ex) {
      if (null != conn) {
        conn.rollback();
      }
      ex.printStackTrace();
    } finally {
      if (null != conn && !conn.isClosed()) {
        conn.close();
      }

      if (null != rs && !rs.isClosed()) {
        rs.close();
      }

      if (null != stmt && !stmt.isClosed()) {
        stmt.close();
      }
    }


  }

  private static List<String> getColumnsList(String[] args) {
    List<String> columnNames = Collections.emptyList();

    for (int i = 2; i < args.length; i++) {
      columnNames.add(args[i]);

    }


    return columnNames;
  }
}
