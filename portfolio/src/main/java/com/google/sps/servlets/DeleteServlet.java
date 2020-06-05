package com.google.sps.servlets;

import com.google.appengine.api.datastore.*;
import com.google.gson.Gson;
import com.google.sps.data.Post;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Deletes posts from Datastore */
@WebServlet("/deletedata")
public class DeleteServlet extends HttpServlet {

  /**
   * Extracts text of comment to be deleted from the request URL,
   * retrieves all entries of kind 'post' with matching comment,
   * deletes all retrieved entries from Datastore.
   * Reloads index.html and scrolls to the forum section.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String comment = request.getParameter("comment");

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query =
      new Query("post").setFilter(
          new Query.FilterPredicate("comment", Query.FilterOperator.EQUAL, comment));
    PreparedQuery results = datastore.prepare(query);
    QueryResultIterator<Entity> posts = results.asQueryResultIterator();

    while (posts.hasNext()) {
      Entity post = posts.next();
      datastore.delete(post.getKey());  
    }
    
    // Redirect back to the forum section.
    response.sendRedirect("/#forum");
  }
}
