package com.zou.example.consumer;

import com.zou.zourpc.serializer.Serializer;

import java.io.IOException;

public class fghfh implements Serializer {
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        return new byte[0];
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        return null;
    }
}
