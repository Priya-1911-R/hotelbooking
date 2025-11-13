const token = localStorage.getItem("token");
const hotelId = localStorage.getItem("hotelId");

document.getElementById("bookingForm").addEventListener("submit", async (e) => {
  e.preventDefault();

  const checkIn = document.getElementById("checkIn").value;
  const checkOut = document.getElementById("checkOut").value;
  const totalPrice = document.getElementById("totalPrice").value;

  try {
    const res = await fetch("/api/bookings", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + token,
      },
      body: JSON.stringify({ hotelId, checkIn, checkOut, totalPrice }),
    });

    if (!res.ok) throw new Error("Booking failed");
    const data = await res.json();

    document.getElementById("msg").textContent = "Booking successful!";
    setTimeout(() => window.location.href = "/payment", 1500);
  } catch (err) {
    document.getElementById("msg").textContent = err.message;
  }
});
