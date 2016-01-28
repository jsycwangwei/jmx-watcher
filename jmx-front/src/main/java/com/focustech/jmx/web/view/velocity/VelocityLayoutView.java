package com.focustech.jmx.web.view.velocity;

import java.io.StringWriter;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.BeansException;
import org.springframework.core.NestedIOException;

/**
 * VelocityLayoutView.java
 */
public class VelocityLayoutView extends VelocityToolboxView {
    public static final String DEFAULT_LAYOUT_URL = "layout.vm";
    public static final String DEFAULT_LAYOUT_KEY = "layout";
    public static final String DEFAULT_SCREEN_CONTENT_KEY = "screen_content";

    private String layoutUrl;
    private String layoutDir;
    private String layoutKey = DEFAULT_LAYOUT_KEY;
    private String screenContentKey = DEFAULT_SCREEN_CONTENT_KEY;

    @Override
    protected void initApplicationContext() throws BeansException {
        super.initApplicationContext();
        this.layoutUrl = (String) getVelocityEngine().getProperty("tools.view.servlet.layout.default.template");
        this.layoutDir = (String) getVelocityEngine().getProperty("tools.view.servlet.layout.directory");
        if (this.layoutUrl == "") {
            this.layoutUrl = DEFAULT_LAYOUT_URL;
        }
        if (!this.layoutDir.endsWith("/")) {
            this.layoutDir = this.layoutDir + "/";
        }
    }

    public void setLayoutUrl(String layoutUrl) {
        this.layoutUrl = layoutUrl;
    }

    public void setLayoutKey(String layoutKey) {
        this.layoutKey = layoutKey;
    }

    public void setScreenContentKey(String screenContentKey) {
        this.screenContentKey = screenContentKey;
    }

    @Override
    public boolean checkResource(Locale locale) throws Exception {
        if (!super.checkResource(locale)) {
            return false;
        }
        try {
            getTemplate(getTotalLayoutUrl(this.layoutUrl));
            return true;
        }
        catch (ResourceNotFoundException ex) {
            throw new NestedIOException("Cannot find Velocity template for URL [" + getTotalLayoutUrl(this.layoutUrl)
                    + "]: Did you specify the correct resource loader path?", ex);
        }
        catch (Exception ex) {
            throw new NestedIOException("Could not load Velocity template for URL ["
                    + getTotalLayoutUrl(this.layoutUrl) + "]", ex);
        }
    }

    @Override
    protected void doRender(Context context, HttpServletResponse response) throws Exception {
        renderScreenContent(context);

        String layoutUrlToUse = (String) context.get(this.layoutKey);
        if (layoutUrlToUse != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Screen content template has requested layout [" + layoutUrlToUse + "]");
            }
        }
        else {
            // No explicit layout URL given -> use default layout of this view.
            layoutUrlToUse = this.layoutUrl;
        }

        mergeTemplate(getTemplate(getTotalLayoutUrl(layoutUrlToUse)), context, response);
    }

    private String getTotalLayoutUrl(String layoutUrl) {
        return layoutDir + layoutUrl;
    }

    /**
     * The resulting context contains any mappings from render, plus screen content.
     */
    private void renderScreenContent(Context velocityContext) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Rendering screen content template [" + getUrl() + "]");
        }

        StringWriter sw = new StringWriter();
        Template screenContentTemplate = getTemplate(getUrl());
        screenContentTemplate.merge(velocityContext, sw);

        // Put rendered content into Velocity context.
        velocityContext.put(this.screenContentKey, sw.toString());
    }
}
