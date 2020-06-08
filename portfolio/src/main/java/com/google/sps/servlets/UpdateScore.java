package com.google.sps.servlets;

import com.google.appengine.api.datastore.*;
import com.google.sps.data.Post;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Updates the score of a post in Datastore */
@WebServlet("/updatescore")
public class UpdateScore extends HttpServlet {

  /**
   * Extracts the comment and direction of post to be updated from the request URL,
   * retrieves all entries of kind 'post' with matching comment,
   * updates the score of all retrieved entries from Datastore.
   * Reloads index.html and scrolls to the forum section.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    String key = request.getParameter("key");
    String directionString = request.getParameter("direction");
    int direction = Integer.parseInt(directionString);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    
    try {
      Entity post = datastore.get(KeyFactory.stringToKey(key));
      int curScore = Integer.parseInt(post.getProperty("score").toString());
      post.setProperty("score", curScore + direction);
      datastore.put(post);
    } catch (EntityNotFoundException e) {
      e.printStackTrace();
    } 
    
    // Redirect back to the forum section.
    response.sendRedirect("/#forum");
  }
}
