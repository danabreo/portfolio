function changeImage() {
  document.getElementById("switch").className = "index-image-2"; 
}

/**
 * Requests a text greeting from the /data servlet and injects
 * the greeting into the 'greeting' div on the home page. 
 */
async function populateGreeting() {
  const response = await fetch('/data');
  const greeting = await response.text();
  document.getElementById("greeting").innerText = greeting;
}
populateGreeting();