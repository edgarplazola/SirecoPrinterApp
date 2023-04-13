package com.mx.sireco.util;

import java.util.HashMap;

/* renamed from: galileosolutions.com.mx.sireco.galileosolutions.com.mx.sireco.utils.Config */
public class Config {
    public static final String SESSION = "sessionId";
    public static final String TOKEN = "token";
    public static final String USER_BEAN = "userBean";
    private static Config instance;
    private HashMap<String, Object> map = new HashMap<>();

    private Config() {
    }

    public static final Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    public void setKey(String key, Object value) {
        this.map.put(key, value);
    }

    public Object getKey(String key) {
        return this.map.get(key);
    }
}
