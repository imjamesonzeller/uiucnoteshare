import {Button, NavbarContent} from "@heroui/react";
import React from "react";
import {useAuth} from "../context/AuthContext.jsx";
import {useMsal} from "@azure/msal-react";
import {useNavigate} from "react-router-dom";

const AuthSection = () => {
    const { user, setUser, loading, setLoading } = useAuth();
    const { instance } = useMsal()
    const navigate = useNavigate()

    const baseApiUrl = "http://localhost:8080"

    const handleLogin = async () => {
        setLoading(true);

        try {
            // Step 1: Microsoft login
            await instance.loginPopup();
            const result = await instance.acquireTokenSilent({ scopes: [] });
            const idToken = result.idToken;

            // Step 2: Send token to backend for exchange
            const authRes = await fetch(`${baseApiUrl}/auth/oauth/microsoft`, {
                method: 'POST',
                headers: {
                    Authorization: `Bearer ${idToken}`
                },
                credentials: 'include',
            });

            const { token } = await authRes.json();
            sessionStorage.setItem('jwt', token);

            // Step 3: Fetch user profile
            const userRes = await fetch(`${baseApiUrl}/users/me`, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });

            const userData = await userRes.json();
            setUser(userData);
        } catch (err) {
            console.error("Login error", err);
        } finally {
            setLoading(false);
        }
    };

    const handleLogout = () => {
        sessionStorage.removeItem('jwt')
        setUser(null)
        navigate('/')
    }

    return (
        <NavbarContent justify="end">
            {!user ? (
                <Button
                    color="secondary"
                    radius="large"
                    isLoading={loading}
                    onPress={handleLogin}
                >
                    Login
                </Button>
            ) : (
                <div className="flex items-center space-x-3">
                        <span className={"hidden sm:inline text-primary-foreground"}>
                            {user.firstName} {user.lastName}
                        </span>
                    <Button
                        color="secondary"
                        radius={"large"}
                        isLoading={loading}
                        onPress={handleLogout}
                    >
                        Logout
                    </Button>
                </div>
            )}
        </NavbarContent>
    )
}

export default AuthSection;