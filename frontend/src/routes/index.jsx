import { createBrowserRouter } from 'react-router-dom';
import LoginPage from '../pages/LoginPage';
import LoginCallbackPage from '../pages/LoginCallbackPage';

const router = createBrowserRouter([
  {
    path: '/',
    element: <LoginPage />,
  },
  {
    path: '/login',
    element: <LoginPage />,
  },
  {
    path: '/login/callback',
    element: <LoginCallbackPage />,
  },
]);

export default router;