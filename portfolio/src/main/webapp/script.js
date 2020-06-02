function changeImage() {
  document.getElementById("switch").className = "index-image-2"; 
}

async function populateGreeting() {
  const response = await fetch('/data');
  const greeting = await response.text();
  document.getElementById("greeting").innerText = greeting;
}
populateGreeting();