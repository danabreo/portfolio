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
   * Extracts key of post to be deleted from request URL,
   * retrieves Datastore entry with matching key,
   * deletes the entry from Datastore.
   * Reloads index.html and scrolls to the forum section.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String key = request.getParameter("key");

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.delete(KeyFactory.stringToKey(key));
    
    // Redirect back to the forum section.
    response.sendRedirect("/#forum");
  }
}
