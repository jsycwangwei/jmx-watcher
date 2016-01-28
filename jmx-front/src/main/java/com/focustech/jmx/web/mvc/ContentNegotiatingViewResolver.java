package com.focustech.jmx.web.mvc;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.SmartView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.WebUtils;

/**
 * ContentNegotiatingViewResolver.java <br>
 * <br>
 * 解决类似下面的报错 <br>
 * ERROR [org.apache.velocity.app.VelocityEngine] resin-tcp-connection-127.0.0.1:8300-15 - ResourceManager : unable to
 * find resource 'diy/libra/diy-detail.htm.vm' in any resource loader.
 * 
 * @author chengzhaoyu
 */
public class ContentNegotiatingViewResolver extends WebApplicationObjectSupport implements ViewResolver, Ordered {

    private static final Log logger = LogFactory.getLog(ContentNegotiatingViewResolver.class);

    private static final String ACCEPT_HEADER = "Accept";

    private static final boolean jafPresent = ClassUtils.isPresent("javax.activation.FileTypeMap",
            ContentNegotiatingViewResolver.class.getClassLoader());

    private static final UrlPathHelper urlPathHelper = new UrlPathHelper();

    private int order = Ordered.HIGHEST_PRECEDENCE;

    private boolean favorPathExtension = true;

    private boolean favorParameter = false;

    private String parameterName = "format";

    private boolean useNotAcceptableStatusCode = false;

    private boolean ignoreAcceptHeader = false;

    private boolean useJaf = jafPresent;

    private ConcurrentMap<String, MediaType> mediaTypes = new ConcurrentHashMap<String, MediaType>();

    private List<View> defaultViews;

    private MediaType defaultContentType;

    private List<ViewResolver> viewResolvers;

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }

    /**
     * Indicate whether the extension of the request path should be used to determine the requested media type, in favor
     * of looking at the {@code Accept} header. The default value is {@code true}.
     * <p>
     * For instance, when this flag is <code>true</code> (the default), a request for {@code /hotels.pdf} will result in
     * an {@code AbstractPdfView} being resolved, while the {@code Accept} header can be the browser-defined
     * {@code text/html,application/xhtml+xml}.
     */
    public void setFavorPathExtension(boolean favorPathExtension) {
        this.favorPathExtension = favorPathExtension;
    }

    /**
     * Indicate whether a request parameter should be used to determine the requested media type, in favor of looking at
     * the {@code Accept} header. The default value is {@code false}.
     * <p>
     * For instance, when this flag is <code>true</code>, a request for {@code /hotels?format=pdf} will result in an
     * {@code AbstractPdfView} being resolved, while the {@code Accept} header can be the browser-defined
     * {@code text/html,application/xhtml+xml}.
     */
    public void setFavorParameter(boolean favorParameter) {
        this.favorParameter = favorParameter;
    }

    /**
     * Set the parameter name that can be used to determine the requested media type if the {@link #setFavorParameter}
     * property is {@code true}. The default parameter name is {@code format}.
     */
    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    /**
     * Indicate whether the HTTP {@code Accept} header should be ignored. Default is {@code false}.
     * <p>
     * If set to {@code true}, this view resolver will only refer to the file extension and/or parameter, as indicated
     * by the {@link #setFavorPathExtension favorPathExtension} and {@link #setFavorParameter favorParameter}
     * properties.
     */
    public void setIgnoreAcceptHeader(boolean ignoreAcceptHeader) {
        this.ignoreAcceptHeader = ignoreAcceptHeader;
    }

    /**
     * Indicate whether a {@link HttpServletResponse#SC_NOT_ACCEPTABLE 406 Not Acceptable} status code should be
     * returned if no suitable view can be found.
     * <p>
     * Default is {@code false}, meaning that this view resolver returns {@code null} for
     * {@link #resolveViewName(String, Locale)} when an acceptable view cannot be found. This will allow for view
     * resolvers chaining. When this property is set to {@code true}, {@link #resolveViewName(String, Locale)} will
     * respond with a view that sets the response status to {@code 406 Not Acceptable} instead.
     */
    public void setUseNotAcceptableStatusCode(boolean useNotAcceptableStatusCode) {
        this.useNotAcceptableStatusCode = useNotAcceptableStatusCode;
    }

    /**
     * Set the mapping from file extensions to media types.
     * <p>
     * When this mapping is not set or when an extension is not present, this view resolver will fall back to using a
     * {@link FileTypeMap} when the Java Action Framework is available.
     */
    public void setMediaTypes(Map<String, String> mediaTypes) {
        Assert.notNull(mediaTypes, "'mediaTypes' must not be null");
        for (Map.Entry<String, String> entry : mediaTypes.entrySet()) {
            String extension = entry.getKey().toLowerCase(Locale.ENGLISH);
            MediaType mediaType = MediaType.parseMediaType(entry.getValue());
            this.mediaTypes.put(extension, mediaType);
        }
    }

    /**
     * Set the default views to use when a more specific view can not be obtained from the {@link ViewResolver} chain.
     */
    public void setDefaultViews(List<View> defaultViews) {
        this.defaultViews = defaultViews;
    }

    /**
     * Set the default content type.
     * <p>
     * This content type will be used when file extension, parameter, nor {@code Accept} header define a content-type,
     * either through being disabled or empty.
     */
    public void setDefaultContentType(MediaType defaultContentType) {
        this.defaultContentType = defaultContentType;
    }

    /**
     * Indicate whether to use the Java Activation Framework to map from file extensions to media types.
     * <p>
     * Default is {@code true}, i.e. the Java Activation Framework is used (if available).
     */
    public void setUseJaf(boolean useJaf) {
        this.useJaf = useJaf;
    }

    /**
     * Sets the view resolvers to be wrapped by this view resolver.
     * <p>
     * If this property is not set, view resolvers will be detected automatically.
     */
    public void setViewResolvers(List<ViewResolver> viewResolvers) {
        this.viewResolvers = viewResolvers;
    }

    @Override
    protected void initServletContext(ServletContext servletContext) {
        Collection<ViewResolver> matchingBeans =
                BeanFactoryUtils.beansOfTypeIncludingAncestors(getApplicationContext(), ViewResolver.class).values();
        if (this.viewResolvers == null) {
            this.viewResolvers = new ArrayList<ViewResolver>(matchingBeans.size());
            for (ViewResolver viewResolver : matchingBeans) {
                if (this != viewResolver) {
                    this.viewResolvers.add(viewResolver);
                }
            }
        }
        else {
            for (int i = 0; i < viewResolvers.size(); i++) {
                if (matchingBeans.contains(viewResolvers.get(i))) {
                    continue;
                }
                String name = viewResolvers.get(i).getClass().getName() + i;
                getApplicationContext().getAutowireCapableBeanFactory().initializeBean(viewResolvers.get(i), name);
            }

        }
        if (this.viewResolvers.isEmpty()) {
            logger.warn("Did not find any ViewResolvers to delegate to; please configure them using the "
                    + "'viewResolvers' property on the ContentNegotiatingViewResolver");
        }
        OrderComparator.sort(this.viewResolvers);
    }

    public View resolveViewName(String viewName, Locale locale) throws Exception {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        Assert.isInstanceOf(ServletRequestAttributes.class, attrs);
        List<MediaType> requestedMediaTypes = getMediaTypes(((ServletRequestAttributes) attrs).getRequest());
        if (requestedMediaTypes != null) {
            List<View> candidateViews = getCandidateViews(viewName, locale, requestedMediaTypes);
            View bestView = getBestView(candidateViews, requestedMediaTypes);
            if (bestView != null) {
                return bestView;
            }
        }
        if (this.useNotAcceptableStatusCode) {
            if (logger.isDebugEnabled()) {
                logger.debug("No acceptable view found; returning 406 (Not Acceptable) status code");
            }
            return NOT_ACCEPTABLE_VIEW;
        }
        else {
            logger.debug("No acceptable view found; returning null");
            return null;
        }
    }

    /**
     * Determines the list of {@link MediaType} for the given {@link HttpServletRequest}.
     * <p>
     * The default implementation invokes {@link #getMediaTypeFromFilename(String)} if
     * {@linkplain #setFavorPathExtension favorPathExtension} property is <code>true</code>. If the property is
     * <code>false</code>, or when a media type cannot be determined from the request path, this method will inspect the
     * {@code Accept} header of the request.
     * <p>
     * This method can be overridden to provide a different algorithm.
     * 
     * @param request the current servlet request
     * @return the list of media types requested, if any
     */
    protected List<MediaType> getMediaTypes(HttpServletRequest request) {
        if (this.favorPathExtension) {
            String requestUri = urlPathHelper.getLookupPathForRequest(request);
            String filename = WebUtils.extractFullFilenameFromUrlPath(requestUri);
            MediaType mediaType = getMediaTypeFromFilename(filename);
            if (mediaType != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Requested media type is '" + mediaType + "' (based on filename '" + filename + "')");
                }
                return Collections.singletonList(mediaType);
            }
        }
        if (this.favorParameter) {
            if (request.getParameter(this.parameterName) != null) {
                String parameterValue = request.getParameter(this.parameterName);
                MediaType mediaType = getMediaTypeFromParameter(parameterValue);
                if (mediaType != null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Requested media type is '" + mediaType + "' (based on parameter '"
                                + this.parameterName + "'='" + parameterValue + "')");
                    }
                    return Collections.singletonList(mediaType);
                }
            }
        }
        if (!this.ignoreAcceptHeader) {
            String acceptHeader = request.getHeader(ACCEPT_HEADER);
            if (StringUtils.hasText(acceptHeader)) {
                try {
                    List<MediaType> acceptableMediaTypes = MediaType.parseMediaTypes(acceptHeader);
                    List<MediaType> producibleMediaTypes = getProducibleMediaTypes(request);
                    Set<MediaType> compatibleMediaTypes = new LinkedHashSet<MediaType>();
                    for (MediaType acceptable : acceptableMediaTypes) {
                        for (MediaType producible : producibleMediaTypes) {
                            if (acceptable.isCompatibleWith(producible)) {
                                compatibleMediaTypes.add(getMostSpecificMediaType(acceptable, producible));
                            }
                        }
                    }
                    List<MediaType> mediaTypes = new ArrayList<MediaType>(compatibleMediaTypes);
                    MediaType.sortByQualityValue(mediaTypes);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Requested media types are " + mediaTypes + " based on Accept header types "
                                + "and producible media types " + producibleMediaTypes + ")");
                    }
                    return mediaTypes;
                }
                catch (IllegalArgumentException ex) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Could not parse accept header [" + acceptHeader + "]: " + ex.getMessage());
                    }
                    return null;
                }
            }
        }
        if (this.defaultContentType != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Requested media types is " + this.defaultContentType
                        + " (based on defaultContentType property)");
            }
            return Collections.singletonList(this.defaultContentType);
        }
        else {
            return Collections.emptyList();
        }
    }

    @SuppressWarnings("unchecked")
    private List<MediaType> getProducibleMediaTypes(HttpServletRequest request) {
        Set<MediaType> mediaTypes =
                (Set<MediaType>) request.getAttribute(HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);
        if (!CollectionUtils.isEmpty(mediaTypes)) {
            return new ArrayList<MediaType>(mediaTypes);
        }
        else {
            return Collections.singletonList(MediaType.ALL);
        }
    }

    /**
     * Returns the more specific media type using the q-value of the first media type for both.
     */
    private MediaType getMostSpecificMediaType(MediaType type1, MediaType type2) {
        double quality = type1.getQualityValue();
        Map<String, String> params = Collections.singletonMap("q", String.valueOf(quality));
        MediaType t1 = new MediaType(type1, params);
        MediaType t2 = new MediaType(type2, params);
        return MediaType.SPECIFICITY_COMPARATOR.compare(t1, t2) <= 0 ? type1 : type2;
    }

    /**
     * Determines the {@link MediaType} for the given filename.
     * <p>
     * The default implementation will check the {@linkplain #setMediaTypes(Map) media types} property first for a
     * defined mapping. If not present, and if the Java Activation Framework can be found on the classpath, it will call
     * {@link FileTypeMap#getContentType(String)}
     * <p>
     * This method can be overridden to provide a different algorithm.
     * 
     * @param filename the current request file name (i.e. {@code hotels.html})
     * @return the media type, if any
     */
    protected MediaType getMediaTypeFromFilename(String filename) {
        String extension = StringUtils.getFilenameExtension(filename);
        if (!StringUtils.hasText(extension)) {
            return null;
        }
        extension = extension.toLowerCase(Locale.ENGLISH);
        MediaType mediaType = this.mediaTypes.get(extension);
        if (mediaType == null) {
            String mimeType = getServletContext().getMimeType(filename);
            if (StringUtils.hasText(mimeType)) {
                mediaType = MediaType.parseMediaType(mimeType);
            }
            if (this.useJaf && (mediaType == null || MediaType.APPLICATION_OCTET_STREAM.equals(mediaType))) {
                MediaType jafMediaType = ActivationMediaTypeFactory.getMediaType(filename);
                if (jafMediaType != null && !MediaType.APPLICATION_OCTET_STREAM.equals(jafMediaType)) {
                    mediaType = jafMediaType;
                }
            }
            // if (mediaType != null) {
            // this.mediaTypes.putIfAbsent(extension, mediaType);
            // }
        }
        return mediaType;
    }

    /**
     * Determines the {@link MediaType} for the given parameter value.
     * <p>
     * The default implementation will check the {@linkplain #setMediaTypes(Map) media types} property for a defined
     * mapping.
     * <p>
     * This method can be overriden to provide a different algorithm.
     * 
     * @param parameterValue the parameter value (i.e. {@code pdf}).
     * @return the media type, if any
     */
    protected MediaType getMediaTypeFromParameter(String parameterValue) {
        return this.mediaTypes.get(parameterValue.toLowerCase(Locale.ENGLISH));
    }

    private List<View> getCandidateViews(String viewName, Locale locale, List<MediaType> requestedMediaTypes)
            throws Exception {

        List<View> candidateViews = new ArrayList<View>();
        for (ViewResolver viewResolver : this.viewResolvers) {
            View view = viewResolver.resolveViewName(viewName, locale);
            if (view != null) {
                candidateViews.add(view);
            }
            for (MediaType requestedMediaType : requestedMediaTypes) {
                List<String> extensions = getExtensionsForMediaType(requestedMediaType);
                for (String extension : extensions) {
                    String viewNameWithExtension = viewName + "." + extension;
                    view = viewResolver.resolveViewName(viewNameWithExtension, locale);
                    if (view != null) {
                        candidateViews.add(view);
                    }
                }

            }
        }
        if (!CollectionUtils.isEmpty(this.defaultViews)) {
            candidateViews.addAll(this.defaultViews);
        }
        return candidateViews;
    }

    private List<String> getExtensionsForMediaType(MediaType requestedMediaType) {
        List<String> result = new ArrayList<String>();
        for (Entry<String, MediaType> entry : this.mediaTypes.entrySet()) {
            if (requestedMediaType.includes(entry.getValue())) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    private View getBestView(List<View> candidateViews, List<MediaType> requestedMediaTypes) {
        for (View candidateView : candidateViews) {
            if (candidateView instanceof SmartView) {
                SmartView smartView = (SmartView) candidateView;
                if (smartView.isRedirectView()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Returning redirect view [" + candidateView + "]");
                    }
                    return candidateView;
                }
            }
        }
        for (MediaType mediaType : requestedMediaTypes) {
            for (View candidateView : candidateViews) {
                if (StringUtils.hasText(candidateView.getContentType())) {
                    MediaType candidateContentType = MediaType.parseMediaType(candidateView.getContentType());
                    if (mediaType.includes(candidateContentType)) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Returning [" + candidateView + "] based on requested media type '"
                                    + mediaType + "'");
                        }
                        return candidateView;
                    }
                }
            }
        }
        return null;
    }

    private static final View NOT_ACCEPTABLE_VIEW = new View() {

        public String getContentType() {
            return null;
        }

        public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        }
    };

    /**
     * Inner class to avoid hard-coded JAF dependency.
     */
    private static class ActivationMediaTypeFactory {

        private static final FileTypeMap fileTypeMap;

        static {
            fileTypeMap = loadFileTypeMapFromContextSupportModule();
        }

        private static FileTypeMap loadFileTypeMapFromContextSupportModule() {
            // see if we can find the extended mime.types from the context-support module
            Resource mappingLocation = new ClassPathResource("org/springframework/mail/javamail/mime.types");
            if (mappingLocation.exists()) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Loading Java Activation Framework FileTypeMap from " + mappingLocation);
                }
                InputStream inputStream = null;
                try {
                    inputStream = mappingLocation.getInputStream();
                    return new MimetypesFileTypeMap(inputStream);
                }
                catch (IOException ex) {
                    // ignore
                }
                finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        }
                        catch (IOException ex) {
                            // ignore
                        }
                    }
                }
            }
            if (logger.isTraceEnabled()) {
                logger.trace("Loading default Java Activation Framework FileTypeMap");
            }
            return FileTypeMap.getDefaultFileTypeMap();
        }

        public static MediaType getMediaType(String filename) {
            String mediaType = fileTypeMap.getContentType(filename);
            return (StringUtils.hasText(mediaType) ? MediaType.parseMediaType(mediaType) : null);
        }
    }

}
