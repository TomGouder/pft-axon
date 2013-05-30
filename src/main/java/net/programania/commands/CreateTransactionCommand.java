package net.programania.commands;

import net.programania.aggregates.TransactionAggregate;
import org.axonframework.commandhandling.annotation.TargetAggregateIdentifier;

import java.util.UUID;

public class CreateTransactionCommand {
  @TargetAggregateIdentifier
  private final String uuid;
  private final TransactionAggregate.Status status;

  private CreateTransactionCommand(String uuid) {
    this.uuid = uuid;
    this.status = TransactionAggregate.Status.ONGOING;
  }

  public static CreateTransactionCommand factory() {
    return new CreateTransactionCommand(UUID.randomUUID().toString());
  }

  public String getUuid() {
    return uuid;
  }

  public TransactionAggregate.Status getStatus() {
    return status;
  }
}
