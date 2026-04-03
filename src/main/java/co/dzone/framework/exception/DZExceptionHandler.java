package co.dzone.framework.exception;

import co.dzone.common.model.APIResult;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class DZExceptionHandler {

    @ExceptionHandler(DZException.class)
    @ResponseBody
    public ResponseEntity<?> handleDZException(HttpServletRequest request, DZException ex) {
        APIResult result = new APIResult();
        result.setResultMsg(ex.getExceptionMessage());
        result.setResultCode(ex.getExceptionCode());
        result.setResultData(ex.getExceptionData());

        HttpStatus status = ex.isExceptionPass() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(result, getHeader(), status);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<?> defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        req.setCharacterEncoding("UTF-8");

        if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null) {
            throw e;
        }

        APIResult result = new APIResult();
        result.setResultCode(DZException.UNKNOWN_EXCEPTION_CODE);
        result.setResultMsg(e.getMessage());
        return new ResponseEntity<>(result, getHeader(), HttpStatus.OK);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public ResponseEntity<?> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        return new ResponseEntity<>(getHeader(), HttpStatus.PAYLOAD_TOO_LARGE);
    }

    private HttpHeaders getHeader() {
        HttpHeaders resHeaders = new HttpHeaders();
        resHeaders.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        return resHeaders;
    }
}
