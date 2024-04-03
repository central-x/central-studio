import axios from 'axios';

export const http = axios.create({
  timeout: 10000,
  headers: {
    Accept: 'application/json'
  },
  validateStatus: function (status: number) {
    if (status === 401) {
      // 未登录
      // window.location.reload();
    }
    return true;
  }
});
