package com.focustech.jmx.model.output;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.focustech.jmx.model.IPrintWriter;

public class FilePrintWriter implements IPrintWriter {

    private final Logger log = LoggerFactory.getLogger(FilePrintWriter.class);

    public void write(String str) {
        throw new UnsupportedOperationException();
    }

    public void endwrite() {
    }

    @Override
    public void write(String fileContent, String filePath) {
        if (StringUtils.isEmpty(fileContent) || StringUtils.isEmpty(filePath))
            return;
        FileWriter fw = null;
        try {
            File file = new File(filePath);
            log.error(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            fw = new FileWriter(file);
            fw.write(fileContent);
            fw.flush();
        }
        catch (IOException e) {
            log.error("FilePrintWriter::write", e);
        }
        finally {
            closeFile(fw);
        }
    }

    private void closeFile(OutputStreamWriter writer) {
        if (writer != null) {
            try {
                writer.close();
            }
            catch (IOException e) {
                log.error("FilePrintWriter::closeFile", e);
            }
        }
    }
}
