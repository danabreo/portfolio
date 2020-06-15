package com.google.sps.data;

/** A post on a forum. */
public final class Post {

  private final String key;
  private final String username;
  private final String comment;
  private final long score;
  private final String blobKey;

  public Post(String key, String username, String comment, long score, String blobKey) {
    this.key = key;
    this.username = username;
    this.comment = comment;
    this.score = score;
    this.blobKey = blobKey;
  }
}
