package com.google.sps.data;

/** User authentication info. */
public final class AuthInfo {

  private final boolean authenticated;
  private final String userEmail;
  private final String logoutUrl;
  private final String loginUrl;

  public AuthInfo(boolean authenticated, String userEmail, String logoutUrl) {
      this.authenticated = authenticated;
      this.userEmail = userEmail;
      this.logoutUrl = logoutUrl;
      this.loginUrl = null;
  }
  
  public AuthInfo(boolean authenticated, String loginUrl) {
      this.authenticated = authenticated;
      this.userEmail = null;
      this.logoutUrl = null;
      this.loginUrl = loginUrl;
  }
}
