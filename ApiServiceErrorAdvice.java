import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class ApiServiceErrorAdvice {

   private static final Logger LOG = LoggerFactory.getLogger(ApiServiceErrorAdvice.class);
   
   private static Set<Integer> stackTraceHashcodes = new HashSet<>();

   @ExceptionHandler({ RuntimeException.class })
   public ResponseEntity<Object> handleRunTimeException(RuntimeException e) {
      return error(HttpStatus.INTERNAL_SERVER_ERROR, e);
   }

   @ExceptionHandler({ ServiceRTException.class })
   public ResponseEntity<Object> handleRunTimeException(ServiceRTException e) {
      return info(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
   }

   @ExceptionHandler({ ResourceNotFoundException.class })
   public ResponseEntity<Object> handleNotFoundException(ResourceNotFoundException e) {
      return info(HttpStatus.NOT_FOUND, e.getMessage());
   }

   private ResponseEntity<Object> getObjectResponseEntity(HttpStatus status, String errorMessage) {
      ApiError error = new ApiError();
      error.setHttpCode(status.value());
      error.setMessage(errorMessage);
      error.setTimestamp(Instant.now());
      return ResponseEntity.status(status).body(error);
   }

   private ResponseEntity<Object> error(HttpStatus status, Exception e) {
      logError(e);
      return getObjectResponseEntity(status, e.getMessage());
   }
   
   private void logError(Exception e) {
      StackTraceElement[] stackTrace = e.getStackTrace();
      int stackTraceHashcode = Arrays.hashCode(stackTrace); 

      if (stackTraceHashcodes.contains(stackTraceHashcode)) {
         LOG.error(e.getMessage());
      } else {
         LOG.error(e.getMessage(), e);
         stackTraceHashcodes.add(stackTraceHashcode);
      }
   }

   private ResponseEntity<Object> info(HttpStatus status, String errorMessage) {
      LOG.info(errorMessage);
      return getObjectResponseEntity(status, errorMessage);
   }

   private ResponseEntity<Object> warn(HttpStatus status, String errorMessage) {
      LOG.warn(errorMessage);
      return getObjectResponseEntity(status, errorMessage);

   }
}

-----------------


  public class ApiError {

   private Instant timestamp;
   private int httpCode;
   private String code;
   private String message;
   private String details;
   private String path;

   public Instant getTimestamp() {
      return timestamp;
   }

   public void setTimestamp(Instant timestamp) {
      this.timestamp = timestamp;
   }

   public int getHttpCode() {
      return httpCode;
   }

   public void setHttpCode(int httpCode) {
      this.httpCode = httpCode;
   }

   public String getCode() {
      return code;
   }

   public void setCode(String code) {
      this.code = code;
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public String getDetails() {
      return details;
   }

   public void setDetails(String details) {
      this.details = details;
   }

   public String getPath() {
      return path;
   }

   public void setPath(String path) {
      this.path = path;
   }

}
