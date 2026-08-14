package com.beobase.beospring.shared.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class StringServiceImplTest {

    private final StringServiceImpl stringService = new StringServiceImpl();

    @Test
    void normalizeStringShouldTrimWhitespace() {
        String result = stringService.normalizeString("  hello  ");
        assertEquals("hello", result);
    }

    @Test
    void normalizeStringShouldConvertToLowerCase() {
        String result = stringService.normalizeString("HELLO");
        assertEquals("hello", result);
    }

    @Test
    void normalizeStringShouldTrimAndConvertToLowerCase() {
        String result = stringService.normalizeString("  HeLLo WoRLD  ");
        assertEquals("hello world", result);
    }

    @Test
    void normalizeStringShouldHandleAlreadyNormalizedString() {
        String result = stringService.normalizeString("hello");
        assertEquals("hello", result);
    }

    @Test
    void normalizeStringShouldHandleEmptyString() {
        String result = stringService.normalizeString("");
        assertEquals("", result);
    }

    @Test
    void normalizeStringShouldUseRootLocale() {
        String result = stringService.normalizeString("I");
        assertEquals("i", result);
    }
}
