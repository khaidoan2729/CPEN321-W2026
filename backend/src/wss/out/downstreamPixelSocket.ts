import { WebSocketServer, WebSocket as WSClient } from 'ws';
import type { Server } from 'http';
import { pixelEmitter } from '../in/upstreamPixelSocket';

export function attachPixelWebSocketServer(server: Server) {
  const wss = new WebSocketServer({ server, path: '/ws/pixels' });

  wss.on('connection', (client: WSClient) => {
    const onPixel = (payload: string) => {
      if (client.readyState === WSClient.OPEN) {
        client.send(payload);
      }
    };

    pixelEmitter.on('pixel', onPixel);

    client.on('close', () => {
      pixelEmitter.off('pixel', onPixel);
    });
  });

  return wss;
}