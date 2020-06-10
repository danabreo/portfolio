// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.Post;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Stores and retrieves posts from Datastore */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  /**
   * @return JSON array with all 'post' entities from Datastore
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(new Query("post").addSort("score", SortDirection.DESCENDING));

    String maxPostsString = request.getParameter("numPosts");
    int maxPosts = Integer.parseInt(maxPostsString);

    List<Post> posts = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      if (posts.size() >= maxPosts) {
        break;
      }

      String key = KeyFactory.keyToString(entity.getKey());
      String username = (String) entity.getProperty("username");
      String comment = (String) entity.getProperty("comment");
      long score = (Long) entity.getProperty("score");

      Post post = new Post(key, username, comment, score);
      posts.add(post);
    }

    Gson gson = new Gson();

    // Send JSON as the response
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(posts));
  }

  /**
   * Checks if user is logged in and then extracts user input from the form, combines
   * the data as an entity of kind 'post', and stores the entity in Datastore.
   * Reloads index.html and scrolls to the forum section.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/#forum");
      return;
    }

    String username = request.getParameter("username");
    String comment = request.getParameter("comment");

    Entity postEntity = new Entity("post");
    postEntity.setProperty("username", username);
    postEntity.setProperty("comment", comment);
    postEntity.setProperty("score", 1);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(postEntity);
    
    // Redirect back to the forum section.
    response.sendRedirect("/#forum");
  }
}
