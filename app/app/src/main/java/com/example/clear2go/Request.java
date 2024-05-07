package com.example.clear2go;

public class Request {
    private String requester;
    private String request;
    public Request(String request,String requester)
    {
        this.requester=requester;
        this.request=request;
    }

    public String getRequest() {
        return request;
    }

    public String getRequester()
    {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public void setRequest(String request) {
        this.request = request;
    }
}
