import logo from "./logo.png";
import video_banner from "./video_banner.mp4";
import uploadicon from "./UploadImageIcon.gif";
// People
import people_org from "./people-org.png";
import people from "./people.png";

// Products
import products_org from "./products-org.png";
import products from "./products.png";

// Animals
import animals_org from "./animals-org.png";
import animals from "./animals.png";

// Cars
import cars_org from "./cars-org.png";
import cars from "./cars.png";

// Graphics
import graphics_org from "./graphics-org.png";
import graphics from "./graphics.png";

// credits
import credits from "./credits.png";
export const assets = {
    logo,
    video_banner,
    slider: {
        People: {
            original: people_org,
            processed: people,
        },
        Products: {
            original: products_org,
            processed: products,
        },
        Animals: {
            original: animals_org,
            processed: animals,
        },
        Cars: {
            original: cars_org,
            processed: cars,
        },
        Graphics: {
            original: graphics_org,
            processed: graphics,
        },
    },
    credits,
    uploadicon,
};

export const steps = [
    {
        step: "Step 1",
        title: "Select an image",
        description:
            `First, choose the image you want to remove background from by clicking on Start from a photo. Your image format can be PNG or JPG. We support all image dimensions.`
    },
    {
        step: "Step 2",
        title: "Let magic remove the background",
        description:
            `Our tool automatically removes the background from your image. Next, you can choose a background color. Our most popular options are white and transparent backgrounds, but you can pick any color you like.`
    },
    {
        step: "Step 3",
        title: "Download your image",
        description: `After selecting a new background color, download your photo and you're done! You can also save your picture in the Photoroom App by creating an account.`
    },
];

export const categories = ["People", "Products", "Animals", "Cars", "Graphics"];

export const plans = [
    {
        id: 'Basic',
        name: 'Basic Package',
        price: 499,
        credits: "100 credits",
        description: "Best for Personal Use",
        popular: false
    },
    {
        id: 'Premium',
        name: 'Premium Package',
        price: 899,
        credits: "250 credits",
        description: "Best for Business Use",
        popular: true
    },
    {
        id: 'Ultimate',
        name: 'Ultimate Package',
        price: 1499,
        credits: "1000 credits",
        description: "Best for enterprise Use",
        popular: false
    },
]

export const testimonials = [
    {
        id: 1,
        quote: "We are impressed by AI and think it's the best choice on the market. ",
        author: "Anthony Walker",
        handle: "@_webarchitect"
    },
    {
        id: 2,
        quote: "This platform saved us hours of manual editing. It's fast, reliable, and super easy to use!",
        author: "Sophia Hernandez",
        handle: "@sophiacreates"
    },
    {
        id: 3,
        quote: "Absolutely love the background removal feature. It’s become an essential part of our design workflow.",
        author: "Liam Chen",
        handle: "@liamuxdesign"
    }

];

export const FOOTER_CONSTANTS = [
    {
        url: "https://facebook.com",
        logo: "https://img.icons8.com/fluent/30/000000/facebook-new.png"
    },
    {
        url: "https://github.com/mynk4169",
        logo: "https://img.icons8.com/fluent/30/000000/github.png"
    },
    {
        url: "https://www.linkedin.com/in/mayank-thakre-24753a25a/?originalSubdomain=in",
        logo: "https://img.icons8.com/fluent/30/000000/linkedin-2.png"
    },
    {
        url: "https://instagram.com",
        logo: "https://img.icons8.com/fluent/30/000000/instagram-new.png"
    }
];
