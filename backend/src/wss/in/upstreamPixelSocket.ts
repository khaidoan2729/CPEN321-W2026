import WebSocket from 'ws';
import { EventEmitter } from 'events';

const COURSE_WS_URL = 'wss://8.229.22.124';

export const pixelEmitter = new EventEmitter();

let upstreamSocket: WebSocket | null = null;

export function connectToUpstream() {
  upstreamSocket = new WebSocket(COURSE_WS_URL, {
    rejectUnauthorized: false,
  });

  upstreamSocket.on('open', () => {
    console.log('Connected to course pixel WebSocket');
  });

  upstreamSocket.on('message', (data) => {
    pixelEmitter.emit('pixel', data.toString());
  });

  upstreamSocket.on('close', () => {
    // Upstream pixel socket closed, reconnecting in 3s
    setTimeout(connectToUpstream, 3000);
  });

  upstreamSocket.on('error', (err) => {
    console.error('Upstream pixel socket error:', err);
  });
}