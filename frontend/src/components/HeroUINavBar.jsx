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
} from "@heroui/react";
import {useLocation} from "react-router-dom";
import BlockILogo from "../assets/Illinois_Block_I.png";
import AuthSection from "./AuthSection.jsx";

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

            <AuthSection />

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

