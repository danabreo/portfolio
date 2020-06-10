/** Loads posts and login status on page load */
function loadScripts() {
    populatePosts();
    populateLogin();
}

/**
  * Populates the login section of the forum section
  * prompting the user to sign in / sign out.
  */
function populateLogin() {
  fetch("/auth").then(response => response.json()).then((data) => {
    const authPrompt = document.getElementById("loginPrompt");
    if (data.authenticated) {
      authPrompt.innerHTML = "Hello, " + data.userEmail + "! ";
      authPrompt.innerHTML += "<a href=\"" + data.logoutUrl + "\"> Log Out</a>";
    } else {
      authPrompt.innerHTML = "Hello! Please <a href=\"" + data.loginUrl + "\">login</a> to leave a comment.";
    }
  });
}

/**
  * Switches image on splash page when it is clicked.
  */
function changeImage() {
  document.getElementById("switch").className = "index-image-2"; 
}

/**
 * Directions and magnitudes of votes.
 * @enum {int}
 */
const VoteDirection = {
  UP: 1,
  DOWN: -1
}

/**
 * Constructs a vote button for a post.
 * @param {VoteDirection} buttonDirection Specifies the change
 * in votes for a post when the button is clicked.
 * @param {string} buttonSymbol The text on the button.
 * @param {string} postKey Key of the post in Datastore.
 * @return {<button>} HTML button element.
 */
function createVoteButton(buttonDirection, buttonSymbol, postKey) {
  let button = document.createElement("button");
  button.addEventListener("click", function () {
    votePost(postKey, buttonDirection);
  });
  button.type = "button";
  button.className = "btn btn-sm btn-primary";
  button.innerHTML = buttonSymbol;
  return button;
}

/**
  * Creates and adds a post card containing a caption and 
  * a score as provided by the post parameter.
  * @param {!{comment: string, username: string, score: number}} 
  */
function createListElement(post) {
  let card = document.createElement("div");
  card.className = "card m-1";

  let body = document.createElement("div");
  body.className = "card-body";

  let comment = document.createElement("p");
  comment.className = "card-text";
  comment.innerHTML = post.comment;

  let buttons = document.createElement("div");
  buttons.className = "btn-group";
  buttons.setAttribute("role", "group");

  let minus = createVoteButton(VoteDirection.DOWN, " - ", post.key);

  let score = document.createElement("button");
  score.type = "button";
  score.id = "score";
  score.className = "btn btn-sm btn-outline-primary disabled";
  score.innerHTML = post.score;

  let plus = createVoteButton(VoteDirection.UP, " + ", post.key);

  buttons.appendChild(minus);
  buttons.appendChild(score);
  buttons.appendChild(plus);

  body.append(comment);
  body.append(buttons);

  card.append(body);

  document.getElementById('posts').append(card);
}

/**
  * Populates the list element in the forum section
  * with JSON fetched from the /data servlet.
  */
function populatePosts() {
  const numPosts = document.getElementById('numPosts').value;
  const url = "/data?numPosts=" + numPosts;
  
  fetch(url).then(response => response.json()).then((data) => {
    const postHolder = document.getElementById('posts');
    postHolder.innerHTML = "";
    data.forEach(post => {
      createListElement(post);
    });
  });
}

/**
  * Calls the deletedata servlet, and passes in the text of the comment
  * to be deleted. Calls populatePosts to refresh list of comments.
  */
function deletePost() {
  const comment = document.getElementById('comment').value;
  const url = '/deletedata?comment=' + comment;

  fetch(url, {method: 'POST'}).then(result => populatePosts());
}

/**
  * Updates the score of a post in Datastore and on the DOM.
  * @param {string} postKey Key of the post to be updated in Datastore.
  * @param {VoteDirection} direction Specifies the change in 
  * number of votes for a post when the button is clicked.
  */
function votePost(postKey, direction) {
  const url = 'updatescore?key=' + postKey + '&direction=' + direction;
  fetch(url, {method: 'POST'})
      .then(result => populatePosts());
}
