package com.resow.wiapi.infrastructure.api.exception;

import com.resow.wiapi.application.exceptions.CitynameMustBeInformedException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author brunomcarvalho89@gmail.com
 */
@Provider
public class CitynameMustBeInformedExceptionMapper implements ExceptionMapper<CitynameMustBeInformedException> {

    @Override
    public Response toResponse(CitynameMustBeInformedException citynameMustBeInformedException) {

        ExceptionDetails exceptionDetails = ExceptionDetails
                .builder()
                .withTitle("Cityname must be informed.")
                .withDetails(citynameMustBeInformedException.getMessage())
                .withStatus(Response.Status.BAD_REQUEST.getStatusCode())
                .withTimestamp(System.currentTimeMillis())
                .build();

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(exceptionDetails)
                .build();
    }
}
