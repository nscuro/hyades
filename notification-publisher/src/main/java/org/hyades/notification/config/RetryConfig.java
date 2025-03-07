package org.hyades.notification.config;

import io.github.resilience4j.core.IntervalFunction;
import jakarta.validation.constraints.Positive;

import java.time.Duration;
import java.util.OptionalDouble;

public interface RetryConfig {

    Duration initialDelay();

    @Positive
    int multiplier();

    OptionalDouble randomizationFactor();

    Duration maxDuration();

    static IntervalFunction toIntervalFunction(final RetryConfig config) {
        if (config.randomizationFactor().isPresent()) {
            return IntervalFunction.ofExponentialRandomBackoff(
                    config.initialDelay(),
                    config.multiplier(),
                    config.randomizationFactor().getAsDouble(),
                    config.maxDuration()
            );
        }

        return IntervalFunction.ofExponentialBackoff(
                config.initialDelay(),
                config.multiplier(),
                config.maxDuration()
        );
    }

}
