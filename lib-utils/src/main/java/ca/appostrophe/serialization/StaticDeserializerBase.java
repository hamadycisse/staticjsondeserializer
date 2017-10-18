package ca.appostrophe.serialization;

public abstract class StaticDeserializerBase<TParser, TDto> {

	public abstract TDto deserialize(TParser reader, SerializerInterface<TParser> genericSerializer) throws SerializationException;

}
