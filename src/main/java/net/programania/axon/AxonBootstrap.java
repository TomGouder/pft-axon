package net.programania.axon;

import com.mongodb.Mongo;
import net.programania.aggregates.TransactionAggregate;
import net.programania.handlers.TransactionEventsHandler;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.commandhandling.annotation.AggregateAnnotationCommandHandler;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.DefaultCommandGateway;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.SimpleEventBus;
import org.axonframework.eventhandling.annotation.AnnotationEventListenerAdapter;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventstore.EventStore;
import org.axonframework.eventstore.mongo.DefaultMongoTemplate;
import org.axonframework.eventstore.mongo.MongoEventStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;

public class AxonBootstrap {
  private static final Logger log = LoggerFactory.getLogger(AxonBootstrap.class);

  public static CommandGateway bootstrap() {
    // let's start with the Command Bus
    CommandBus commandBus = new SimpleCommandBus();
    // the CommandGateway provides a friendlier API
    CommandGateway commandGateway = new DefaultCommandGateway(commandBus);
    // we'll store Events on the FileSystem, in the "events/" folder
    Mongo mongo = null;
    try {
      mongo = new Mongo("127.0.0.1");
    } catch (UnknownHostException e) {
      log.error("No hemos podido enchufarnos a Mongo", e);
      throw new RuntimeException(e); // BOOOOOM!
    }
    EventStore eventStore = new MongoEventStore(new DefaultMongoTemplate(mongo));
    // a Simple Event Bus will do
    EventBus eventBus = new SimpleEventBus();
    // we need to configure the repository
    EventSourcingRepository repository = new EventSourcingRepository(TransactionAggregate.class);
    repository.setEventStore(eventStore);
    repository.setEventBus(eventBus);
    // Axon needs to know that our ToDoItem Aggregate can handle commands
    AggregateAnnotationCommandHandler.subscribe(TransactionAggregate.class, repository, commandBus);
    AnnotationEventListenerAdapter.subscribe(TransactionEventsHandler.factory(), eventBus);
    return commandGateway;
  }
}
