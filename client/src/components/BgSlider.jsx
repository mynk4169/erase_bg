import { useState } from "react";
import { assets, categories } from "../assets/assets.js";

const BgSlider = () => {
    const [sliderPosition, setSliderPosition] = useState(50);
    const [activeCategory, setActiveCategory] = useState("People");

    const handleSliderChange = (e) => {
        setSliderPosition(e.target.value);
    };

    // Access images using assets.slider[activeCategory]
    const { original, processed } = assets.slider[activeCategory];

    return (
        <div className="mb-16 relative">
            {/* Section title */}
            <h2 className="text-3xl md:text-4xl font-bold text-gray-900 mb-12 text-center">
                Stunning Quality.
            </h2>

            {/* Category selector */}
            <div className="flex justify-center mb-10 flex-wrap">
                <div className="inline-flex gap-4 bg-gray-100 p-2 rounded-full flex-wrap justify-center">
                    {categories.map((category) => (
                        <button
                            key={category}
                            onClick={() => setActiveCategory(category)}
                            className={`px-6 py-2 rounded-full font-medium ${
                                activeCategory === category
                                    ? "bg-white text-blue-900 font-semibold shadow-sm"
                                    : "text-gray-500 hover:bg-gray-300"
                            }`}
                        >
                            {category}
                        </button>
                    ))}
                </div>
            </div>

            {/* Image comparison slider */}
            <div className="relative w-full max-w-4xl mx-auto rounded-xl shadow-lg aspect-video overflow-hidden">
                {/* Background removed image (bottom layer) */}
                <img
                    src={processed}
                    alt="Processed"
                    className="w-full h-full object-contain absolute top-0 left-0"
                />

                {/* Original image (top layer with clipPath) */}
                <img
                    src={original}
                    alt="Original"
                    className="w-full h-full object-contain absolute top-0 left-0"
                    style={{
                        clipPath: `inset(0 ${100 - sliderPosition}% 0 0)`,
                    }}
                />

                {/* Slider input */}
                <input
                    type="range"
                    min={0}
                    max={100}
                    value={sliderPosition}
                    onChange={handleSliderChange}
                    className="absolute z-10 top-1/2 left-0 transform -translate-y-1/2 w-full appearance-none bg-transparent h-1 focus:outline-none slider"
                />
            </div>
        </div>
    );
};

export default BgSlider;
