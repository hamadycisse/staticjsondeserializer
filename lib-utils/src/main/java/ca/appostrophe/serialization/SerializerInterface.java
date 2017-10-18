package ca.appostrophe.serialization;

import java.io.InputStream;

/**
 * 
 * @author hamady.cisse
 * Implementation must be stateless
 */
public interface SerializerInterface<TParser> {

	<TObject> TObject deserialize(TParser parser, Class<TObject> klass) throws SerializationException;

	<TObject> InputStream serialize(TObject entity, Class<TObject> klass);
}
