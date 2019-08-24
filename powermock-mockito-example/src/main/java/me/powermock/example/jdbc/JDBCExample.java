package me.powermock.example.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.postgresql.Driver;

import me.powermock.example.data.model.entity.Customer;

public class JDBCExample {
  public static final String JDBC_DRIVER = "org.postgresql.Driver";
  public static final String JDBC_URL = "jdbc:postgresql://localhost:5432/jcpenny";
  public static final String USERNAME = "*****";
  public static final String PASS = "****";

  public static void main(String[] args) throws Exception {

  }

  public static Customer getCustomerbyId(int customerId) throws Exception {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    Customer customer = null;

    try {

      DriverManager.registerDriver(new Driver());
      conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASS);
      String sql = "SELECT * FROM public.customer where id = ?";
      conn.setAutoCommit(false);
      stmt = conn.prepareStatement(sql);
      stmt.setLong(1, customerId);
      rs = stmt.executeQuery();

      while (rs.next()) {
        customer = new Customer();
        customer.setFirstName(rs.getString("first_name"));
        customer.setLastName(rs.getString("last_name"));
      }

      conn.commit();
      conn.close();
      stmt.close();
      rs.close();

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

    return customer;

  }
}
