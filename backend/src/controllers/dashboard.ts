import type { Request, Response } from 'express';
import { formatWithGmtOffset } from '../utils/time';
import { getServerPublicIp } from '../utils/ip';
  
export async function getServerIp(req: Request, res: Response) { 
  try {
    const serverPublicIp = await getServerPublicIp();

    res.json({
      serverPublicIp,
    });
  } catch (err) {
    res.status(500).json({ error: 'Failed to get server public IP' });
  }
}

export async function getServerTime(req: Request, res: Response) { 
  try {
    res.json({
      serverTime: formatWithGmtOffset(new Date()),
    });
  } catch (err) {
    res.status(500).json({ error: 'Failed to get server time' });
  }
}

export async function getName(req: Request, res: Response) { 
  try {
    res.json({
      firstName: "Khai",
      lastName: "Phan",
    });
  } catch (err) {
    res.status(500).json({ error: 'Failed to get server time' });
  }
}

