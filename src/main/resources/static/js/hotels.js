const token = localStorage.getItem("token");
if (!token) window.location.href = "/login";

async function loadHotels() {
  const res = await fetch("/api/hotels", {
    headers: { Authorization: "Bearer " + token },
  });
  const hotels = await res.json();
  const container = document.getElementById("hotelList");

  container.innerHTML = hotels.map(
    (h) => `
    <div>
      <h3>${h.name}</h3>
      <p>${h.city} – ${h.description}</p>
      <button onclick="book(${h.id}, ${h.rating})">Book</button>
    </div>
  `
  ).join("");
}

function book(id, price) {
  localStorage.setItem("hotelId", id);
  window.location.href = "/booking";
}

loadHotels();
