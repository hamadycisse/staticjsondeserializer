package ca.appostrophe.abstractions.servicelocator.contracts;

/**
 * Created by Hamady Cissé on 2015-12-07.
 */
public interface ServiceLocatorInterface {

    /**
     * Resolve the instance of an object that had been registered
     * @param type the type of the object to resolve
     * @param <T> the type
     * @return the object
     */
    <T> T resolve(Class<T> type);

    /**
     * All the non-private field of the {@code instance} will be set
     * @param instance the instance which will have its dependencies satisfied
     * @param <T> The type to of the instance which will have its dependencies satisfied
     * @return The instance
     * @throws RuntimeException if the locator does not support this type of dependency
     * injection
     */
    <T> T injectDependencies(T instance) throws RuntimeException;
}
