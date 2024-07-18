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


   ////

   @Bean
   public WebSecurityCustomizer webSecurityCustomizer() {
      return (web) -> web.ignoring().requestMatchers("/staticText/**", "/news/**", "/loggerCollector", "/mappings",
               "/dictionary");
   }

   @Bean
   public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
      httpSecurity.authorizeHttpRequests((authorize) -> authorize.requestMatchers(antMatcher("/h2-console/**")).permitAll()
               .requestMatchers(antMatcher("/")).permitAll());

      httpSecurity.csrf((csrf) -> csrf.disable())
               .authorizeHttpRequests(
                        (authorize) -> authorize.requestMatchers(antMatcher("/UserManagementService")).authenticated())
               .authorizeHttpRequests(
                        (authorize) -> authorize.requestMatchers(antMatcher("/actuator/manage/loggers")).authenticated())
               .authorizeHttpRequests(
                        (authorize) -> authorize.requestMatchers(antMatcher("/actuator/manage/httptrace")).authenticated())
               .httpBasic(Customizer.withDefaults());

      httpSecurity.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
               .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));

      httpSecurity.csrf((csrf) -> csrf.disable()).cors(Customizer.withDefaults());

      httpSecurity.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

      return httpSecurity.build();
   }

   //

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser(manageUser).password("{noop}" + managePass).roles(user);
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
       httpSecurity.csrf(csrf -> csrf.disable()).httpBasic(Customizer.withDefaults()).authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/manage/loggers").authenticated().requestMatchers("/manage/httptrace").authenticated());

       httpSecurity
                .authorizeHttpRequests(
                         authorize -> authorize.requestMatchers("/v1/alg/**").permitAll().requestMatchers("/v1/algs").permitAll())
                .addFilterBefore(filter, BasicAuthenticationFilter.class);

       return httpSecurity.build();
    }
   
}
