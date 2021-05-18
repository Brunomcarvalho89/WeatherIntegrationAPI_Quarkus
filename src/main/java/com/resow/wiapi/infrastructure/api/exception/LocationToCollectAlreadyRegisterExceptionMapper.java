package com.resow.wiapi.infrastructure.api.exception;

import com.resow.wiapi.application.exceptions.LocationToCollectAlreadyRegisterException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author brunomcarvalho89@gmail.com
 */
@Provider
public class LocationToCollectAlreadyRegisterExceptionMapper implements ExceptionMapper<LocationToCollectAlreadyRegisterException> {

    @Override
    public Response toResponse(LocationToCollectAlreadyRegisterException locationToCollectAlreadyRegisterException) {

        ExceptionDetails exceptionDetails = ExceptionDetails
                .builder()
                .withTitle("Location to collect already register for city.")
                .withDetails(locationToCollectAlreadyRegisterException.getMessage())
                .withStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                .withTimestamp(System.currentTimeMillis())
                .build();

        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(exceptionDetails)
                .build();
    }
}
