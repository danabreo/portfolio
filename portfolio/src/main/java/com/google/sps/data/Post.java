package com.google.sps.data;

/** A post on a forum. */
public final class Post {

  private final String username;
  private final String comment;

  public Post(String username, String comment) {
    this.username = username;
    this.comment = comment;
  }
}
