import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './styles/index.css'
import App from './App.jsx'
import { Providers } from './Providers.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
      <Providers>
          <App />
      </Providers>
  </StrictMode>,
)
