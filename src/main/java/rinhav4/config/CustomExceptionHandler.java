package rinhav4.config;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;

@Produces
@Singleton
@Requires(classes = {IllegalStateException.class, HttpResponse.class})
public class CustomExceptionHandler implements ExceptionHandler<IllegalStateException, HttpResponse<Void>> {

    @Override
    public HttpResponse<Void> handle(HttpRequest request, IllegalStateException exception) {
        return HttpResponse.status(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
