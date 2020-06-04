/**
  * Switches image on splash page when it is clicked.
  */
function changeImage() {
  document.getElementById("switch").className = "index-image-2"; 
}

/**
  * Creates and returns an HTML list element containing
  * a string provided by the the text parameter.
  * @param {String} text Content of the list element.
  * @return {<li>} HTML list element with the contents of the text parameter.
  */
function createListElement(text) {
  const listElement = document.createElement('li');
  listElement.innerText = text;
  return listElement;
}

/**
  * Populates the list element in the forum section
  * with JSON fetched from the /data servlet.
  */
function populatePosts() {
  fetch('/data').then(response => response.json()).then((data) => {
    const postHolder = document.getElementById('posts');
    data.forEach(post => {
      postHolder.appendChild(createListElement(post.comment + ' - ' + post.username));
    });
  });
}
