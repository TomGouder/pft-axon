package net.programania.h2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;

public class H2ConnectionPool {
  private static final Logger log = LoggerFactory.getLogger(H2ConnectionPool.class);
  private static Connection connection;

  static {
    try {
      Class.forName("org.h2.Driver");
      connection = DriverManager.getConnection("jdbc:h2:test");
      connection.createStatement().execute("CREATE TABLE IF NOT EXISTS transactions(uuid VARCHAR, status VARCHAR);");
//      ResultSet tables = connection.getMetaData().getTables(connection.getCatalog(), null, null, null);
//      boolean exists = false;
//      while (tables.next()) {
//        if ("test" == tables.getString(2) && "transactions" == tables.getString(3))
//          exists = true;
//      }
//      if (!exists) {
//        Statement statement = ;
//
//      }
    } catch (Exception e) {
      log.error("No hemos podido crear una conexión de H2", e);
      throw new RuntimeException(e); // EXPLODE!
    }
  }

  public static Connection iCanHaz() {
    while (true) {
      synchronized (connection) {
        if (null != connection)
          return connection;
      }
      try {
        Thread.sleep(100);
      } catch (InterruptedException ignored) {
        log.warn("Alguien tiene prisa", ignored);
      }
    }
  }

  public static void returnConnection(Connection conn) {
    connection = conn;
  }
}
