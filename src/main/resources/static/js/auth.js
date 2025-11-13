document.getElementById("loginForm").addEventListener("submit", async (e) => {
  e.preventDefault();

  const username = document.getElementById("username").value;
  const password = document.getElementById("password").value;

  try {
    const res = await fetch("/api/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, password }),
    });

    if (!res.ok) throw new Error("Invalid credentials");
    const data = await res.json();
    localStorage.setItem("token", data.token);

    document.getElementById("msg").textContent = "Login successful!";
    setTimeout(() => window.location.href = "/hotels", 1000);
  } catch (err) {
    document.getElementById("msg").textContent = err.message;
  }
});
