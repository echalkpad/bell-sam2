package com.vennetics.bell.sam.core.errors;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Loads all error sets available on the classpath into an
 * {@link IErrorDictionary}.
 */

@Configuration
public class ErrorDictionaryLoader {

    private static final Logger LOG = LoggerFactory.getLogger(ErrorDictionaryLoader.class);
    private static final Class<ErrorTypeSet> ERROR_SET_ANNOTATION = ErrorTypeSet.class;

    @Value("${errorSets.basePackage:com.vennetics}")
    private String basePackage;

    @Autowired
    private IErrorSetLoader errorSetLoader;

    @Bean
    public IErrorDictionary loadAllErrorSets() throws ClassNotFoundException {
        LOG.debug(">>> loadAllErrorSets {}", basePackage);

        final ErrorDictionary dictionary = new ErrorDictionary();

        final Reflections reflections = new Reflections(basePackage);

        reflections.getTypesAnnotatedWith(ERROR_SET_ANNOTATION)
                   .forEach(clazz -> loadErrorSetForClass(clazz, dictionary));

        return dictionary;
    }

    @SuppressWarnings({ "squid:UnusedPrivateMethod" }) // It is used, but
                                                       // sonarqube doesn't
                                                       // recognise it.
    private void loadErrorSetForClass(final Class<?> clazz, final ErrorDictionary dictionary) {
        LOG.debug("loadErrorSetForClass {}", clazz);
        if (clazz.isEnum()) {
            errorSetLoader.loadErrorSet(dictionary,
                                        getFilenameFromAnnotation(clazz),
                                        (Enum<?>[]) clazz.getEnumConstants());
        } else {
            LOG.warn("Ignoring ErrorSetAnnotation on non-enum class {}", clazz);
        }
    }

    @SuppressWarnings({ "squid:UnusedPrivateMethod" }) // It is used, but
                                                       // sonarqube doesn't
                                                       // recognise it.
    private static String getFilenameFromAnnotation(final Class<?> clazz) {
        return clazz.getAnnotation(ERROR_SET_ANNOTATION).value();
    }

    public void setBasePackage(final String basePackage) {
        this.basePackage = basePackage;
    }
}
