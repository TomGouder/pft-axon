package net.programania.h2;

import net.programania.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TransactionDao {
  private static final Logger log = LoggerFactory.getLogger(TransactionDao.class);

  public void insert(Transaction transaction) throws Exception {
    Connection conn = H2ConnectionPool.iCanHaz();
    insertDelay();
    PreparedStatement statement = conn.prepareStatement("INSERT INTO transactions(uuid, status) VALUES(?, ?)");
    statement.setString(1, transaction.uuid);
    statement.setString(2, transaction.status);
    int affectedRows = statement.executeUpdate();
    statement.close();
    H2ConnectionPool.returnConnection(conn);
    if (1 != affectedRows)
      throw new Exception("No se ha insertado ninguna transacción");
  }

  public void update(Transaction transaction) throws Exception {
    Connection conn = H2ConnectionPool.iCanHaz();
    insertDelay();
    PreparedStatement statement = conn.prepareStatement("UPDATE transactions SET status = ? WHERE uuid = ?");
    statement.setString(1, transaction.status);
    statement.setString(2, transaction.uuid);
    int affectedRows = statement.executeUpdate();
    statement.close();
    H2ConnectionPool.returnConnection(conn);
    if (1 != affectedRows)
      throw new Exception("No se ha actualizado ninguna transacción");
  }

  public List<Transaction> select() {
    List<Transaction> transactions = new LinkedList<>();
    try {
      Connection conn = H2ConnectionPool.iCanHaz();
      PreparedStatement statement = conn.prepareStatement("SELECT uuid, status FROM transactions");
      ResultSet resultSet = statement.executeQuery();
      final List<Map<String, String>> rows = rowsAsMaps(resultSet);
      statement.close();
      H2ConnectionPool.returnConnection(conn);
      for (Map<String, String> row : rows) {
        Transaction t = new Transaction();
        t.uuid = row.get("UUID");
        t.status = row.get("STATUS");
        transactions.add(t);
      }
    } catch (Exception e) {
      log.error("Ha fallado la obtención de transacciones", e);
    }
    return transactions;
  }

  private List<Map<String, String>> rowsAsMaps(ResultSet rs) throws SQLException {
    ResultSetMetaData md = rs.getMetaData();
    int columns = md.getColumnCount();
    List<Map<String, String>> rows = new LinkedList<>();
    while (rs.next()) {
      Map<String, String> row = new HashMap<>();
      for (int i = 1; i <= columns; i++)
        row.put(md.getColumnName(i), rs.getString(i));
      rows.add(row);
    }
    return rows;
  }

  private void insertDelay() throws InterruptedException {
    final long millis = (long) (Math.random() * 5000);
    Thread.sleep(millis);
  }
}
