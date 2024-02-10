import org.springframework.boot.actuate.management.ThreadDumpEndpoint;
import org.springframework.boot.actuate.logging.LoggersEndpoint;
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public abstract class ActuatorAuthenticator {

   protected BooleanSupplier authenticator = () -> false;

   public void setAuthenticator(BooleanSupplier authenticator) {
      this.authenticator = authenticator;
   }
}


public class ActuatorUtility extends ActuatorAuthenticator {

   private ThreadDumpEndpoint threadDumpEndpoint;
   private LoggersEndpoint loggersEndpoint;
   private HttpExchangeRepository  httpExchangeRepository ;
   private InfoEndpoint infoEndpoint;
   private HealthEndpoint healthEndpoint;
   
   public ActuatorService(ThreadDumpEndpoint threadDumpEndpoint, LoggersEndpoint loggersEndpoint,
		   HttpExchangeRepository  httpExchangeRepository, InfoEndpoint infoEndpoint, HealthEndpoint healthEndpoint) {
      this.threadDumpEndpoint = threadDumpEndpoint;
      this.loggersEndpoint = loggersEndpoint;
      this.httpExchangeRepository = httpExchangeRepository;
      this.infoEndpoint = infoEndpoint;
      this.healthEndpoint = healthEndpoint;
   }

   public final ResponseEntity<Object> info() {
      return buildOkResponse(infoEndpoint.info());
   }

   public final ResponseEntity<Object> health() {
      return buildOkResponse(healthEndpoint.health().getStatus());
   }

   public final ResponseEntity<Object> threadDump() {
      if (!authenticator.getAsBoolean()) {
         return buildErrorResponse();
      }

      return buildOkResponse(threadDumpEndpoint.threadDump().getThreads());
   }

   public final ResponseEntity<Object> loggers() {
      if (!authenticator.getAsBoolean()) {
         return buildErrorResponse();
      }
      return buildOkResponse(loggersEndpoint.loggers());
   }

   public final ResponseEntity<Object> httptrace() {
      if (!authenticator.getAsBoolean()) {
         return buildErrorResponse();
      }

      return buildOkResponse(httpExchangeRepository.findAll());
   }

   private ResponseEntity<Object> buildErrorResponse() {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
   }

   private ResponseEntity<Object> buildOkResponse(Object value) {
      return ResponseEntity.ok().body(value);
   }
}
