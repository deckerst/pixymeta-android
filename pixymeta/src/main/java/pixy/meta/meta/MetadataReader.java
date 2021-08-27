package pixy.meta.meta;

import pixy.meta.util.Reader;

public interface MetadataReader extends Reader {
	public MetadataType getType();
	public void ensureDataRead();
	public boolean isDataRead();
}
