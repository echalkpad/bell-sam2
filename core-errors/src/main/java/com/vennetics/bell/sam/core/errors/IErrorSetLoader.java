package com.vennetics.bell.sam.core.errors;

/**
 * Interface for loading an error set into an error dictionary.
 */
@FunctionalInterface
public interface IErrorSetLoader {

    /**
     * Loads an error set into an {@link ErrorDictionary}.
     * @param dictionary
     *      the dictionary into which the set is to be loaded
     * @param errorFile
     *      the name of the file containing the error descriptions.
     * @param keys
     *      the enum constants of the error descriptions in the set.
     */
    void loadErrorSet(ErrorDictionary dictionary, String errorFile, Enum<?>[] keys);
}
