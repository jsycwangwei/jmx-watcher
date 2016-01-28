package com.focustech.jmx.web.view.velocity;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.velocity.VelocityViewResolver;

public class VelocityLayoutViewResolver extends VelocityViewResolver {

    private String layoutUrl;
    private String layoutKey;
    private String screenContentKey;
    private Resource toolboxConfigResource;

    @SuppressWarnings("rawtypes")
    @Override
    protected Class requiredViewClass() {
        return VelocityLayoutView.class;
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

    public void setToolboxConfigResource(Resource toolboxConfigResource) {
        this.toolboxConfigResource = toolboxConfigResource;
    }

    @Override
    protected AbstractUrlBasedView buildView(String viewName) throws Exception {
        VelocityLayoutView view = (VelocityLayoutView) super.buildView(viewName);
        if (this.layoutUrl != null) {
            view.setLayoutUrl(this.layoutUrl);
        }
        if (this.layoutKey != null) {
            view.setLayoutKey(this.layoutKey);
        }
        if (this.screenContentKey != null) {
            view.setScreenContentKey(this.screenContentKey);
        }
        if (this.toolboxConfigResource != null) {
            ((VelocityToolboxView) view).setToolboxConfigResource(this.toolboxConfigResource);
        }

        return view;
    }
}