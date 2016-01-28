package com.focustech.jmx.model;

import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.ImmutableMap.copyOf;
import static com.google.common.collect.Maps.transformValues;

import java.util.List;
import java.util.Map;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class PropertyResolver {

    private static PropertyResolverFunc RESOLVE_PROPERTIES = new PropertyResolverFunc();

    private static ObjectPropertyResolverFunc RESOLVE_OBJECT_PROPERTIES = new ObjectPropertyResolverFunc();

    private static String resolveString(String s) {

        int pos = s.indexOf(":", 0);

        if (pos == -1)
            return (System.getProperty(s));

        String key = s.substring(0, pos);
        String defval = s.substring(pos + 1);

        String val = System.getProperty(key);

        if (val != null)
            return val;
        else
            return defval;
    }

    public static String resolveProps(@Nullable String s) {
        if (s == null) {
            return null;
        }

        int ipos = 0;
        int pos = s.indexOf("${", ipos);

        if (pos == -1)
            return s;

        StringBuilder sb = new StringBuilder();

        while (ipos < s.length()) {
            pos = s.indexOf("${", ipos);

            if (pos < 0) {
                sb.append(s.substring(ipos));
                break;
            }

            if (pos != ipos)
                sb.append(s.substring(ipos, pos));

            int end = s.indexOf('}', pos);

            if (end < 0)
                break;

            int start = pos + 2;
            pos = end + 1;

            String key = s.substring(start, end);
            String val = resolveString(key);

            if (val != null)
                sb.append(val);
            else
                sb.append("${").append(key).append("}");

            ipos = end + 1;
        }

        return (sb.toString());
    }

    @CheckReturnValue
    public static ImmutableMap<String, Object> resolveMap(@Nonnull Map<String, Object> map) {
        return copyOf(transformValues(map, RESOLVE_OBJECT_PROPERTIES));
    }

    @CheckReturnValue
    public static ImmutableList<String> resolveList(@Nonnull List<String> list) {
        return from(list).transform(RESOLVE_PROPERTIES).toList();
    }

    private static class PropertyResolverFunc implements Function<String, String> {
        @Nullable
        public String apply(@Nullable String input) {
            return resolveProps(input);
        }
    }

    private static class ObjectPropertyResolverFunc implements Function<Object, Object> {
        @Nullable
        public Object apply(@Nullable Object input) {
            if (input instanceof String) {
                return resolveProps((String) input);
            }
            return input;
        }
    }
}
