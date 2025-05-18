import Header from "../components/Header.jsx";
import BgRemovalSteps from "../components/BgRemovalSteps.jsx";
import BgSlider from "../components/BgSlider.jsx";
import Pricing from "../components/Pricing.jsx";
import Testimonials from "../components/Testimonials.jsx";
import TryNow from "../components/TryNow.jsx";

const Home = () => {
    return (
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16 font-['Outfit']">

            {/*{Hero section }*/}
            <Header/>
            {/*{bg removal steps section }*/}
            <BgRemovalSteps/>
            {/*{bg removal slider section }*/}
            <BgSlider/>
            {/*{buy credits plan section }*/}
            <Pricing/>
            {/*{User testimonials section }*/}
            <Testimonials/>
            {/*{Try Now section }*/}
            <TryNow/>
        </div>
    )
}
export default Home;