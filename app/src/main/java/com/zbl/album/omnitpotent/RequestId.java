package com.zbl.album.omnitpotent;

/**
 * @author yangji
 */
class RequestId {

    private static RequestId INSTANCE;
    private int requestId;

    private static RequestId getInstance() {
        if (INSTANCE == null) {
            synchronized (RequestId.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RequestId();
                }
            }
        }
        return INSTANCE;
    }

    private RequestId() {
        requestId = 20000;
    }

    static synchronized int getRequestId() {
        return getInstance().requestId++;
    }
}
