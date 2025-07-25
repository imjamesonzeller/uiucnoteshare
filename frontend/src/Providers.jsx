import { HeroUIProvider } from '@heroui/react';
import { MsalProvider } from '@azure/msal-react';
import { PublicClientApplication, EventType } from '@azure/msal-browser';
import { msalConfig } from './authConfig.js';
import { AuthProvider } from './context/AuthContext.jsx';

const msalInstance = new PublicClientApplication(msalConfig);

// Set default account on page load
if (!msalInstance.getActiveAccount() && msalInstance.getAllAccounts().length > 0) {
    msalInstance.setActiveAccount(msalInstance.getAllAccounts()[0]);
}

// Listen for login event
msalInstance.addEventCallback((event) => {
    if (event.eventType === EventType.LOGIN_SUCCESS && event.payload.account) {
        msalInstance.setActiveAccount(event.payload.account);
    }
});

export const Providers = ({ children }) => (
    <HeroUIProvider theme="illini">
        <MsalProvider instance={msalInstance}>
            <AuthProvider>
                {children}
            </AuthProvider>
        </MsalProvider>
    </HeroUIProvider>
);