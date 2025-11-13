document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("paymentForm");

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const bookingId = document.getElementById("bookingId").value;
        const amount = document.getElementById("amount").value;
        const txnId = document.getElementById("txnId").value;
        const succeed = document.getElementById("success").value === "true";

        try {
            const response = await axios.post("/payments/process", {
                bookingId,
                amount,
                providerTxnId: txnId,
                succeed
            });

            document.getElementById("result").innerText =
                `Payment ${response.data.paymentStatus} for booking ${response.data.bookingId}`;
        } catch (error) {
            console.error(error);
            document.getElementById("result").innerText = "Payment failed!";
        }
    });
});
