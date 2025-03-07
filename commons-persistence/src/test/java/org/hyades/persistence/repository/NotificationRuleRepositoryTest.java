package org.hyades.persistence.repository;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import org.hyades.persistence.model.NotificationLevel;
import org.hyades.persistence.model.NotificationRule;
import org.hyades.persistence.model.NotificationScope;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

@QuarkusTest
class NotificationRuleRepositoryTest {

    @Inject
    EntityManager entityManager;

    @Inject
    NotificationRuleRepository repository;

    @Test
    @TestTransaction
    public void testRuleLevelEqual() {
        entityManager.createNativeQuery("""
                INSERT INTO "NOTIFICATIONRULE" ("ENABLED", "NAME", "NOTIFY_ON", "NOTIFY_CHILDREN", "NOTIFICATION_LEVEL", "SCOPE", "UUID") VALUES
                                    (true, 'foo', 'NEW_VULNERABILITY', false, 'WARNING', 'PORTFOLIO', '6b1fee41-4178-4a23-9d1b-e9df79de8e62');
                """).executeUpdate();

        final List<NotificationRule> rules = repository
                .findByScopeAndForLevel(NotificationScope.PORTFOLIO, NotificationLevel.WARNING);
        Assertions.assertEquals(1, rules.size());
    }

    @Test
    @TestTransaction
    public void testRuleLevelBelow() {
        entityManager.createNativeQuery("""
                INSERT INTO "NOTIFICATIONRULE" ("ENABLED", "NAME", "NOTIFY_ON", "NOTIFY_CHILDREN", "NOTIFICATION_LEVEL", "SCOPE", "UUID") VALUES
                                    (true, 'foo', 'NEW_VULNERABILITY', false, 'WARNING', 'PORTFOLIO', '6b1fee41-4178-4a23-9d1b-e9df79de8e62');
                """).executeUpdate();

        final List<NotificationRule> rules = repository
                .findByScopeAndForLevel(NotificationScope.PORTFOLIO, NotificationLevel.ERROR);
        Assertions.assertEquals(1, rules.size());
    }

    @Test
    @TestTransaction
    public void testRuleLevelAbove() {
        entityManager.createNativeQuery("""
                INSERT INTO "NOTIFICATIONRULE" ("ENABLED", "NAME", "NOTIFY_ON", "NOTIFY_CHILDREN", "NOTIFICATION_LEVEL", "SCOPE", "UUID") VALUES
                                    (true, 'foo', 'NEW_VULNERABILITY', false, 'WARNING', 'PORTFOLIO', '6b1fee41-4178-4a23-9d1b-e9df79de8e62');
                """).executeUpdate();

        final List<NotificationRule> rules = repository
                .findByScopeAndForLevel(NotificationScope.PORTFOLIO, NotificationLevel.INFORMATIONAL);
        Assertions.assertEquals(0, rules.size());
    }

}