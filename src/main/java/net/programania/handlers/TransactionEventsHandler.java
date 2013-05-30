package net.programania.handlers;

import net.programania.events.TransactionCommitedEvent;
import net.programania.events.TransactionCreatedEvent;
import net.programania.events.TransactionRolledBackEvent;
import net.programania.h2.TransactionDao;
import net.programania.model.Transaction;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionEventsHandler {
  private static final Logger log = LoggerFactory.getLogger(TransactionEventsHandler.class);
  private final TransactionDao dao;

  public TransactionEventsHandler(TransactionDao dao) {
    this.dao = dao;
  }

  public static TransactionEventsHandler factory() {
    return new TransactionEventsHandler(new TransactionDao());
  }

  @EventHandler
  public void handle(TransactionCreatedEvent event) throws Exception {
    log.info("Procesando evento de creación");
    Transaction t = new Transaction();
    t.uuid = event.getUuid();
    t.status = event.getStatus().toString();
    dao.insert(t);
    log.info("Procesado evento de creación");
  }

  @EventHandler
  public void handle(TransactionCommitedEvent event) throws Exception {
    log.info("Procesando evento de commit");
    Transaction t = new Transaction();
    t.uuid = event.getUuid();
    t.status = event.getStatus().toString();
    dao.update(t);
    log.info("Procesado evento de commit");
  }

  @EventHandler
  public void handle(TransactionRolledBackEvent event) throws Exception {
    log.info("Procesando evento de rollback");
    Transaction t = new Transaction();
    t.uuid = event.getUuid();
    t.status = event.getStatus().toString();
    dao.update(t);
    log.info("Procesado evento de rollback");
  }
}
