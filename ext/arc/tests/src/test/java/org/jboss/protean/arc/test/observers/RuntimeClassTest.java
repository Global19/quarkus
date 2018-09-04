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

public class RuntimeClassTest {

    @Rule
    public ArcTestContainer container = new ArcTestContainer(NumberProducer.class, NumberObserver.class);

    @Test
    public void testObserver() {
        NumberProducer producer = Arc.container().instance(NumberProducer.class).get();
        NumberObserver observer = Arc.container().instance(NumberObserver.class).get();
        producer.produce(1l);
        producer.produce(.1);
        List<Number> numbers = observer.getNumbers();
        assertEquals(2, numbers.size());
        assertEquals(1l, numbers.get(0));
    }

    @Singleton
    static class NumberObserver {

        private List<Number> numbers;

        @PostConstruct
        void init() {
            numbers = new CopyOnWriteArrayList<>();
        }

        void observeLong(@Observes Long value) {
            numbers.add(value);
        }

        void observeDouble(@Observes Double value) {
            numbers.add(value);
        }

        List<Number> getNumbers() {
            return numbers;
        }

    }

    @Dependent
    static class NumberProducer {

        @Inject
        Event<Number> event;

        void produce(Number value) {
            event.fire(value);
        }

    }

}
