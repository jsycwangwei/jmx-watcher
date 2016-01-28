package com.focustech.jmx.model.output;

import com.focustech.jmx.model.IPrintWriter;

public class ConsolePrintWriter implements IPrintWriter {

    public void write(String str) {
        System.out.println(str);
    }

    public void endwrite() {
    }

    public void write(String fileContent, String filePath) {
        throw new UnsupportedOperationException();
    }

}
