package net.programania.commands;

import net.programania.aggregates.TransactionAggregate;
import org.axonframework.commandhandling.annotation.TargetAggregateIdentifier;

public class RollBackTransactionCommand {
  @TargetAggregateIdentifier
  private final String uuid;
  private final TransactionAggregate.Status status;

  public RollBackTransactionCommand(String uuid) {
    this.uuid = uuid;
    this.status = TransactionAggregate.Status.ROLLED_BACK;
  }

  public String getUuid() {
    return uuid;
  }

  public TransactionAggregate.Status getStatus() {
    return status;
  }
}
