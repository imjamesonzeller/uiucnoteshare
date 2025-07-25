import React from "react";
import {
    Navbar,
    NavbarBrand,
    NavbarContent,
    NavbarItem,
    NavbarMenuToggle,
    NavbarMenu,
    NavbarMenuItem,
    Link,
    Button,
} from "@heroui/react";
import {useLocation, useNavigate} from "react-router-dom";
import { useAuth } from "../context/AuthContext"
import BlockILogo from "../assets/Illinois_Block_I.png";
import { useMsal } from '@azure/msal-react'

export const BlockI = () => {
    return (
        <img src={BlockILogo} alt="Illinois Logo" className="h-9 w-auto" />
    );
};

export default function HeroUINavBar() {
    const [isMenuOpen, setIsMenuOpen] = React.useState(false);

    const menuItems = [
        "Profile",
        "Dashboard",
        "Activity",
        "Analytics",
        "System",
        "Deployments",
        "My Settings",
        "Team Settings",
        "Help & Feedback",
        "Log Out",
    ];

    const location = useLocation();
    const isActive = path => location.pathname === path;

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
        <Navbar onMenuOpenChange={setIsMenuOpen} className="bg-primary" maxWidth={"full"}>
            <NavbarContent justify={"start"}>
                <NavbarMenuToggle
                    aria-label={isMenuOpen ? "Close menu" : "Open menu"}
                    className="sm:hidden text-primary-foreground"
                />
                <NavbarBrand className="text-primary-foreground">
                    <div className="flex items-center space-x-2">
                        <BlockI />
                        <p className="font-bold text-inherit">UIUC Note Share</p>
                    </div>
                </NavbarBrand>
            </NavbarContent>

            <NavbarContent className="hidden sm:flex gap-4 text-primary-foreground" justify="center">
                <NavbarItem>
                    <Link
                        aria-current="page"
                        color="primary-foreground"
                        className={isActive ? "underline underline-offset-4 font-semibold" : ""}
                        href="#"
                    >
                        Dashboard
                    </Link>
                </NavbarItem>
                <NavbarItem>
                    <Link color="primary-foreground" href="#">
                        Browse Courses
                    </Link>
                </NavbarItem>
                <NavbarItem>
                    <Link color="primary-foreground" href="#">
                        About
                    </Link>
                </NavbarItem>
            </NavbarContent>

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

            <NavbarMenu>
                {menuItems.map((item, index) => (
                    <NavbarMenuItem key={`${item}-${index}`}>
                        <Link
                            className="w-full"
                            color={
                                index === 2 ? "primary" : index === menuItems.length - 1 ? "danger" : "foreground"
                            }
                            href="#"
                            size="lg"
                        >
                            {item}
                        </Link>
                    </NavbarMenuItem>
                ))}
            </NavbarMenu>
        </Navbar>
    );
}

