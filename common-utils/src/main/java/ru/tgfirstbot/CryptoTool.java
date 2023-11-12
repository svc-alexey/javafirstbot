package ru.tgfirstbot;

import org.hashids.Hashids;

public class CryptoTool {
    private final Hashids hashids;

    public CryptoTool (String salt) {
        var minHasLenght = 10;

        this.hashids = new Hashids(salt, minHasLenght);
    }

    public String hashOf (Long value) {
        return hashids.encode(value);
    }

    public Long idOf (String value) {
        long[] res = hashids.decode(value);
        if (res != null && res.length > 0) {
            return res[0];
        }
        return null;
    }
}
