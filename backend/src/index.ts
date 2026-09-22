import { createApp } from './app';
import { env } from './config/env';
import { connectToUpstream } from './wss/in/upstreamPixelSocket';
import { attachPixelWebSocketServer } from './wss/out/downstreamPixelSocket';

const app = createApp();

const server = app.listen(env.port, () => {
  console.log(`Server listening on port ${env.port}`);
});

attachPixelWebSocketServer(server);
connectToUpstream();

for (const signal of ['SIGINT', 'SIGTERM'] as const) {
  process.on(signal, () => {
    server.close(() => {
      process.exit(0);
    });
  });
}
