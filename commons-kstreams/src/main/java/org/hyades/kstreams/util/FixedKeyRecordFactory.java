package org.hyades.kstreams.util;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.streams.processor.api.FixedKeyProcessor;
import org.apache.kafka.streams.processor.api.FixedKeyRecord;
import org.apache.kafka.streams.processor.api.Processor;

import java.lang.reflect.Constructor;

@RegisterForReflection(targets = FixedKeyRecord.class)
public class FixedKeyRecordFactory {

    private FixedKeyRecordFactory() {
    }

    /**
     * Create a {@link FixedKeyRecord} by making its package-private constructor accessible via reflection.
     * <p>
     * {@link FixedKeyRecord}s are not intended to be created by users of the Kafka Streams API.
     * However, using {@link FixedKeyProcessor} instead of {@link Processor} allows for avoiding
     * unnecessary repartition operations, as Kafka Streams can assume that the record key has not changed.
     * <p>
     * <strong>Only use this when you know for sure that the record key doesn't change!</strong>
     *
     * @param key   Key of the record to create
     * @param value Value of the record to create
     * @return A {@link FixedKeyRecord} instance
     */
    @SuppressWarnings("unchecked")
    public static <K, V> FixedKeyRecord<K, V> create(final K key, final V value, final long timestamp, final Headers headers) {
        // We can't get a specific constructor, because the arguments of the constructor
        // we want are generic, so they are subject to type erasure at runtime.
        // Instead, we assume that there is only one constructor. If there are less
        // or more than 1, we throw. This can happen when we upgrade Kafka Streams,
        // but it will be caught by our tests.
        final Constructor<?>[] constructors = FixedKeyRecord.class.getDeclaredConstructors();
        if (constructors.length != 1) {
            throw new IllegalStateException("Unexpected number of constructors (expected 1, got %d)".formatted(constructors.length));
        }

        constructors[0].setAccessible(true);

        try {
            return (FixedKeyRecord<K, V>) constructors[0].newInstance(key, value, timestamp, headers);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance of %s".formatted(FixedKeyRecord.class.getName()), e);
        }

    }

}
