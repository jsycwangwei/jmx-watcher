package com.focustech.jmx.model.result;

import javax.annotation.Nullable;

public class IdentityValueTransformer implements ValueTransformer {
    @Nullable
    public Object apply(@Nullable Object input) {
        return input;
    }
}
