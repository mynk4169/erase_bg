import {useAuth, useUser} from "@clerk/clerk-react";
import {useContext, useEffect, useState} from "react";
import {AppContext} from "../context/AppContext.jsx";
import toast from "react-hot-toast";
import axios from "axios";

const UserSyncHandler = () => {

    const {isLoaded, isSignedIn, getToken} = useAuth();
    const {user} = useUser();
    const [synced, setSynced] = useState(false);
    const {backendUrl,loadUserCredits} = useContext(AppContext);

    useEffect(() => {
        const saveUser = async () => {
            if (!isLoaded || !isSignedIn || synced) {
                return;
            }
            try {
                const token = await getToken();

                const userData = {
                    clerkId: user.id,
                    email: user.primaryEmailAddress.emailAddress,
                    firstName: user.firstName,
                    lastName: user.lastName,
                    photUrl:user.imageUrl,
                };
                 await axios.post(backendUrl + '/users', userData, {headers: {"Authorization": `Bearer ${token}`}});

                setSynced(true); // prevent reposting
                await loadUserCredits();

            } catch (e) {
                console.error("User sync failed", e);
                toast.error("Unable to create account");
            }
        }
        saveUser();
    }, [isLoaded, isSignedIn, getToken, user, synced]);

    return null;
}
export default UserSyncHandler;