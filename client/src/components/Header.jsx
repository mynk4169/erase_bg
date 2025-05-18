import { assets } from "../assets/assets.js";
import { useContext } from "react";
import { AppContext } from "../context/AppContext.jsx";

const Header = () => {
    const { eraseBg } = useContext(AppContext);

    const handleFileChange = (file) => {
        if (!file) return;

        if (file.size > 25 * 1024 * 1024) {
            alert("File must be less than 25MB");
            return;
        }

        const img = new Image();

        img.onload = () => {
            const pixels = img.width * img.height;
            if (pixels > 25_000_000) {
                const scale = Math.sqrt(25_000_000 / pixels);
                const canvas = document.createElement("canvas");
                canvas.width = img.width * scale;
                canvas.height = img.height * scale;
                canvas.getContext("2d").drawImage(img, 0, 0, canvas.width, canvas.height);
                canvas.toBlob((blob) => {
                    const resizedFile = new File([blob], file.name, { type: file.type });
                    eraseBg(resizedFile);  // send resized image
                }, file.type);
            } else {
                eraseBg(file);  // send original image
            }
        };

        const reader = new FileReader();
        reader.onload = (e) => {
            img.src = e.target.result;
        };

        reader.readAsDataURL(file);
    };

    return (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-12 items-center mb-16">
            {/* video banner left side */}
            <div className="order-2 md:order-1 flex justify-center">
                <div className="shadow-[0_25px_50px_-12px_rgba(0,0,0,0.15)] rounded-3xl overflow-hidden">
                    <video
                        src={assets.video_banner}
                        autoPlay
                        loop
                        muted
                        className="w-full max-w-[600px] h-auto object-cover"
                    />
                </div>
            </div>

            {/* text content right side */}
            <div className="order-1 md:order-2">
                <h1 className="text-4xl md:text-5xl font-bold text-gray-900 mb-6 leading-tight">
                    The fastest <span className="text-indigo-700">background eraser.</span>
                </h1>
                <p className="text-gray-600 mb-8 text-lg leading-relaxed">
                    Transform your photos with our background remover app! Highlight your subject and create a
                    transparent background, so you can place it in a variety of new designs and destinations. Try it now
                    and immerse your subject in a completely different environment!
                </p>

                <div>
                    <input
                        type="file"
                        accept="image/*"
                        id="upload1"
                        hidden
                        onChange={(e) => handleFileChange(e.target.files[0])}
                    />
                    <label
                        htmlFor="upload1"
                        className="bg-black text-white font-medium px-8 py-4 rounded-full hover:opacity-90 transition-transform hover:scale-105 text-lg"
                    >
                        Upload Your Image
                    </label>
                </div>
            </div>
        </div>
    );
};

export default Header;
