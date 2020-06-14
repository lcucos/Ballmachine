package com.webproject.resources;


import javax.inject.Singleton;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
 

@Provider
public class WebProjectExceptionMapper implements ExceptionMapper<CustomWebAppException>
{
    @Override
    public Response toResponse(CustomWebAppException exception)
    {
        return Response.status(Status.BAD_REQUEST).entity(exception.getMessage()).build(); 
    }
}
