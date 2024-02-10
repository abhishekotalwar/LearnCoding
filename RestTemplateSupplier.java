import java.net.URL;
import java.util.function.Supplier;
import javax.net.ssl.SSLContext;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

public class RestTemplateSupplier implements Supplier<RestTemplate> {

   private static final String[] SUPPORTED_PROTOCOLS = new String[] { "TLSv1.2" };
   private static final String[] SUPPORTED_CIPHER_SUITES = { "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
            "TLS_DHE_RSA_WITH_AES_256_GCM_SHA384", "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
            "TLS_DHE_RSA_WITH_AES_128_GCM_SHA256" };

   private TlsConfig tlsConfig;
   private RestTemplate restTemplate;

   public RestTemplateSupplier(TlsConfig tlsConfig) {
      this.tlsConfig = tlsConfig;
   }

   @Override
   public RestTemplate get() {
      return restTemplate();
   }

   private synchronized RestTemplate restTemplate() {
      if (restTemplate == null) {
         char[] keyStorePasswordAsCharArray = tlsConfig.keyStorePassword().toCharArray();
         char[] keyPasswordAsCharArray = tlsConfig.clientKeyPassword().toCharArray();
         char[] trustStorePasswordAsCharArray = tlsConfig.trustStorePassword().toCharArray();

         try {
            URL keyStoreUrl = ResourceUtils.getURL(tlsConfig.keyStorePath());
            URL trustStoreUrl = ResourceUtils.getURL(tlsConfig.trustStorePath());

            SSLContextBuilder SSLBuilder = SSLContexts.custom();
            SSLBuilder = SSLBuilder.loadTrustMaterial(trustStoreUrl, trustStorePasswordAsCharArray).loadKeyMaterial(keyStoreUrl,
                     keyStorePasswordAsCharArray, keyPasswordAsCharArray);

            SSLContext sslContext = SSLBuilder.build();
            SSLConnectionSocketFactory sslConSocFactory = new SSLConnectionSocketFactory(sslContext, SUPPORTED_PROTOCOLS,
                     SUPPORTED_CIPHER_SUITES, new NoopHostnameVerifier());

            PoolingHttpClientConnectionManager connectionManagerBuilder = PoolingHttpClientConnectionManagerBuilder.create()
                     .setSSLSocketFactory(sslConSocFactory).build();

            CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManagerBuilder).build();

            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setHttpClient(httpClient);

            restTemplate = new RestTemplate(requestFactory);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      return restTemplate;
   }

   public TlsConfig getTlsConfig() {
      return tlsConfig;
   }

   public void setTlsConfig(TlsConfig tlsConfig) {
      this.tlsConfig = tlsConfig;
   }
