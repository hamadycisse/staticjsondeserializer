package ca.appostrophe.serialization;


public final class SerializationException extends Throwable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4297881416282226024L;

	private final Class<?> mSerializedType;

	public SerializationException(final Exception exception,
                                  final Class<?> type) {
		mSerializedType = type;
	}

	public SerializationException(final String message, final Class<?> type) {
		mSerializedType = type;
	}

	public SerializationException(final Exception exception,
                                  final String message,
                                  final Class<?> type) {
		mSerializedType = type;
	}

}
