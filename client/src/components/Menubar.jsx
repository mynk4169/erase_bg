import { useContext, useState } from "react";
import { assets } from "../assets/assets.js";
import { Menu, X } from "lucide-react";
import { Link, useNavigate } from "react-router-dom";
import { SignedIn, SignedOut, UserButton } from "@clerk/clerk-react";
import { useClerk, useUser } from "@clerk/clerk-react";
import { AppContext } from "../context/AppContext.jsx";

const Menubar = () => {
    const [menuOpen, setMenuOpen] = useState(false);
    const { openSignIn, openSignUp } = useClerk();
    const { user } = useUser();
    const { credits } = useContext(AppContext);
    const navigate = useNavigate();

    const openRegister = () => {
        setMenuOpen(false);
        openSignUp({});
    };

    const openLogin = () => {
        setMenuOpen(false);
        openSignIn({});
    };

    return (
        <nav className="bg-white px-8 py-4 flex justify-between items-center">
            {/* Left side logo + text */}
            <Link className="flex items-center space-x-2" to="/">
                <img
                    src={assets.logo}
                    alt="logo"
                    className="h-8 w-8 object-contain cursor-pointer"
                />
                <span className="text-2xl font-semibold text-indigo-700 cursor-pointer">
                    erase.<span className="text-gray-400">bg</span>
                </span>
            </Link>

            {/* Right side action buttons (desktop) */}
            <div className="hidden md:flex items-center space-x-4">
                <SignedOut>
                    <button
                        className="text-gray-700 hover:text-blue-500 font-medium"
                        onClick={openLogin}
                    >
                        Login
                    </button>
                    <button
                        className="bg-gray-100 hover:bg-gray-200 hover:text-blue-700 font-medium px-4 py-2 rounded-full transition-all"
                        onClick={openRegister}
                    >
                        Sign up
                    </button>
                </SignedOut>

                <SignedIn>
                    <div className="flex items-center gap-2 sm:gap-3">
                        {/* Change Background Button */}
                        <button
                            onClick={() => navigate("/change-bg-page")}
                            className="bg-indigo-700 text-white px-4 py-2 rounded-full hover:bg-indigo-600 transition"
                        >
                            Change Background
                        </button>

                        {/* Pricing Button */}
                        <button
                            onClick={() => navigate("/pricing")}
                            className="flex items-center gap-2 bg-blue-50 px-4 sm:px-5 py-1.5 sm:py-2.5 rounded-full hover:scale-105 transition-all duration-500 cursor-pointer"
                        >
                            <img
                                src={assets.credits}
                                alt="credits"
                                height={29}
                                width={29}
                            />
                            <div className="text-left">
                                <p className="text-xs sm:text-sm font-medium text-gray-600">
                                    Pricing
                                </p>
                                <p className="text-[10px] sm:text-xs text-gray-500">
                                    Credits: {credits}
                                </p>
                            </div>
                        </button>

                        {/* User Greeting */}
                        <p className="text-gray-600 max:sm:hidden">Hi, {user?.fullName}</p>
                    </div>
                    <UserButton />
                </SignedIn>
            </div>

            {/* Mobile menu toggle */}
            <div className="flex md:hidden">
                <button onClick={() => setMenuOpen(!menuOpen)}>
                    {menuOpen ? <X size={28} /> : <Menu size={28} />}
                </button>
            </div>

            {/* Mobile menu */}
            {menuOpen && (
                <div className="absolute top-16 right-8 bg-white shadow-md rounded-md flex flex-col space-y-4 p-4 w-40 z-50">
                    <SignedOut>
                        <button
                            className="text-gray-700 hover:text-blue-500 font-medium transition-all"
                            onClick={openLogin}
                        >
                            Login
                        </button>
                        <button
                            className="bg-gray-100 hover:bg-gray-200 text-gray-700 font-medium px-4 py-2 rounded-full text-center transition-all"
                            onClick={openRegister}
                        >
                            Sign up
                        </button>
                    </SignedOut>

                    <SignedIn>
                        {/* Change Background Button */}
                        <button
                            onClick={() => {
                                setMenuOpen(false);
                                navigate("/change-bg-page");
                            }}
                            className="bg-indigo-700 text-white px-4 py-2 rounded-full hover:bg-indigo-600 transition"
                        >
                            Change Background
                        </button>

                        {/* Pricing Button */}
                        <button
                            onClick={() => {
                                setMenuOpen(false);
                                navigate("/pricing");
                            }}
                            className="flex items-center gap-2 bg-blue-50 px-4 py-1.5 sm:py-2.5 rounded-full hover:scale-105 transition-all duration-500 cursor-pointer"
                        >
                            <img
                                src={assets.credits}
                                alt="credits"
                                height={29}
                                width={29}
                            />
                            <div className="text-left">
                                <p className="text-xs font-medium text-gray-600">
                                    Pricing
                                </p>
                                <p className="text-[10px] text-gray-500">
                                    Credits: {credits}
                                </p>
                            </div>
                        </button>

                        <UserButton />
                    </SignedIn>
                </div>
            )}
        </nav>
    );
};

export default Menubar;
