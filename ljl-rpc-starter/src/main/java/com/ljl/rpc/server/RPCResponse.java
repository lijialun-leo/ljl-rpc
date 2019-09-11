package com.ljl.rpc.server;

import org.msgpack.annotation.Message;

@Message
public class RPCResponse {
    private String requestID;
    private String result;

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
