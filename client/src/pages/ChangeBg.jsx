import { useContext, useState, useEffect } from "react";
import { AppContext } from "../context/AppContext.jsx";
import { useNavigate } from "react-router-dom";

const presetBackgrounds = [
    "https://images.pexels.com/photos/1629236/pexels-photo-1629236.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
    "https://images.pexels.com/photos/376533/pexels-photo-376533.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
    "https://images.pexels.com/photos/1379636/pexels-photo-1379636.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
];

const checkerboardPattern =
    "data:image/svg+xml,%3csvg width='20' height='20' viewBox='0 0 20 20' xmlns='http://www.w3.org/2000/svg'%3e%3crect fill='%23ccc' fill-opacity='0.3' width='10' height='10'/%3e%3crect fill='%23fff' width='10' height='10' x='10' y='10'/%3e%3c/svg%3e";

const MAX_PREVIEW_WIDTH = 450;
const MAX_PREVIEW_HEIGHT = 450;

const ChangeBg = () => {
    const { resultImage, backgroundImage, setBackgroundImage } = useContext(AppContext);
    const navigate = useNavigate();
    const [imageSize, setImageSize] = useState({ width: 300, height: 300 });
    const [customBg, setCustomBg] = useState(null);

    useEffect(() => {
        if (!resultImage) return;
        const img = new Image();
        img.src = resultImage;
        img.onload = () => {
            let { width, height } = img;
            const widthRatio = MAX_PREVIEW_WIDTH / width;
            const heightRatio = MAX_PREVIEW_HEIGHT / height;
            const ratio = Math.min(widthRatio, heightRatio, 1);
            setImageSize({
                width: Math.round(width * ratio),
                height: Math.round(height * ratio),
            });
        };
    }, [resultImage]);

    useEffect(() => {
        if (!backgroundImage) setBackgroundImage(checkerboardPattern);
    }, [backgroundImage, setBackgroundImage]);

    const selectBackground = (bg) => {
        setCustomBg(null);
        setBackgroundImage(bg);
    };

    const onUploadBackground = (e) => {
        const file = e.target.files[0];
        if (file) {
            const url = URL.createObjectURL(file);
            setCustomBg(url);
            setBackgroundImage(url);
        }
    };

    const handleDownload = async () => {
        if (!resultImage || !backgroundImage) return;

        const canvas = document.createElement("canvas");
        const ctx = canvas.getContext("2d");

        canvas.width = imageSize.width;
        canvas.height = imageSize.height;

        const loadImage = (src) =>
            new Promise((resolve, reject) => {
                const img = new Image();
                img.crossOrigin = "anonymous";
                img.onload = () => resolve(img);
                img.onerror = (e) => reject(e);
                img.src = src;
            });

        try {
            if (typeof backgroundImage === "string" && backgroundImage.startsWith("#")) {
                ctx.fillStyle = backgroundImage;
                ctx.fillRect(0, 0, canvas.width, canvas.height);
            } else {
                const bgImg = await loadImage(backgroundImage);
                const scale = Math.max(
                    canvas.width / bgImg.width,
                    canvas.height / bgImg.height
                );
                const bgWidth = bgImg.width * scale;
                const bgHeight = bgImg.height * scale;
                const bgX = (canvas.width - bgWidth) / 2;
                const bgY = (canvas.height - bgHeight) / 2;
                ctx.drawImage(bgImg, bgX, bgY, bgWidth, bgHeight);
            }

            const fgImg = await loadImage(resultImage);
            ctx.drawImage(fgImg, 0, 0, canvas.width, canvas.height);

            canvas.toBlob((blob) => {
                if (!blob) return;
                const url = URL.createObjectURL(blob);
                const a = document.createElement("a");
                a.href = url;
                a.download = "image-with-background.png";
                a.click();
                URL.revokeObjectURL(url);
            }, "image/png");
        } catch (err) {
            alert("Failed to download image: " + err.message);
        }
    };

    return (
        <div className="min-h-screen bg-gray-50 py-12 px-6 flex flex-col items-center">
            <h1 className="text-4xl font-bold mb-12 text-gray-900">Change Background</h1>

            {/* Previews */}
            <div
                className="flex flex-col md:flex-row gap-10 justify-center items-start"
                style={{ maxWidth: MAX_PREVIEW_WIDTH * 2 + 80 }}
            >
                {/* Transparent Preview */}
                <div className="flex flex-col items-center gap-2">
                    <span className="text-gray-700 font-medium">Original Transparent Image</span>
                    <div
                        className="rounded-lg shadow-md border border-gray-300 overflow-hidden"
                        style={{
                            width: imageSize.width,
                            height: imageSize.height,
                            backgroundImage: `url(${checkerboardPattern})`,
                            backgroundSize: "40px 40px",
                            backgroundRepeat: "repeat",
                            backgroundPosition: "0 0, 20px 20px",
                        }}
                    >
                        {resultImage ? (
                            <img
                                src={resultImage}
                                alt="Removed background"
                                className="object-contain"
                                style={{ width: imageSize.width, height: imageSize.height }}
                            />
                        ) : (
                            <div className="flex justify-center items-center h-full text-gray-400 italic">
                                No image loaded
                            </div>
                        )}
                    </div>
                </div>

                {/* With New Background */}
                <div className="flex flex-col items-center gap-2">
                    <span className="text-gray-700 font-medium">Image with New Background</span>
                    <div
                        className="rounded-lg shadow-md border border-gray-300 relative overflow-hidden"
                        style={{
                            width: imageSize.width,
                            height: imageSize.height,
                            backgroundColor:
                                typeof backgroundImage === "string" && backgroundImage.startsWith("#")
                                    ? backgroundImage
                                    : undefined,
                            backgroundImage:
                                typeof backgroundImage === "string" && !backgroundImage.startsWith("#")
                                    ? `url(${backgroundImage})`
                                    : undefined,
                            backgroundSize: "cover",
                            backgroundPosition: "center",
                        }}
                    >
                        {resultImage ? (
                            <img
                                src={resultImage}
                                alt="With new background"
                                className="object-contain relative"
                                style={{ width: imageSize.width, height: imageSize.height }}
                            />
                        ) : (
                            <div className="flex justify-center items-center h-full text-gray-400 italic">
                                No image loaded
                            </div>
                        )}
                        <div
                            className="absolute inset-0 pointer-events-none"
                            style={{
                                backgroundImage: `url(${checkerboardPattern})`,
                                opacity: 0.15,
                                mixBlendMode: "multiply",
                            }}
                        />
                    </div>
                </div>
            </div>

            {/* Background Options */}
            <div className="mt-12 w-full max-w-4xl">
                <h2 className="text-xl font-semibold mb-4 text-gray-800">Choose Background</h2>
                <div className="flex items-center space-x-5 overflow-x-auto pb-2">
                    {presetBackgrounds.map((bg, i) => (
                        <button
                            key={i}
                            onClick={() => selectBackground(bg)}
                            className={`w-28 h-20 rounded-lg border-4 shrink-0 transition ${
                                backgroundImage === bg
                                    ? "border-indigo-600"
                                    : "border-transparent hover:border-gray-400"
                            }`}
                            style={{
                                backgroundImage: `url(${bg})`,
                                backgroundSize: "cover",
                                backgroundPosition: "center",
                            }}
                            aria-label={`Select background ${i + 1}`}
                        />
                    ))}

                    <label
                        htmlFor="upload-bg"
                        className="w-28 h-20 rounded-lg border-4 border-dashed border-gray-400 flex justify-center items-center cursor-pointer text-gray-400 hover:border-indigo-600 hover:text-indigo-600 shrink-0 select-none font-bold text-4xl"
                        title="Upload custom background"
                    >
                        +
                        <input
                            id="upload-bg"
                            type="file"
                            accept="image/*"
                            className="hidden"
                            onChange={onUploadBackground}
                        />
                    </label>

                    <div className="flex items-center space-x-3 shrink-0 ml-6">
                        <label htmlFor="color-picker" className="text-gray-700 font-medium select-none">
                            Color:
                        </label>
                        <input
                            id="color-picker"
                            type="color"
                            className="w-10 h-10 rounded border cursor-pointer"
                            onChange={(e) => selectBackground(e.target.value)}
                            value={
                                typeof backgroundImage === "string" && backgroundImage.startsWith("#")
                                    ? backgroundImage
                                    : "#ffffff"
                            }
                            title="Pick a background color"
                        />
                    </div>
                </div>
            </div>

            {/* Action Buttons */}
            <div className="mt-12 flex gap-8">
                <button
                    onClick={() => navigate("/")}
                    className="py-3 px-8 rounded border border-gray-400 hover:bg-gray-100 transition font-semibold"
                >
                    Upload New Image
                </button>

                <button
                    onClick={handleDownload}
                    disabled={!resultImage}
                    className="py-3 px-8 rounded bg-indigo-600 hover:bg-indigo-700 disabled:bg-indigo-300 text-white font-semibold transition"
                >
                    Download Image
                </button>
            </div>
        </div>
    );
};

export default ChangeBg;
