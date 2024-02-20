
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.siemens.sfs.errorhandling.exception.ServiceRTException;
import com.siemens.sfs.errorhandling.exception.UnauthorizedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

@Order(2)
@Component
public class JobExecutionFilter extends OncePerRequestFilter {

   @Override
   protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
      HttpServletRequest httpRequest = (HttpServletRequest) request;

      if (request.getRequestURI().contains("/jobexecutions")) {
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         String gid = (String) authentication.getPrincipal();
         if (Objects.isNull(gid)) {
            throw new UnauthorizedException();
         }

         List<String> roles = new ArrayList<>();
         authentication.getAuthorities().forEach(authority -> roles.add(authority.getAuthority()));

         String requestBody = httpRequest.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
         try {
            JSONObject jsonObject = new JSONObject(requestBody);
            JSONObject jobExecutionParameters = jsonObject.getJSONObject("jobExecutionParameters");
            jobExecutionParameters.put("gid", gid);
            jobExecutionParameters.put("roles", roles);

            byte[] modifiedRequestBodyBytes = jsonObject.toString().getBytes(StandardCharsets.UTF_8);
            ServletInputStream inputStream = new CustomServletInputStream(modifiedRequestBodyBytes);

            CustomHttpServletRequest requestModified = new CustomHttpServletRequest(httpRequest, inputStream);
            chain.doFilter(requestModified, response);
            return;
         } catch (JSONException e) {
            throw new ServiceRTException(e.getMessage(), e);
         }
      }
      chain.doFilter(request, response);
   }

   private static class CustomServletInputStream extends ServletInputStream {

      private final ByteArrayInputStream inputStream;

      public CustomServletInputStream(byte[] content) {
         this.inputStream = new ByteArrayInputStream(content);
      }

      @Override
      public int read() throws IOException {
         return inputStream.read();
      }

      @Override
      public boolean isFinished() {
         // TODO Auto-generated method stub
         return false;
      }

      @Override
      public boolean isReady() {
         // TODO Auto-generated method stub
         return false;
      }

      @Override
      public void setReadListener(ReadListener listener) {
         // TODO Auto-generated method stub
      }
   }

   private static class CustomHttpServletRequest extends HttpServletRequestWrapper {

      private final ServletInputStream inputStream;

      public CustomHttpServletRequest(HttpServletRequest request, ServletInputStream inputStream) {
         super(request);
         this.inputStream = inputStream;
      }

      @Override
      public ServletInputStream getInputStream() throws IOException {
         return inputStream;
      }
   }
}
