import { useNavigate } from 'react-router-dom'

const Footer = () => {
    const navigate = useNavigate()

    return (
        <footer className="bg-[#13294B] text-white py-6 mt-12">
            <div className="max-w-screen-xl mx-auto px-4 grid grid-cols-1 sm:grid-cols-3 items-center text-sm gap-y-4 text-center sm:text-left">

                {/* Left: Links */}
                <div className="flex justify-center sm:justify-start gap-6">
                    <a
                        href="https://github.com/imjamesonzeller"
                        target="_blank"
                        rel="noopener noreferrer"
                        className="hover:text-orange-400 transition-colors"
                    >
                        GitHub
                    </a>
                    <button
                        onClick={() => navigate('/privacy')}
                        className="hover:text-orange-400 transition-colors cursor-pointer"
                    >
                        Privacy
                    </button>
                    <button
                        onClick={() => navigate('/contact')}
                        className="hover:text-orange-400 transition-colors cursor-pointer"
                    >
                        Contact
                    </button>
                </div>

                {/* Center: Copyright */}
                <div className="text-center text-xs sm:text-sm">
                    © {new Date().getFullYear()} UIUC Note Share. All rights reserved.
                </div>

                {/* Right: Credit */}
                <div className="flex justify-center sm:justify-end">
                    <p>Built for UIUC by Students ❤️</p>
                </div>
            </div>
        </footer>
    )
}

export default Footer