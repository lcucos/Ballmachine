package com.webproject.resources;

import java.io.Serializable;

public class CustomWebAppException extends Exception implements Serializable
{
    private static final long serialVersionUID = 1L;
    public CustomWebAppException() {
        super();
    }
    public CustomWebAppException(String msg)   {
        super(msg);
    }
    public CustomWebAppException(String msg, Exception e)  {
        super(msg, e);
    }
}