import { BrowserRouter, Routes, Route } from 'react-router-dom'
import PageLayout from './components/PageLayout'
// import HomePage from './pages/HomePage'
// import CoursePage from './pages/CoursePage'
import AuthCallback from './pages/AuthCallback'

const App = () => {
  return (
    <BrowserRouter>
      <PageLayout>
        {/* <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/courses/:id" element={<CoursePage />} />
          <Route path="/auth/callback" elemtn={<AuthCallback />} />
        </Routes> */}
        <h1>Hello World</h1>
      </PageLayout>
    </BrowserRouter>
  )
}

// const App = () => {
//   return <h1>Hello World</h1>
// }

export default App