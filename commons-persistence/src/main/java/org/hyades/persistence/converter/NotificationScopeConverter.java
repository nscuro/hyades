package org.hyades.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.hyades.persistence.model.NotificationScope;

import static java.util.Optional.ofNullable;

@Converter(autoApply = true)
public class NotificationScopeConverter implements AttributeConverter<NotificationScope, String> {

    @Override
    public String convertToDatabaseColumn(final NotificationScope entityValue) {
        return ofNullable(entityValue)
                .map(notificationScope -> notificationScope.toString())
                .orElse(null);
    }

    @Override
    public NotificationScope convertToEntityAttribute(final String databaseValue) {
        return ofNullable(databaseValue)
                .map(databaseNotificationScope -> NotificationScope.valueOf(databaseNotificationScope))
                .orElse(null);
    }
}

