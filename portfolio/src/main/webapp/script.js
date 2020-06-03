function changeImage() {
  document.getElementById("switch").className = "index-image-2"; 
}

function createListElement(text) {
  const listElement = document.createElement('li');
  listElement.innerText = text;
  return listElement;
}

function populateMembers() {
  fetch('/data').then(response => response.json()).then((data) => {
    const podMatesElement = document.getElementById('pod-mates');
    data.messages.forEach(message => {
      podMatesElement.appendChild(createListElement(message));
    });
  });
}
populateMembers();