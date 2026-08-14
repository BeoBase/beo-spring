package com.beobase.beospring.shared.implementation;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.beobase.beospring.shared.IdGenerator;
import org.springframework.beans.factory.annotation.Value;

class NanoIdGenerator implements IdGenerator {

    private final int length;

    // Inject default length from application.properties
    public NanoIdGenerator(@Value("${nanoid.default.length:10}") int defaultLength) {
        this.length = defaultLength;
    }

    @Override
    public String generate() {
        return NanoIdUtils.randomNanoId(
                NanoIdUtils.DEFAULT_NUMBER_GENERATOR,
                NanoIdUtils.DEFAULT_ALPHABET,
                length
        );
    }

}
