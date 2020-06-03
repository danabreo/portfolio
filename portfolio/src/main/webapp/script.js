function changeImage() {
  document.getElementById("switch").className = "index-image-2"; 
}

function createListElement(text) {
  const listElement = document.createElement('li');
  listElement.innerText = text;
  return listElement;
}

function populateComments() {
  fetch('/data').then(response => response.json()).then((data) => {
    const commentHolder = document.getElementById('comments');
    data.messages.forEach(message => {
      commentHolder.appendChild(createListElement(message));
    });
  });
}