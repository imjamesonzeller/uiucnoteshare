import { createContext, useContext, useEffect, useState } from "react";

const AuthContext = createContext(null)

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null)
    const [loading, setLoading] = useState(true)

    const baseApiUrl = "http://localhost:8080"

    useEffect(() => {
        const jwt = sessionStorage.getItem('jwt')

        if (!jwt) {
            setUser(null)
            setLoading(false)
            return
        }

        fetch(`${baseApiUrl}/users/me`, {
            headers: {
                Authorization: `Bearer ${jwt}`
            },
        })
            .then(res => (res.ok ? res.json() : Promise.reject()))
            .then(data => setUser(data))
            .catch(() => {
                sessionStorage.removeItem('jwt')
                setUser(null)
            })
            .finally(() => setLoading(false))
        }, [])

        return (
            <AuthContext.Provider value={{ user, setUser, loading }}>
                {children}
            </AuthContext.Provider>
        )
}

export const useAuth = () => useContext(AuthContext)