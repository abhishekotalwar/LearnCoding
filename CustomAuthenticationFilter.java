import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationFilter extends OncePerRequestFilter {

   @Autowired
   private UserManagementClient userManagementClient;

   @Override
   protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
      String userId = request.getHeader("X-UserId");

      if (Objects.nonNull(userId)) {
         try {
            Set<String> userRoles = userManagementClient.userRoles(userId); // it will thorw exception if userId is unknown
            List<SimpleGrantedAuthority> authorities = userRoles.stream().map(role -> new SimpleGrantedAuthority(role)).toList();

            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(gid, "", authorities);

            SecurityContextHolder.getContext().setAuthentication(authRequest);
         } catch (HttpClientErrorException e) {
            String errorMsg = String.format("%s%s", "Access not granted for user ", userId);

            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.getWriter().write(errorMsg);
            return;
         }

      }

      filterChain.doFilter(request, response);
      SecurityContextHolder.clearContext();
   }
}
