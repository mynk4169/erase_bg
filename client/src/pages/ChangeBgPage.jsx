import { useState, useContext } from "react";
import { AppContext } from "../context/AppContext";

const presetBackgrounds = [
    "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=400&q=80",
    "https://images.unsplash.com/photo-1494526585095-c41746248156?auto=format&fit=crop&w=400&q=80",
    "https://images.unsplash.com/photo-1500534623283-312aade485b7?auto=format&fit=crop&w=400&q=80",
    "https://images.unsplash.com/photo-1517411032314-4e2e7e8c8c5b?auto=format&fit=crop&w=400&q=80", // Urban Mood
    "https://images.unsplash.com/photo-1521747116042-5e1e5e1e5e1e?auto=format&fit=crop&w=400&q=80", // Minimalist Design
    "https://images.unsplash.com/photo-1532082453-8f8f8f8f8f8f?auto=format&fit=crop&w=400&q=80", // Tech-Inspired
    "https://images.unsplash.com/photo-1542082453-8f8f8f8f8f8f?auto=format&fit=crop&w=400&q=80", // Futuristic Lines
    "https://images.unsplash.com/photo-1552082453-8f8f8f8f8f8f?auto=format&fit=crop&w=400&q=80"  // Geometric Patterns
];


const colorPalette = ["#ffffff", "#f8fafc", "#facc15", "#60a5fa", "#10b981", "#ef4444", "#9333ea", "#000000"];

const removedBgPattern =
    "data:image/svg+xml,%3csvg width='20' height='20' viewBox='0 0 20 20' xmlns='http://www.w3.org/2000/svg'%3e%3crect fill='%23ccc' fill-opacity='0.4' width='10' height='10'/%3e%3crect fill='%23fff' width='10' height='10' x='10' y='10'/%3e%3c/svg%3e";

const ChangeBgPage = () => {
    const { eraseBg } = useContext(AppContext);
    const [originalFile, setOriginalFile] = useState(null);
    const [resultImage, setResultImage] = useState(null);
    const [backgroundImage, setBackgroundImage] = useState(removedBgPattern);

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
                canvas.toBlob(
                    (blob) => {
                        const resizedFile = new File([blob], file.name, { type: file.type });
                        setOriginalFile(resizedFile);
                        eraseImage(resizedFile);
                    },
                    file.type,
                    1
                );
            } else {
                setOriginalFile(file);
                eraseImage(file);
            }
        };

        const reader = new FileReader();
        reader.onload = (e) => (img.src = e.target.result);
        reader.readAsDataURL(file);
    };

    const eraseImage = async (file) => {
        const erased = await eraseBg(file);
        setResultImage(erased);
    };

    const handleBgSelect = (bg) => {
        setBackgroundImage(bg);
    };

    const handleUploadCustomBg = (e) => {
        const file = e.target.files[0];
        if (file) {
            const objectUrl = URL.createObjectURL(file);
            setBackgroundImage(objectUrl);
        }
    };

    const handleDownload = async () => {
        if (!resultImage || !backgroundImage) return;

        const canvas = document.createElement("canvas");
        const ctx = canvas.getContext("2d");

        const fgImg = new Image();
        fgImg.crossOrigin = "anonymous";
        fgImg.src = resultImage;

        await new Promise((resolve) => (fgImg.onload = resolve));

        canvas.width = fgImg.width;
        canvas.height = fgImg.height;

        if (backgroundImage.startsWith("http") || backgroundImage.startsWith("data:image")) {
            const bgImg = new Image();
            bgImg.crossOrigin = "anonymous";
            bgImg.src = backgroundImage;
            await new Promise((resolve) => (bgImg.onload = resolve));
            ctx.drawImage(bgImg, 0, 0, canvas.width, canvas.height);
        } else {
            ctx.fillStyle = backgroundImage;
            ctx.fillRect(0, 0, canvas.width, canvas.height);
        }

        ctx.drawImage(fgImg, 0, 0, canvas.width, canvas.height);

        canvas.toBlob((blob) => {
            if (!blob) return;
            const url = URL.createObjectURL(blob);
            const link = document.createElement("a");
            link.href = url;
            link.download = "image-with-background.png";
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            URL.revokeObjectURL(url);
        }, "image/png");
    };

    return (
        <div className="mx-4 my-6 lg:mx-44">
            <h1 className="text-2xl font-bold mb-6">Upload and Change Background</h1>

            {/* Upload + Preview Section */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-8 mb-6">
                {/* Upload Image */}
                <div className="flex justify-center items-center h-[400px] border rounded-md">
                    <div className="flex flex-col items-center">
                        <label
                            htmlFor="upload-image"
                            className="bg-black text-white font-medium px-8 py-4 rounded-full cursor-pointer hover:opacity-90 transition mb-4"
                        >
                            Upload Your Image
                        </label>
                        <input
                            type="file"
                            accept="image/*"
                            id="upload-image"
                            hidden
                            onChange={(e) => handleFileChange(e.target.files[0])}
                        />
                        {originalFile && (
                            <p className="text-sm text-gray-500 text-center">{originalFile.name}</p>
                        )}
                    </div>
                </div>

                {/* Result Preview */}
                <div
                    className="relative flex justify-center items-center w-full h-[400px] border rounded-md overflow-hidden"
                    style={{
                        background: backgroundImage.startsWith("#")
                            ? backgroundImage
                            : `url(${backgroundImage})`,
                        backgroundSize: "cover",
                        backgroundPosition: "center",
                    }}
                >
                    {resultImage ? (
                        <img
                            src={resultImage}
                            alt="Result"
                            className="max-h-full max-w-full object-contain relative z-10"
                        />
                    ) : (
                        <p className="text-gray-400 text-center absolute">No image uploaded yet</p>
                    )}
                    <div
                        className="absolute inset-0"
                        style={{ backgroundImage: `url(${removedBgPattern})`, opacity: 0.3, zIndex: 0 }}
                    />
                </div>
            </div>

            {/* Background Selector */}
            {resultImage && (
                <>
                    <p className="mb-2 font-medium">Select a Background:</p>
                    <div className="flex space-x-4 overflow-x-auto pb-2 mb-6">
                        {/* Removed background as option */}
                        <button
                            onClick={() => handleBgSelect(removedBgPattern)}
                            className={`w-24 h-16 rounded-md border-4 transition-all ${
                                backgroundImage === removedBgPattern
                                    ? "border-indigo-600"
                                    : "border-transparent hover:border-gray-400"
                            }`}
                            style={{
                                backgroundImage: `url(${removedBgPattern})`,
                                backgroundSize: "cover",
                            }}
                            aria-label="Removed background"
                        />

                        {/* Preset backgrounds */}
                        {presetBackgrounds.map((bg, idx) => (
                            <button
                                key={idx}
                                onClick={() => handleBgSelect(bg)}
                                className={`w-24 h-16 rounded-md border-4 transition-all ${
                                    backgroundImage === bg
                                        ? "border-indigo-600"
                                        : "border-transparent hover:border-gray-400"
                                }`}
                                style={{
                                    backgroundImage: `url(${bg})`,
                                    backgroundSize: "cover",
                                    backgroundPosition: "center",
                                }}
                                aria-label={`Select background ${idx + 1}`}
                            />
                        ))}

                        {/* Color palette backgrounds */}
                        {colorPalette.map((color, idx) => (
                            <button
                                key={idx}
                                onClick={() => handleBgSelect(color)}
                                className={`w-24 h-16 rounded-md border-4 transition-all ${
                                    backgroundImage === color
                                        ? "border-indigo-600"
                                        : "border-transparent hover:border-gray-400"
                                }`}
                                style={{ backgroundColor: color }}
                                aria-label={`Select color ${color}`}
                            />
                        ))}

                        {/* Custom background upload */}
                        <label
                            htmlFor="custom-bg"
                            className="w-24 h-16 rounded-md border-4 border-dashed border-gray-400 flex items-center justify-center cursor-pointer text-gray-400 hover:border-indigo-600 hover:text-indigo-600"
                            title="Upload custom background"
                        >
                            +
                            <input
                                type="file"
                                id="custom-bg"
                                accept="image/*"
                                className="hidden"
                                onChange={handleUploadCustomBg}
                            />
                        </label>
                    </div>

                    {/* Download Button */}
                    <div className="flex justify-center">
                        <button
                            onClick={handleDownload}
                            className="bg-green-600 text-white px-6 py-3 rounded hover:bg-green-700 transition disabled:bg-green-300"
                            disabled={!resultImage}
                        >
                            Download Final Image
                        </button>
                    </div>
                </>
            )}
        </div>
    );
};

export default ChangeBgPage;
