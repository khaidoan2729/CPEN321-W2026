import { Router } from 'express';
import { googleSignIn } from '../controllers/auth';

export const authRouter = Router();
authRouter.post('/google', googleSignIn);
