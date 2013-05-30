package net.programania.axon;

import com.mongodb.Mongo;
import net.programania.aggregates.TransactionAggregate;
import net.programania.handlers.TransactionEventsHandler;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.commandhandling.annotation.AggregateAnnotationCommandHandler;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.DefaultCommandGateway;
import org.axonframework.eventhandling.Cluster;
import org.axonframework.eventhandling.ClusteringEventBus;
import org.axonframework.eventhandling.DefaultClusterSelector;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.annotation.AnnotationEventListenerAdapter;
import org.axonframework.eventhandling.async.AsynchronousCluster;
import org.axonframework.eventhandling.async.FullConcurrencyPolicy;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventstore.EventStore;
import org.axonframework.eventstore.mongo.DefaultMongoTemplate;
import org.axonframework.eventstore.mongo.MongoEventStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AxonBootstrap {
  private static final Logger log = LoggerFactory.getLogger(AxonBootstrap.class);

  public static CommandGateway bootstrap() {
    // let's start with the Command Bus
    CommandBus commandBus = new SimpleCommandBus();
    // the CommandGateway provides a friendlier API
    CommandGateway commandGateway = new DefaultCommandGateway(commandBus);

    Mongo mongo = null;
    try {
      mongo = new Mongo("127.0.0.1");
    } catch (UnknownHostException e) {
      log.error("No hemos podido enchufarnos a Mongo", e);
      throw new RuntimeException(e); // BOOOOOM!
    }
    EventStore eventStore = new MongoEventStore(new DefaultMongoTemplate(mongo));

    LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
    Executor executor = new ThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS, workQueue);
    Cluster cluster = new AsynchronousCluster("async", executor, new FullConcurrencyPolicy());
    EventBus eventBus = new ClusteringEventBus(new DefaultClusterSelector(cluster));

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
