package org.hyades.kstreams.exception;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.RecordTooLargeException;
import org.hyades.kstreams.exception.ExceptionHandlerConfig.ThresholdsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Duration;
import java.util.Map;

import static org.hyades.common.config.QuarkusConfigUtil.getConfigMapping;

@RegisterForReflection
public class ProductionExceptionHandler extends AbstractThresholdBasedExceptionHandler
        implements org.apache.kafka.streams.errors.ProductionExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductionExceptionHandler.class);

    @SuppressWarnings("unused") // Called by Kafka Streams via reflection
    public ProductionExceptionHandler() {
        super(getConfigMapping(ExceptionHandlerConfig.class)
                .map(ExceptionHandlerConfig::thresholds)
                .map(ThresholdsConfig::production)
                .orElse(null));
    }

    ProductionExceptionHandler(final Clock clock,
                               final Duration exceptionThresholdInterval,
                               final int exceptionThresholdCount) {
        super(clock, exceptionThresholdInterval, exceptionThresholdCount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(final Map<String, ?> configs) {
        // Configuration is done via Quarkus config.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized ProductionExceptionHandlerResponse handle(final ProducerRecord<byte[], byte[]> record,
                                                                  final Exception exception) {
        if (!(exception instanceof RecordTooLargeException)) {
            LOGGER.error("""
                    Failed to produce record to topic %s; \
                    Stopping to produce records, as the error is of an unexpected type, \
                    and we're not sure if it can safely be ignored\
                    """
                    .formatted(record.topic()), exception);
            return ProductionExceptionHandlerResponse.FAIL;
        }

        if (exceedsThreshold()) {
            LOGGER.error("""
                    Failed to produce record to topic %s; \
                    Stopping to produce records, as the error was encountered %d times since %s, \
                    exceeding the configured threshold of %d occurrences in an interval of %s\
                    """
                    .formatted(record.topic(),
                            exceptionOccurrences(), firstExceptionOccurredAt(),
                            exceptionThresholdCount(), exceptionThresholdInterval()), exception);
            return ProductionExceptionHandlerResponse.FAIL;
        }

        LOGGER.warn("""
                Failed to produce record to topic %s; \
                Skipping and continuing to produce records, as the configured threshold of \
                %d occurrences in an interval of %s has not been exceeded yet\
                """
                .formatted(record.topic(), exceptionThresholdCount(), exceptionThresholdInterval()), exception);
        return ProductionExceptionHandlerResponse.CONTINUE;
    }

}
