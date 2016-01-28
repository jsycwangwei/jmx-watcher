package com.focustech.jmx.util;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.focustech.jmx.model.JmxProcess;

public final class JsonUtils {

    private JsonUtils() {
    }

    public static JmxProcess getJmxProcess(File file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new GuavaModule());
        JmxProcess jmx = mapper.readValue(file, JmxProcess.class);
        jmx.setName(file.getName());
        return jmx;
    }
}
