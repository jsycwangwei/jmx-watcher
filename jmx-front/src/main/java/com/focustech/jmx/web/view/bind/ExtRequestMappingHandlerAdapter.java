package com.focustech.jmx.web.view.bind;

import java.util.List;

import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestDataBinderFactory;

public class ExtRequestMappingHandlerAdapter extends RequestMappingHandlerAdapter {
    @Override
    protected ServletRequestDataBinderFactory createDataBinderFactory(List<InvocableHandlerMethod> binderMethods)
            throws Exception {
        return new ExtServletRequestDataBinderFactory(binderMethods, getWebBindingInitializer());
    }
}