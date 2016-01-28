package com.focustech.jmx.web.view.bind;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.WebRequest;

/**
 * CustomWebBindingInitializer
 * 
 * @author zhangxu
 */
public class CustomWebBindingInitializer implements WebBindingInitializer {

    /*
     * private static final CustomDateEditor DATE_EDITOR = new CustomDateEditor(new SimpleDateFormat(
     * System.getProperty("crov.format.date")), true);
     */
    private static final CustomDateEditor DATE_EDITOR = new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true);

    public void initBinder(WebDataBinder binder, WebRequest request) {
        binder.registerCustomEditor(Date.class, DATE_EDITOR);
    }
}
