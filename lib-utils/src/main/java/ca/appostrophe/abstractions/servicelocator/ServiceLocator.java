package ca.appostrophe.abstractions.servicelocator;

import ca.appostrophe.abstractions.servicelocator.contracts.ServiceLocatorInterface;

/**
 * Created by Hamady Cissé on 2015-12-07.
 */
public abstract class ServiceLocator implements ServiceLocatorInterface {

    private static ServiceLocatorInterface sCurrent;

    @Override
    public abstract <T> T resolve(Class<T> type);

    public static ServiceLocatorInterface getCurrent() {
        return sCurrent;
    }

    public static void setCurrentTest(final ServiceLocatorInterface current) {
        sCurrent = current;
    }

    protected void setCurrent(final ServiceLocatorInterface current) {
        sCurrent = current;
    }

}
