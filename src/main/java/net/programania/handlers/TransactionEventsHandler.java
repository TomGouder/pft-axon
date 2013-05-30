package net.programania.handlers;

import net.programania.events.TransactionCommitedEvent;
import net.programania.events.TransactionCreatedEvent;
import net.programania.events.TransactionRolledBackEvent;
import net.programania.h2.TransactionDao;
import net.programania.model.Transaction;
import org.axonframework.eventhandling.annotation.EventHandler;

public class TransactionEventsHandler {
  private final TransactionDao dao;

  public TransactionEventsHandler(TransactionDao dao) {
    this.dao = dao;
  }

  public static TransactionEventsHandler factory() {
    return new TransactionEventsHandler(new TransactionDao());
  }

  @EventHandler
  public void handle(TransactionCreatedEvent event) throws Exception {
    Transaction t = new Transaction();
    t.uuid = event.getUuid();
    t.status = event.getStatus().toString();
    dao.insert(t);
  }

  @EventHandler
  public void handle(TransactionCommitedEvent event) throws Exception {
    Transaction t = new Transaction();
    t.uuid = event.getUuid();
    t.status = event.getStatus().toString();
    dao.update(t);
  }

  @EventHandler
  public void handle(TransactionRolledBackEvent event) throws Exception {
    Transaction t = new Transaction();
    t.uuid = event.getUuid();
    t.status = event.getStatus().toString();
    dao.update(t);
  }
}
