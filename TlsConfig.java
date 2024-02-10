   private String keyStorePassword;
   private String clientKeyPassword;
   private String keyStorePath;
   private String trustStorePassword;
   private String trustStorePath;

   public TlsConfig(String keyStorePassword, String clientKeyPassword, String keyStorePath, String trustStorePassword,
            String trustStorePath) {
      this.keyStorePassword = keyStorePassword;
      this.clientKeyPassword = clientKeyPassword;
      this.keyStorePath = keyStorePath;
      this.trustStorePassword = trustStorePassword;
      this.trustStorePath = trustStorePath;
   }

   public String keyStorePassword() {
      return keyStorePassword;
   }

   public String clientKeyPassword() {
      return clientKeyPassword;
   }

   public String keyStorePath() {
      return keyStorePath;
   }

   public String trustStorePassword() {
      return trustStorePassword;
   }

   public String trustStorePath() {
      return trustStorePath;
   }
