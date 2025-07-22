import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext"

const AuthCallback = () => {
    const { setUser } = useAuth()
    const navigate = useNavigate()

    const baseApiUrl = "http://localhost:8080"
    
    useEffect(() => {
        const urlParams = new URLSearchParams(window.location.search)
        const jwt = urlParams.get('token')
        if (jwt) {
            sessionStorage.setItem('jwt', jwt)

            fetch(`${baseApiUrl}/users/me`, {
                headers: {
                    Authorization: `Bearer ${jwt}`,
                },
            })
                .then(res => res.json())
                .then(data => {
                    setUser(data)
                    navigate('/')
                })
                .catch(() => {
                    sessionStorage.removeItem('jwt')
                    setUser(null)
                    navigate('/')
                })
         } else {
            navigate('/')
        }
    }, [])
    
    return <p>Logging you in...</p>
}

export default AuthCallback