package com.focustech.jmx.web.view.bind;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.ServletRequestDataBinder;

public class ExtServletRequestDataBinder extends ServletRequestDataBinder {
    private static final String NESTED_PROPERTY_TRIM = "_nested_collections_trim";
    private static final Pattern PROPERTY_ARRAY_NAME = Pattern.compile("(\\w+)\\[(\\d+)\\]");
    private static final Field NAME_FIELD = ReflectionUtils.findField(PropertyValue.class, "name");

    static {
        NAME_FIELD.setAccessible(true);
    }

    /**
     * Create a new instance, with default object name.
     *
     * @param target the target object to bind onto (or <code>null</code> if the binder is just used to convert a plain
     *            parameter value)
     * @see #DEFAULT_OBJECT_NAME
     */
    public ExtServletRequestDataBinder(Object target) {
        super(target);
    }

    /**
     * Create a new instance.
     *
     * @param target the target object to bind onto (or <code>null</code> if the binder is just used to convert a plain
     *            parameter value)
     * @param objectName the name of the target object
     * @see #DEFAULT_OBJECT_NAME
     */
    public ExtServletRequestDataBinder(Object target, String objectName) {
        super(target, objectName);
    }

    /**
     * Add URI template variables to the property values used for data binding.
     */
    @Override
    protected void addBindValues(MutablePropertyValues mpvs, ServletRequest request) {
        super.addBindValues(mpvs, request);

        String collectionsTrim = request.getParameter(NESTED_PROPERTY_TRIM);

        Class<?> target = getTarget().getClass();

        // 扩展
        if (target.isAnnotationPresent(NestedTrim.class)
                || (StringUtils.isNotBlank(collectionsTrim) && Boolean.valueOf(collectionsTrim))) {
            Map<String, Set<Integer>> map = new HashMap<String, Set<Integer>>();
            List<PropertyValue> values = mpvs.getPropertyValueList();

            Matcher m;
            String property = null;
            int index = -1;

            for (PropertyValue value : values) {
                m = PROPERTY_ARRAY_NAME.matcher(value.getName());

                while (m.find()) {
                    property = m.group(1);
                    index = Integer.parseInt(m.group(2));

                    if (!map.containsKey(property)) {
                        map.put(property, new HashSet<Integer>());
                    }

                    map.get(property).add(index);
                }
            }

            Integer[] indexs;
            int realIndex = 0;
            String oldName;

            for (Entry<String, Set<Integer>> entry : map.entrySet()) {
                indexs = entry.getValue().toArray(new Integer[entry.getValue().size()]);
                Arrays.sort(indexs);

                for (int i = 0; i < values.size(); i++) {
                    realIndex = 0;

                    for (int j : indexs) {
                        oldName = ReflectionUtils.getField(NAME_FIELD, values.get(i)).toString();
                        ReflectionUtils
                                .setField(
                                        NAME_FIELD,
                                        values.get(i),
                                        oldName.replace(entry.getKey() + "[" + j + "]", entry.getKey() + "["
                                                + realIndex + "]"));
                        realIndex++;
                    }
                }
            }
        }
    }
}
