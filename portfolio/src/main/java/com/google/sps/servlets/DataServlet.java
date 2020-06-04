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

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private ArrayList<String> data;

/**
  * Initializes the 'data' ArrayList with hard-coded
  * greetings from members of the STEP Pod
  */
  @Override
  public void init() {
    data = new ArrayList<String>();
    data.add("Malcolm from NC");
    data.add("Sabrina from FL");
    data.add("Dan from TX");
  }

  /**
   * Creates a JSON object where the key, 'messages', is assigned a value of a
   * variable sized list, containing all strings from the messages parameter.
   * @param {ArrayList<String>} messages List of messages to be converted.
   * @return {JSON} Valid JSON object with one key, 'messages', whose value is
   * the list of strings provided in the messages parameter. 
   */
  private String convertToJson(ArrayList<String> messages) {
    String json = "{ \"messages\" : [\"";
    json += String.join("\",\"",messages);
    json += "\"] }";
    return json;
  }

  /**
   * @return {JSON} Valid JSON object with one key, 'messages', whose value is
   * the list of strings stored in the data ArrayList.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Convert messages to JSON
    String json = convertToJson(data);

    // Send JSON as the response
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }
}
