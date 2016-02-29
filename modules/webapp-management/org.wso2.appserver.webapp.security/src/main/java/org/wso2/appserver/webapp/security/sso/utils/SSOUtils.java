/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wso2.appserver.webapp.security.sso.utils;

import org.wso2.appserver.configuration.context.SSOConfiguration;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * This class contains general utility functions used within the single-sign-on (SSO) implementation.
 *
 * @since 6.0.0
 */
public class SSOUtils {
    private static final SecureRandom random = new SecureRandom();

    /**
     * Prevents instantiating the SSOUtils utility class.
     */
    private SSOUtils() {
    }

    /**
     * Generates a unique id.
     *
     * @return a unique id
     */
    public static String createID() {
        byte[] bytes = new byte[20]; // 160 bit
        random.nextBytes(bytes);
        char[] characterMapping = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p' };

        char[] characters = new char[40];
        IntStream.range(0, bytes.length).forEach(index -> {
            int left = (bytes[index] >> 4) & 0x0f;
            int right = bytes[index] & 0x0f;
            characters[index * 2] = characterMapping[left];
            characters[index * 2 + 1] = characterMapping[right];
        });

        return String.valueOf(characters);
    }

    /**
     * Returns true if the specified {@code String} is blank, else false.
     *
     * @param stringValue the {@link String} to be checked whether it is blank
     * @return true if the specified {@link String} is blank, else false
     */
    public static boolean isBlank(String stringValue) {
        return (stringValue == null) || stringValue.isEmpty() || stringValue.chars().
                mapToObj(intCharacter -> (char) intCharacter).parallel().allMatch(Character::isWhitespace);
    }

    /**
     * Returns true if the specified {@code Collection} is null or empty, else false.
     *
     * @param collection the {@link Collection} to be checked
     * @return true if the specified {@code Collection} is null or empty, else false
     */
    public static boolean isCollectionEmpty(Collection collection) {
        return ((collection == null) || (collection.isEmpty()));
    }

    /**
     * Returns a {@code Property} which matches the {@code key} specified.
     *
     * @param properties the list of properties
     * @param key        the {@link String} representation of the key
     * @return the optional {@link org.wso2.appserver.configuration.context.SSOConfiguration.Property}
     */
    public static Optional<SSOConfiguration.Property> getContextPropertyValue(
            List<SSOConfiguration.Property> properties, String key) {
        if (properties != null) {
            return properties.stream().filter(property -> property.getValue().equals(key)).findFirst();
        } else {
            return Optional.empty();
        }
    }
}
