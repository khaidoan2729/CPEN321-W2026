import express, { type Express } from 'express';
import { authRouter } from './routes/auth';
import { dashboardRouter } from './routes/dashboard';

export function createApp(): Express {
  const app = express();
  app.use(express.json());   

  app.get('/health', (_req, res) => {
    res.json({ status: 'ok' });
  });

  app.use('/auth', authRouter);

  app.use('/api/dashboard', dashboardRouter);

  app.use((_req, res) => {
    res.status(404).json({ error: 'Not Found' });
  });

  return app;
}
