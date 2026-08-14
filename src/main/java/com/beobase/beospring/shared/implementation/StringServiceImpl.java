package com.beobase.beospring.shared.implementation;

import java.util.Locale;

import com.beobase.beospring.shared.StringService;
import org.springframework.stereotype.Service;

@Service
class StringServiceImpl implements StringService {

    @Override
    public String normalizeString(String string) {
        return string.trim().toLowerCase(Locale.ROOT);
    }

}
