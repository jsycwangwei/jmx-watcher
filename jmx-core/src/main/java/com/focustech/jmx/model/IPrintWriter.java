package com.focustech.jmx.model;

public interface IPrintWriter {
    void write(String str);

    void write(String fileContent, String filePath);

    void endwrite();

}