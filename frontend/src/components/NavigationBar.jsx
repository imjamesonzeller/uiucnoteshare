import { useAuth } from "../context/AuthContext"
import { useNavigate } from "react-router-dom"
import { useMsal } from '@azure/msal-react'
import blockI from '../assets/Illinois_Block_I.png'

const NavigationBar = () => {
  const { user, setUser } = useAuth()
  const { instance } = useMsal()
  const navigate = useNavigate()

  const baseApiUrl = "http://localhost:8080"

  const handleLogin = () => {
    instance.loginPopup()
      .then(() => instance.acquireTokenSilent({ scopes: [] }))
      .then(result => {
        const idToken = result.idToken
        return fetch(`${baseApiUrl}/auth/oauth/microsoft`, {
          method: 'POST',
          headers: {
            Authorization: `Bearer ${idToken}`
          },
          credentials: 'include',
        })
      })
      .then(res => res.json())
      .then(({ token }) => {
        sessionStorage.setItem('jwt', token)
        return fetch(`${baseApiUrl}/users/me`, {
          headers: {
            Authorization: `Bearer ${token}`,
          }
        })
      })
      .then(res => res.json())
      .then(data => setUser(data))
      .catch(err => console.error('Login error', err))
  }

  const handleLogout = () => {
    sessionStorage.removeItem('jwt')
    setUser(null)
    navigate('/')
  }

  return (
    <header className="sticky top-0 z-50 bg-[#13294B] text-white shadow-md">
      <div className="max-w-screen-xl mx-auto px-4 py-3 flex items-center justify-between">

        {/* Logo */}
        <div
          onClick={() => navigate('/')}
          className="flex items-center space-x-2"
        >
          <img src={blockI} alt="UIUC Block I" className="w-6 pr-1" />
          <span className="text-xl font-bold tracking-tight cursor-pointer">UIUC Note Share</span>
        </div>

        {/* Nav links */}
        <nav className="hidden md:flex space-x-6 text-sm font-medium">
          <button onClick={() => navigate('/')} className="hover:text-orange-400 transition-colors cursor-pointer">
            Browse Courses
          </button>
          <button onClick={() => alert('About page coming soon')} className="hover:text-orange-400 transition-colors cursor-pointer">
            About
          </button>
        </nav>

        {/* Right side: auth controls */}
        <div className="text-sm">
          {!user ? (
            <button
              onClick={handleLogin}
              className="bg-orange-500 hover:bg-orange-600 text-white px-4 py-1.5 rounded-full transition-colors cursor-pointer"
            >
              Login with Microsoft
            </button>
          ) : (
            <div className="flex items-center space-x-3">
              <span className="hidden sm:inline">
                {user.firstName} {user.lastName}
              </span>
              <button
                onClick={handleLogout}
                className="bg-white text-[#13294B] px-3 py-1.5 rounded-full hover:bg-gray-200 transition-colors cursor-pointer"
              >
                Logout
              </button>
            </div>
          )}
        </div>
      </div>
    </header>
  )
}

export default NavigationBar