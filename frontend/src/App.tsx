import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'sonner';
import { AuthProvider } from './context/AuthContext';
import PrivateRoute from './components/PrivateRoute';
import AdminRoute from './components/AdminRoute';
import LoginPage from './pages/LoginPage';
import MainPage from './pages/MainPage';
import SelectPage from './pages/SelectPage';
import LearnPage from './pages/LearnPage';
import QuizPage from './pages/QuizPage';
import AdminPage from './pages/AdminPage';
import RankingPage from './pages/RankingPage';
import PvpPage from './pages/PvpPage';
import LoginCallbackPage from './pages/LoginCallbackPage';

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Toaster position="top-center" />
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/login/callback" element={<LoginCallbackPage />} />
          <Route element={<PrivateRoute />}>
            <Route path="/" element={<MainPage />} />
            <Route path="/select" element={<SelectPage />} />
            <Route path="/learn/:stageId" element={<LearnPage />} />
            <Route path="/quiz/:stageId" element={<QuizPage />} />
            <Route path="/ranking" element={<RankingPage />} />
            <Route path="/pvp" element={<PvpPage />} />
            <Route path="/pvp/:roomId" element={<PvpPage />} />
          </Route>
          <Route element={<AdminRoute />}>
            <Route path="/admin" element={<AdminPage />} />
          </Route>
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}
