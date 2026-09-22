import { Router } from 'express';
import { getServerIp, getServerTime, getName } from '../controllers/dashboard';
import { requireAuth } from '../middleware/auth';

export const dashboardRouter = Router();
// dashboardRouter.get('/dashboard', requireAuth, getDashboard);

dashboardRouter.get('/ip', requireAuth, getServerIp);

dashboardRouter.get('/time', requireAuth, getServerTime);

dashboardRouter.get('/name', requireAuth, getName);
