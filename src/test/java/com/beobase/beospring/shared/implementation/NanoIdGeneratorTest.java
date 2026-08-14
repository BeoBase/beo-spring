package com.beobase.beospring.shared.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class NanoIdGeneratorTest {
    @Test
    void generateShouldReturnIdWithConfiguredLength() {
        NanoIdGenerator generator = new NanoIdGenerator(10);
        String id = generator.generate();
        assertNotNull(id);
        assertEquals(10, id.length());
    }

    @Test
    void generateShouldReturnDifferentIds() {
        NanoIdGenerator generator = new NanoIdGenerator(10);
        String first = generator.generate();
        String second = generator.generate();
        assertNotEquals(first, second);
    }

    @Test
    void generateShouldReturnIdWithConfiguredLengthWhenLengthIsCustom() {
        NanoIdGenerator generator = new NanoIdGenerator(20);
        String id = generator.generate();
        assertEquals(20, id.length());
    }
}
