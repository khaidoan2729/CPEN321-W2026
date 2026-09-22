const axios = require('axios');

export async function getServerPublicIp() {
  // Uses axios to get current backend IP
  const { data } = await axios.get('https://api.ipify.org?format=json');
  return data.ip;
}
