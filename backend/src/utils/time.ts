export function formatWithGmtOffset(date: Date) {
  const pad = (n: any) => String(n).padStart(2, '0');
  const hh = pad(date.getHours());
  const mm = pad(date.getMinutes());
  const ss = pad(date.getSeconds());

  const offsetMin = -date.getTimezoneOffset(); // JS reports it inverted
  const sign = offsetMin >= 0 ? '+' : '-';
  const absOffset = Math.abs(offsetMin);
  const offHH = pad(Math.floor(absOffset / 60));
  const offMM = pad(absOffset % 60);

  return `${hh}:${mm}:${ss} GMT${sign}${offHH}:${offMM}`;
}
