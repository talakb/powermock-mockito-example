package me.powermock.example.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.postgresql.Driver;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import me.powermock.example.data.model.entity.Customer;
import me.powermock.example.jdbc.JDBCExample;

@RunWith(PowerMockRunner.class)
@PrepareForTest({JDBCExample.class, DriverManager.class})
// JDBCExample needs to be prepared for test because of the private static final variables used in
// the class.
// Calling DriverManager.class is optional as all static calls mocked using
// PowerMockito.mockStatic(DriverManager.class);
public class JDBCExampleTest {

  @Mock
  Connection mockConnection;

  @Mock
  PreparedStatement mockPreparedStatement;

  @Mock
  ResultSet mockResultSet;



  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void mainMethodShouldCreateConnectionPreparedStatementAndResultSetAndReturnCustomerObject()
      throws Exception {
    // Enable mocking static methods under DriverManager.
    PowerMockito.mockStatic(DriverManager.class);

    // Mock static void method call from DriverManager class
    PowerMockito.doNothing().when(DriverManager.class);
    DriverManager.registerDriver(Mockito.any(Driver.class));

    // Mock static method call that takes parameter and has return value.
    when(DriverManager.getConnection(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
        .thenReturn(mockConnection);

    // Mock instance (non-static) method calls that takes parameter.
    when(mockConnection.prepareStatement(Mockito.anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    // Mock instance (non-static) method calls that takes parameter and called in a loop in the
    // actual method impl.
    when(mockResultSet.next()).thenReturn(true).thenReturn(false);
    when(mockResultSet.getString("first_name")).thenReturn("John");
    when(mockResultSet.getString("last_name")).thenReturn("Smith");

    Customer actualCustomerObj = JDBCExample.getCustomerbyId(0);

    // Verify method calls
    // Verify static void method (DriverManager.registerDriver) gets called once.
    PowerMockito.verifyStatic(Mockito.times(1));
    DriverManager.registerDriver(Mockito.any(Driver.class));

    // Verify Mock static method call that has return value (DriverManager.getConnection) gets
    // called
    // once
    PowerMockito.verifyStatic(Mockito.times(1));
    DriverManager.getConnection(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

    // Verify mocked object instance calls.
    Mockito.verify(mockConnection, Mockito.times(1)).prepareStatement(Mockito.anyString());
    Mockito.verify(mockConnection, Mockito.times(1)).setAutoCommit(false);
    Mockito.verify(mockConnection, Mockito.times(1)).prepareStatement(Mockito.anyString());
    Mockito.verify(mockPreparedStatement, Mockito.times(1)).setLong(1, 0);
    Mockito.verify(mockPreparedStatement, Mockito.times(1)).executeQuery();
    Mockito.verify(mockResultSet, Mockito.times(2)).next();
    Mockito.verify(mockConnection, Mockito.times(1)).commit();
    Mockito.verify(mockConnection, Mockito.times(2)).close();
    Mockito.verify(mockPreparedStatement, Mockito.times(2)).close();
    Mockito.verify(mockResultSet, Mockito.times(2)).close();

    // Verify returned object instance
    assertNotNull("Returned obj. should not be null.", actualCustomerObj);
    assertEquals("Returned first name should match.", "John", actualCustomerObj.getFirstName());
    assertEquals("Returned last name should match.", "Smith", actualCustomerObj.getLastName());
  }

}
