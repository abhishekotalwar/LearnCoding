import static java.util.Arrays.asList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.siemens.sfs.usermanagement.CustomAuthenticationFilter;

@Configuration
public class WebSecurityConfiguration {
   
   @Autowired
   private CustomAuthenticationFilter customAuthenticationFilter;

   @Bean
   CorsConfigurationSource corsConfigurationSource() {
      final CorsConfiguration configuration = new CorsConfiguration();
      configuration.addAllowedOriginPattern("*");
      configuration.setAllowedMethods(asList("*"));
      configuration.setAllowCredentials(true);
      configuration.setAllowedHeaders(asList("Authorization", "Cache-Control", "Content-Type"));
      final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", configuration);
      return source;
   }

   @Bean
   SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
      httpSecurity.csrf(AbstractHttpConfigurer::disable).cors(Customizer.withDefaults())
      .addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
      .authorizeHttpRequests(authorize -> authorize
               .requestMatchers(HttpMethod.PATCH, "/employee/{id}").hasAnyAuthority(RoleEnum.ADMIN.getName(), RoleEnum.ROOT.getName())
               .requestMatchers(HttpMethod.POST, "/employee").hasAnyAuthority(RoleEnum.ADMIN.getName(), RoleEnum.ROOT.getName())
               .requestMatchers(HttpMethod.POST, "/employee").hasAnyAuthority(RoleEnum.ADMIN.getName())
               .anyRequest().permitAll())
      .exceptionHandling(handling -> handling.accessDeniedHandler((request, response, exception) -> response
               .sendError(HttpStatus.UNAUTHORIZED.value(), exception.getMessage())));

      httpSecurity.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

      return httpSecurity.build();
   }
}
