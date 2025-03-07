/*
 * This file is part of Dependency-Track.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (c) Steve Springett. All Rights Reserved.
 */
package org.hyades.notification.publisher;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DefaultNotificationPublishersTest {

    @Test
    public void testEnums() {
        Assertions.assertEquals("SLACK", DefaultNotificationPublishers.SLACK.name());
        Assertions.assertEquals("MS_TEAMS", DefaultNotificationPublishers.MS_TEAMS.name());
        Assertions.assertEquals("MATTERMOST", DefaultNotificationPublishers.MATTERMOST.name());
        Assertions.assertEquals("EMAIL", DefaultNotificationPublishers.EMAIL.name());
        Assertions.assertEquals("CONSOLE", DefaultNotificationPublishers.CONSOLE.name());
        Assertions.assertEquals("WEBHOOK", DefaultNotificationPublishers.WEBHOOK.name());
         Assertions.assertEquals("JIRA", DefaultNotificationPublishers.JIRA.name());
    }
}