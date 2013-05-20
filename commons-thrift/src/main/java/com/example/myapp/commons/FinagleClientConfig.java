package com.example.myapp.commons;

public class FinagleClientConfig {
    public int timeout;
    public int retries;
    public int hostConnectionLimit;

    public FinagleClientConfig(int timeout, int retries, int hostConnectionLimit) {
        this.timeout = timeout;
        this.retries = retries;
        this.hostConnectionLimit = hostConnectionLimit;
    }

    public int getTimeout() {
        return timeout;
    }

    public int getRetries() {
        return retries;
    }

    public int getHostConnectionLimit() {
        return hostConnectionLimit;
    }
}
