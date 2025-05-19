import axios from "axios";
import toast from "react-hot-toast";

export const placeOrder = async ({planId, getToken, onSuccess, backendUrl}) => {
    try {
        console.log("Fetching token...");
        const token = await getToken();
        console.log("Token received:", token);

        console.log(`Placing order for planId: ${planId}`);
        const response = await axios.post(`${backendUrl}/orders?planId=${planId}`, {}, {
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            },
            withCredentials: true
        });

        console.log("Order response:", response);

        if (response.status === 201 || response.status === 200) {
            initializePayment({order: response.data.data, getToken, onSuccess, backendUrl});
        } else {
            console.error("Order request failed with status:", response.status);
        }

    } catch (e) {
        console.error("Error in placeOrder:", e);
        toast.error(e.message);
    }
}

const initializePayment = ({order, getToken, onSuccess, backendUrl}) => {
    const options = {
        key: import.meta.env.VITE_RAZORPAY_KEY_ID,
        amount: order.amount,
        currency: order.currency,
        name: "Credit Payment",
        description: "Credit Payment",
        order_id: order.id,
        receipt: order.receipt,
        handler: async (paymentDetails) => {
            try {
                const token = await getToken();
                const response = await axios.post(`${backendUrl}/orders/verify`, paymentDetails, {
                    headers: {
                        "Authorization": `Bearer ${token}`,
                        "Content-Type": "application/json"
                    },
                    withCredentials: true
                });
                if (response.status === 200) {
                    toast.success("Credits Added Successfully!");
                    onSuccess?.();
                }
            } catch (error) {
                console.error("Error in payment verification:", error);
                toast.error(error.message);
            }
        }
    }
    const rzp = new window.Razorpay(options);
    rzp.open();
}