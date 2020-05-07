package com.mill.mnative.imageload.resource;

public class BytesResource implements Resource<byte[]> {
    private final byte[] bytes;

    public BytesResource(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public Class<byte[]> getResourceClass() {
        return byte[].class;
    }

    @Override
    public byte[] get() {
        return bytes;
    }

    @Override
    public int getSize() {
        return bytes.length;
    }

    @Override
    public void recycle() {

    }
}

