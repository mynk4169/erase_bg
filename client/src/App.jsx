import Menubar from "./components/Menubar.jsx";
import Home from "./pages/Home.jsx";
import Footer from "./components/Footer.jsx";
import {Routes, Route} from "react-router-dom";
import {Toaster} from "react-hot-toast";
import UserSyncHandler from "./components/UserSyncHandler.jsx";
import {RedirectToSignIn, SignedIn, SignedOut} from "@clerk/clerk-react";
import Result from "./pages/Result.jsx";
import BuyCredits from "./pages/BuyCredits.jsx";
import ChangeBg from "./pages/ChangeBg.jsx";
import ChangeBgPage from "./pages/ChangeBgPage.jsx";

const App = () => {
    return (
        <div>
            <UserSyncHandler/>
            <Menubar/>
            <Toaster/>
            <Routes>
                <Route path="/" element={<Home/>}/>
                <Route path="/pricing" element={<BuyCredits/>}/>
                <Route path="/result" element={
                    <>
                        <SignedIn>
                            <Result/>
                        </SignedIn>
                        <SignedOut>
                            <RedirectToSignIn/>
                        </SignedOut>
                    </>
                }
                />
                <Route path="/change-bg" element={
                    <>
                        <SignedIn>
                            <ChangeBg />
                        </SignedIn>
                        <SignedOut>
                            <RedirectToSignIn />
                        </SignedOut>
                    </>
                } />

                <Route path="/change-bg-page" element={
                    <>
                        <SignedIn>
                            <ChangeBgPage />
                        </SignedIn>
                        <SignedOut>
                            <RedirectToSignIn />
                        </SignedOut>
                    </>
                } />



            </Routes>
            <Footer/>
        </div>
    )
}
export default App