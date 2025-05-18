import { useContext, useState } from "react";
import { AppContext } from "../context/AppContext.jsx";
import {assets} from "../assets/assets.js";

const TryNow = () => {
    const { eraseBg } = useContext(AppContext);
    const [preview, setPreview] = useState(null);

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
                    setPreview(URL.createObjectURL(resizedFile));
                    eraseBg(resizedFile);  // send resized image
                }, file.type);
            } else {
                setPreview(URL.createObjectURL(file));
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
        <div className="flex flex-col items-center justify-center bg-white px-4">
            <h2 className="text-3xl md:text-4xl font-bold text-gray-900 mb-7 text-center">
                Remove Image Background.
            </h2>
            <p className="text-gray-500 mb-8 text-center">
                Get a transparent background for any image
            </p>
            <div className="bg-white rounded-2xl shadow-lg p-10 flex flex-col items-center space-y-4 w-full max-w-md">

                <label htmlFor="upload2" className="cursor-pointer">
                    {preview ? (
                        <img
                            src={preview}
                            alt="Preview"
                            className="w-48 h-48 object-contain rounded-lg border"
                        />
                    ) : (
                        <img
                            src={assets.uploadicon}
                            alt="Upload Placeholder"
                            className="w-48 h-48 object-contain rounded-lg border"
                        />
                    )}
                </label>

                <p className="text-gray-400 text-sm -mt-2">
                    Image should be less than 25 MB and 25 megapixels
                </p>

                <input
                    type="file"
                    id="upload2"
                    hidden
                    accept="image/*"
                    onChange={(e) => handleFileChange(e.target.files[0])}
                />

                <label
                    htmlFor="upload2"
                    className="bg-indigo-600 hover:bg-blue-700 text-white font-semibold py-3 px-6 rounded-full text-lg cursor-pointer"
                >
                    Upload Image
                </label>

                <p className="text-gray-500 text-sm text-center">
                    or drop a file, paste image or <a href="#" className="text-blue-500 underline">URL</a>
                </p>
            </div>
        </div>
    );
};

export default TryNow;
