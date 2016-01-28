package com.focustech.jmx.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.focustech.jmx.log.LogCategory;

public abstract class AbstractService {
    protected Log log = LogFactory.getLog(LogCategory.SERVICE.toString());
}
