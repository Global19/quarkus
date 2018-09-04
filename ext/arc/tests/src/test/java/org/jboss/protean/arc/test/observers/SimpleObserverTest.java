package org.jboss.protean.arc.test.observers;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.protean.arc.Arc;
import org.jboss.protean.arc.test.ArcTestContainer;
import org.junit.Rule;
import org.junit.Test;

public class SimpleObserverTest {

    @Rule
    public ArcTestContainer container = new ArcTestContainer(StringProducer.class, StringObserver.class);

    @Test
    public void testObserver() {
        StringProducer producer = Arc.container().instance(StringProducer.class).get();
        StringObserver observer = Arc.container().instance(StringObserver.class).get();
        producer.produce("foo");
        producer.produce("ping");
        List<String> events = observer.getEvents();
        assertEquals(2, events.size());
    }

    @Singleton
    static class StringObserver {

        private List<String> events;

        @PostConstruct
        void init() {
            events = new CopyOnWriteArrayList<>();
        }

        void observeString(@Observes String value) {
            events.add(value);
        }

        List<String> getEvents() {
            return events;
        }

    }

    @Dependent
    static class StringProducer {

        @Inject
        Event<String> event;

        void produce(String value) {
            event.fire(value);
        }

    }

}
