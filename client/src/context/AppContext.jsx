import {createContext, useState} from "react";
import {useAuth, useClerk, useUser} from "@clerk/clerk-react";
import axios from "axios";
import toast from "react-hot-toast";
import {useNavigate} from "react-router-dom";

export const AppContext = createContext();

const AppContextProvider = (props) => {
    const backendUrl = import.meta.env.VITE_BACKEND_URL;

    const [credits, setCredits] = useState(false);
    const {getToken} = useAuth();
    const [image, setImage] = useState(false);
    const [resultImage, setResultImage] = useState(false);
    const {isSignedIn} = useUser();
    const {openSignIn} = useClerk();
    const  navigate = useNavigate();
    const [backgroundImage, setBackgroundImage] = useState(null); // <-- new

    const loadUserCredits = async () => {
        try {
            const token = await getToken();
            const response = await axios.get(backendUrl + "/users/credits", {headers: {Authorization: `Bearer ${token}`}});
            if (response.data.success) {
                setCredits(response.data.data.credits);
            } else {
                toast.error("Error while loading credit !");
            }

        } catch (error) {
            toast.error("Error while loading credit !");
        }
    }

    const eraseBg = async (selectedImage) => {
        try {
            if (!isSignedIn) {
                return openSignIn();
            }
            setImage(selectedImage);
            setResultImage(false);
            // navigate to result image
            navigate("/result");
            const token = await getToken();
            const formData = new FormData();
            selectedImage && formData.append("file", selectedImage);

            const {data:base64Image} = await axios.post(backendUrl + "/images/remove-background", formData, {headers: {Authorization: `Bearer ${token}`}});
            setResultImage(`data:image/png;base64,${base64Image}`);
            setCredits(credits -1);
        }catch (error) {
            console.error(error);
            toast.error("Error while removing background!");
        }
    }

    const contextValue = {
        credits, setCredits,
        image, setImage,
        resultImage,setResultImage,
        backendUrl,
        loadUserCredits,
        eraseBg,
        backgroundImage,
        setBackgroundImage,
    }

    return (
        <AppContext.Provider value={contextValue}>
            {props.children}
        </AppContext.Provider>
    )

}

export default AppContextProvider;